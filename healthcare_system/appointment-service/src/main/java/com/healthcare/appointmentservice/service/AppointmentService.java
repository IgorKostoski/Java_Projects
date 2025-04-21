package com.healthcare.appointmentservice.service;

import com.healthcare.appointmentservice.dto.AppointmentDTO;
import com.healthcare.appointmentservice.dto.CreateAppointmentRequest;
import java.util.List;

public interface AppointmentService {
    AppointmentDTO createAppointment(CreateAppointmentRequest request);
    AppointmentDTO getAppointmentById(Long id);
    List<AppointmentDTO> getAppointmentsByPatientId(Long patientId);
    List<AppointmentDTO> getAppointmentsByDoctorId(Long doctorId);
    AppointmentDTO cancelAppointment(Long id, String cancelledBy);
    // Add other methods like reschedule, complete, etc.
}