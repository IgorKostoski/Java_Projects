package com.healthcare.notificationservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor; // Add constructor for Jackson deserialization
import java.time.LocalDateTime;

@Data
@NoArgsConstructor // Important: Jackson needs a no-arg constructor for deserialization
public class AppointmentDTO {
    // Include fields expected in the message from AppointmentService
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String reason;
    // Add notes if AppointmentService sends it
}