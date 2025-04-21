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

@Component("defaultStrategy")
@RequiredArgsConstructor
public class DefaultSchedulingStrategy implements SchedulingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSchedulingStrategy.class);
    private final AppointmentRepository appointmentRepository;

    @Override
    public boolean isSchedulingAllowed(CreateAppointmentRequest request) {
        logger.debug("Applying {} for doctor {} at {}",getStrategyName(), request.getDoctorId(), request.getAppointmentDateTime());

        LocalDateTime requestedTime = request.getAppointmentDateTime();

        LocalDateTime windowStart = requestedTime.minusMinutes(29);
        LocalDateTime windowEnd = requestedTime.plusMinutes(29);

        long conflictCount = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                request.getDoctorId(), windowStart, windowEnd)
                .stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED_BY_DOCTOR
                && a.getStatus() != AppointmentStatus.CANCELLED_BY_PATIENT)
                .count();
        if (conflictCount > 0) {
            logger.warn("Scheduling conflict detected by {} for doctor {} at {}", getStrategyName(),request.getDoctorId(),requestedTime);
            return false;
        }
        return true;


    }

    @Override
    public void prepareAppointment(Appointment appointment) {
        logger.debug("Preparing appointment: {} using {}", appointment.getId(), getStrategyName());

        appointment.setStatus(AppointmentStatus.PENDING);

    }

    @Override
    public String getStrategyName() {
        return "Default Scheduling Strategy";
    }
}
