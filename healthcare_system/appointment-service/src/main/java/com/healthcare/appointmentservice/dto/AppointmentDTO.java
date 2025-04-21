package com.healthcare.appointmentservice.dto;


import com.healthcare.appointmentservice.entity.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String reason;
    private String notes;
    private PatientDTO patientDetails;
    private DoctorDTO doctorDetails;
}
