package com.healthcare.doctorservice.controller;


import com.healthcare.doctorservice.dto.DoctorDto;
import com.healthcare.doctorservice.service.DoctorService;
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
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody DoctorDto doctorDto) {
        logger.info("Received request to create doctor: {} {}", doctorDto.getFirstName(), doctorDto.getLastName());
        DoctorDto createdDoctor = doctorService.createDoctor(doctorDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDoctor.getId())
                .toUri();
        logger.info("Responding with created doctor, location: {}", location);
        return ResponseEntity.created(location).body(createdDoctor);
    }

    @GetMapping
    public ResponseEntity<List<DoctorDto>> getAllDoctors(
            @RequestParam(required = false) String speciality) {
        List<DoctorDto> doctors;

        if (speciality != null && !speciality.isBlank()) {
            logger.info("Received request to get all doctors with speciality: {]", speciality);
            doctors = doctorService.findDoctorsBySpecialty(speciality);
        } else {
            logger.info("Received request to get all doctors");
            doctors = doctorService.getAllDoctors();
        }
        return ResponseEntity.ok(doctors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDto> updateDoctor(
            @PathVariable Long id, @Valid @RequestBody DoctorDto doctorDto) {
        logger.info("Received request to update doctor with id : {} ", id, doctorDto.getId());
        DoctorDto updateDoctor = doctorService.updateDoctor(id, doctorDto);
        return ResponseEntity.ok(updateDoctor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        logger.info("Received request to delete doctor with id : {}", id);
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
