package com.healthcare.appointmentservice.controller;


import com.healthcare.appointmentservice.dto.AppointmentDTO;
import com.healthcare.appointmentservice.dto.CreateAppointmentRequest;
import com.healthcare.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        logger.info("Received request to create appointment for patientId: id {}, doctorId: {}", request.getPatientId(), request.getDoctorId());
        AppointmentDTO createAppointment = appointmentService.createAppointment(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createAppointment.getId())
                .toUri();
        logger.info("Responding with created appointment ID: {}, location: {}", createAppointment.getId(), location);
        return ResponseEntity.created(location).body(createAppointment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        logger.info("Received request to get appointment for ID: {}", id);
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        logger.info("Received request to get appointments for doctor ID: {}", doctorId);
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}/cancel") // Or use PATCH
    public ResponseEntity<AppointmentDTO> cancelAppointment(
            @PathVariable Long id,
            @RequestParam(defaultValue = "SYSTEM") String cancelledBy) {
        logger.info("Received request to cancel appointment ID: {} by {}", id, cancelledBy);
        AppointmentDTO cancelledAppointment = appointmentService.cancelAppointment(id, cancelledBy);
        return ResponseEntity.ok(cancelledAppointment);
    }

}
