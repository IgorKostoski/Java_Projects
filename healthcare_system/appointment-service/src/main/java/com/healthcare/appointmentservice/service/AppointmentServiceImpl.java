package com.healthcare.appointmentservice.service;

import com.healthcare.appointmentservice.client.DoctorServiceClient;
import com.healthcare.appointmentservice.client.PatientServiceClient;
import com.healthcare.appointmentservice.dto.AppointmentDTO;
import com.healthcare.appointmentservice.dto.CreateAppointmentRequest;
import com.healthcare.appointmentservice.entity.Appointment;
import com.healthcare.appointmentservice.entity.AppointmentStatus;
import com.healthcare.appointmentservice.exception.ExternalServiceException;
import com.healthcare.appointmentservice.exception.InvalidSchedulingRequestException;
import com.healthcare.appointmentservice.exception.ResourceNotFoundException;
import com.healthcare.appointmentservice.repository.AppointmentRepository;
import com.healthcare.appointmentservice.strategy.SchedulingStrategy;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Injects final fields via constructor
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    // Dependencies
    private final AppointmentRepository appointmentRepository;
    private final PatientServiceClient patientServiceClient;
    private final DoctorServiceClient doctorServiceClient;
    @Qualifier("defaultStrategy") // Specify which strategy bean to inject
    private final SchedulingStrategy schedulingStrategy;
    private final RabbitTemplate rabbitTemplate; // For publishing events

    // Get exchange name from properties
    @Value("${healthcare.amqp.exchange.appointments:healthcare.appointments.exchange}")
    private String appointmentsExchangeName;

    @Override
    @Transactional // Ensure atomicity
    public AppointmentDTO createAppointment(CreateAppointmentRequest request) {
        log.info("Attempting to create appointment for patient {} with doctor {} at {}",
                request.getPatientId(), request.getDoctorId(), request.getAppointmentDateTime());

        // 1. Validate Patient Exists (Handles Feign errors/fallbacks internally)
        validateExternalEntityExists(patientServiceClient::getPatientById, request.getPatientId(), "Patient");

        // 2. Validate Doctor Exists
        validateExternalEntityExists(doctorServiceClient::getDoctorById, request.getDoctorId(), "Doctor");

        // 3. Apply Scheduling Strategy Rules
        if (!schedulingStrategy.isSchedulingAllowed(request)) {
            throw new InvalidSchedulingRequestException("Scheduling conflict or rule violation via "
                    + schedulingStrategy.getStrategyName());
        }

        // 4. Create and prepare the appointment entity
        Appointment appointment = new Appointment();
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setReason(request.getReason());
        schedulingStrategy.prepareAppointment(appointment); // Strategy sets initial status etc.

        // 5. Save the appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created with ID: {}", savedAppointment.getId());

        // 6. Publish Event (Observer Pattern via RabbitMQ)
        publishAppointmentEvent("appointment.created", convertToDTO(savedAppointment));

        return convertToDTO(savedAppointment);
    }

    // Generic helper for validating external entities via Feign
    private <T> void validateExternalEntityExists(java.util.function.Function<Long, ResponseEntity<T>> feignCall, Long id, String entityName) {
        log.debug("Validating existence of {} with ID: {}", entityName, id);
        try {
            ResponseEntity<T> response = feignCall.apply(id);
            // Check status code AND body - fallback might return non-2xx or null body
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("{} validation failed: Service response non-successful or empty body (Status: {}) for ID: {}", entityName, response.getStatusCode(), id);
                throw new ResourceNotFoundException(entityName + " not found or external service error for ID: " + id);
            }
            log.debug("{} with ID: {} validated successfully.", entityName, id);
        } catch (FeignException.NotFound e) {
            log.warn("{} not found via Feign for ID: {}", entityName, id);
            throw new ResourceNotFoundException(entityName + " not found with id: " + id);
        } catch (Exception e) { // Catch other Feign errors, circuit breaker open errors etc.
            log.error("Error validating {} existence for ID {}: {}", entityName, id, e.getMessage(), e);
            throw new ExternalServiceException("Error communicating with " + entityName + " Service: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long id) {
        log.info("Fetching appointment with ID: {}", id);
        Appointment appointment = findAppointmentByIdOrThrow(id);
        // Basic conversion, enrichment could happen here if needed
        return convertToDTO(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByPatientId(Long patientId) {
        log.info("Fetching appointments for patient ID: {}", patientId);
        // Maybe validate patient exists first?
        // validateExternalEntityExists(patientServiceClient::getPatientById, patientId, "Patient");
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByDoctorId(Long doctorId) {
        log.info("Fetching appointments for doctor ID: {}", doctorId);
        // Maybe validate doctor exists first?
        // validateExternalEntityExists(doctorServiceClient::getDoctorById, doctorId, "Doctor");
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentDTO cancelAppointment(Long id, String cancelledBy) {
        log.info("Attempting to cancel appointment ID: {} by {}", id, cancelledBy);
        Appointment appointment = findAppointmentByIdOrThrow(id);

        // Check if cancellation is allowed
        if (appointment.getStatus() == AppointmentStatus.COMPLETED ||
                appointment.getStatus() == AppointmentStatus.CANCELLED_BY_DOCTOR ||
                appointment.getStatus() == AppointmentStatus.CANCELLED_BY_PATIENT ||
                appointment.getStatus() == AppointmentStatus.NO_SHOW) {
            log.warn("Cannot cancel appointment {}: Already in terminal status {}", id, appointment.getStatus());
            throw new InvalidSchedulingRequestException("Appointment cannot be cancelled, status is: " + appointment.getStatus());
        }

        // Determine new status
        AppointmentStatus newStatus = "PATIENT".equalsIgnoreCase(cancelledBy) ?
                AppointmentStatus.CANCELLED_BY_PATIENT :
                AppointmentStatus.CANCELLED_BY_DOCTOR; // Default or check role?

        appointment.setStatus(newStatus);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment ID: {} cancelled by {}, new status: {}", id, cancelledBy, newStatus);

        // Publish cancellation event
        publishAppointmentEvent("appointment.cancelled", convertToDTO(updatedAppointment));

        return convertToDTO(updatedAppointment);
    }

    // --- Helper Methods ---

    private Appointment findAppointmentByIdOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Appointment not found with ID: {}", id);
                    return new ResourceNotFoundException("Appointment not found with id: " + id);
                });
    }

    private void publishAppointmentEvent(String routingKey, AppointmentDTO payload) {
        try {
            log.info("Publishing event to exchange '{}' with routing key '{}' for appointment ID: {}",
                    appointmentsExchangeName, routingKey, payload.getId());
            // RabbitTemplate uses the configured message converter (JSON)
            rabbitTemplate.convertAndSend(appointmentsExchangeName, routingKey, payload);
        } catch (Exception e) {
            // Log error, but typically don't fail the main transaction
            log.error("Failed to publish appointment event for ID {} with key '{}': {}",
                    payload.getId(), routingKey, e.getMessage(), e);
            // Consider dead-lettering or other retry mechanisms for critical events
        }
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        BeanUtils.copyProperties(appointment, dto);
        return dto;
    }

    // Conversion from DTO to Entity only needed for creation usually
}