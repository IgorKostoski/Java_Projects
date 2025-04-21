package com.healthcare.appointmentservice.dto;

import lombok.Data;

@Data // Only needs fields relevant to Appointment Service validation/display
public class DoctorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialty;
    // Add other fields if needed by appointment logic
}