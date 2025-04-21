package com.healthcare.appointmentservice.strategy;

import com.healthcare.appointmentservice.dto.CreateAppointmentRequest;
import com.healthcare.appointmentservice.entity.Appointment;
import com.healthcare.appointmentservice.entity.AppointmentStatus;
import com.healthcare.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("defaultStrategy") // Bean name for injection qualifier
@RequiredArgsConstructor
public class DefaultSchedulingStrategy implements SchedulingStrategy {

    private static final Logger log = LoggerFactory.getLogger(DefaultSchedulingStrategy.class);
    private final AppointmentRepository appointmentRepository; // Inject repository

    @Override
    public boolean isSchedulingAllowed(CreateAppointmentRequest request) {
        log.debug("Applying {} for doctor {} at {}", getStrategyName(), request.getDoctorId(), request.getAppointmentDateTime());

        // Example Rule: Check for conflicting appointments for the same doctor
        LocalDateTime requestedTime = request.getAppointmentDateTime();
        // Define a conflict window (e.g., +/- 29 minutes for 30-min slots)
        LocalDateTime windowStart = requestedTime.minusMinutes(29);
        LocalDateTime windowEnd = requestedTime.plusMinutes(29);

        long conflictingCount = appointmentRepository.findByDoctorIdAndAppointmentDateTimeBetween(
                        request.getDoctorId(), windowStart, windowEnd)
                .stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED_BY_DOCTOR &&
                        a.getStatus() != AppointmentStatus.CANCELLED_BY_PATIENT) // Ignore cancelled
                .count();

        if (conflictingCount > 0) {
            log.warn("Scheduling conflict detected by {} for doctor {} at {}", getStrategyName(), request.getDoctorId(), requestedTime);
            return false; // Conflict found
        }
        // Add more rules: Check working hours, holidays, etc.
        return true; // No conflicts found
    }

    @Override
    public void prepareAppointment(Appointment appointment) {
        log.debug("Preparing appointment {} using {}", appointment.getId(), getStrategyName());
        // Set initial status when creating an appointment using this strategy
        appointment.setStatus(AppointmentStatus.PENDING);
    }

    @Override
    public String getStrategyName() {
        return "Default Scheduling Strategy";
    }
}