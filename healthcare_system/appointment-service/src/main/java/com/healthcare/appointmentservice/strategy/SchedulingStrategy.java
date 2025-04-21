package com.healthcare.appointmentservice.strategy;

import com.healthcare.appointmentservice.dto.CreateAppointmentRequest;
import com.healthcare.appointmentservice.entity.Appointment;

public interface SchedulingStrategy {
    // Checks if the slot is available according to strategy rules
    boolean isSchedulingAllowed(CreateAppointmentRequest request);
    // Performs actions before saving (e.g., setting status)
    void prepareAppointment(Appointment appointment);
    // Identifier for the strategy
    String getStrategyName();
}