package com.healthcare.appointmentservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequest {
    @NotNull(message = "Patient ID cannot be null")
    private Long patientId;

    @NotNull(message = "Doctor ID cannot be null")
    private Long doctorId;

    @NotNull(message = "Appointment date/time cannot be null")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime appointmentDateTime;

    private String reason;
}