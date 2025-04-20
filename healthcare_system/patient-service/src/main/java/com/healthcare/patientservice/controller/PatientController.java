package com.healthcare.patientservice.controller;

import com.healthcare.patientservice.dto.PatientDTO;
import com.healthcare.patientservice.service.PatientService;
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
@RequestMapping("/api/patients")
@RequiredArgsConstructor

public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        logger.info("Received request to create patient: {} {}", patientDTO.getFirstName(), patientDTO.getLastName());
        PatientDTO createdPatient = patientService.createPatient(patientDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPatient.getId())
                .toUri();
        logger.info("Responding with created patient , location: {}", location);

        return ResponseEntity.created(location).body(createdPatient);


    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        logger.info("Received request to get patient with ID: {}", id);
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        logger.info("Received request to get patients");
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDTO patientDTO) {
        logger.info("Received request to update patient with ID: {}", id);
        PatientDTO updatedPatient = patientService.updatePatient(id,patientDTO);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        logger.info("Received request to delete patient with ID: {}", id);
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
