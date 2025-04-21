package com.healthcare.appointmentservice.strategy;

import com.healthcare.appointmentservice.dto.CreateAppointmentRequest;
import com.healthcare.appointmentservice.entity.Appointment;

public interface SchedulingStrategy {

    boolean isSchedulingAllowed(CreateAppointmentRequest request);

    void prepareAppointment(Appointment appointment);

    String getStrategyName();
}
