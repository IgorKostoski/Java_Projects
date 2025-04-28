package com.healthcare.doctorservice.controller;

import com.healthcare.doctorservice.dto.DoctorDto;
import com.healthcare.doctorservice.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);
    private final DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_doctor:write')")
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody DoctorDto doctorDto) {
        // Log entry point clearly
        logger.info("POST /api/doctors request received for doctor: {} {}", doctorDto.getFirstName(), doctorDto.getLastName());
        DoctorDto createdDoctor = doctorService.createDoctor(doctorDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // Base path (/api/doctors)
                .path("/{id}")        // Append /{id}
                .buildAndExpand(createdDoctor.getId()) // Populate {id}
                .toUri();
        // Log success and location
        logger.info("Doctor created successfully with ID: {}. Responding with status 201 Created, Location: {}", createdDoctor.getId(), location);
        return ResponseEntity.created(location).body(createdDoctor);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') or hasAuthority('SCOPE_doctor:read')")
    public ResponseEntity<List<DoctorDto>> getAllDoctors(
            @RequestParam(required = false) String speciality) { // Renamed from specialty for consistency if desired, or keep original
        List<DoctorDto> doctors;

        if (speciality != null && !speciality.isBlank()) {
            // Corrected typo in log message
            logger.info("GET /api/doctors request received with speciality filter: {}", speciality);
            doctors = doctorService.findDoctorsBySpecialty(speciality); // Ensure service method matches param name if changed
            logger.info("Found {} doctors with speciality: {}", doctors.size(), speciality);
        } else {
            logger.info("GET /api/doctors request received for all doctors");
            doctors = doctorService.getAllDoctors();
            logger.info("Found {} doctors", doctors.size());
        }
        return ResponseEntity.ok(doctors);
    }

    // Added GET by ID endpoint
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') or hasAuthority('SCOPE_doctor:read')")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        logger.info("GET /api/doctors/{} request received", id);

        DoctorDto doctor = doctorService.getDoctorById(id);
        logger.info("Doctor found with ID: {}. Responding with status 200 OK.", id);
        return ResponseEntity.ok(doctor);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SCOPE_doctor:write')")
    public ResponseEntity<DoctorDto> updateDoctor(
            @PathVariable Long id, @Valid @RequestBody DoctorDto doctorDto) {

        logger.info("PUT /api/doctors/{} request received", id);

        DoctorDto updatedDoctor = doctorService.updateDoctor(id, doctorDto);
        logger.info("Doctor with ID: {} updated successfully. Responding with status 200 OK.", id);
        return ResponseEntity.ok(updatedDoctor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") //
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        logger.info("DELETE /api/doctors/{} request received", id);

        doctorService.deleteDoctor(id);
        logger.info("Doctor with ID: {} deleted successfully. Responding with status 204 No Content.", id);
        return ResponseEntity.noContent().build();
    }
}