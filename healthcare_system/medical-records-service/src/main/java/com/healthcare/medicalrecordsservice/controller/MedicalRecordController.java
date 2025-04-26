package com.healthcare.medicalrecordsservice.controller;

import com.healthcare.medicalrecordsservice.dto.MedicalRecordDTO;
import com.healthcare.medicalrecordsservice.dto.RecordEntryDTO;
import com.healthcare.medicalrecordsservice.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordController.class);
    private final MedicalRecordService medicalRecordService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordByPatientId(@PathVariable Long patientId) {
        log.info("Request received for medical record of patient ID: {}", patientId);
        MedicalRecordDTO record = medicalRecordService.getRecordByPatientId(patientId);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/patient/{patientId}/entries")
    public ResponseEntity<MedicalRecordDTO> addRecordEntry(
            @PathVariable Long patientId,
            @Valid @RequestBody RecordEntryDTO entryDTO) {
        // Assuming RecordEntryDTO has getEntryType() via Lombok @Data
        log.info("Request received to add entry of type {} for patient ID: {}", entryDTO.getEntryType(), patientId);
        MedicalRecordDTO updatedRecord = medicalRecordService.addRecordEntry(patientId, entryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedRecord);
    }

    @PostMapping("/internal/patient-created")
    public ResponseEntity<Void> handlePatientCreated(@RequestBody PatientCreatedEvent event) {
        // Use record accessors (no 'get')
        log.info("Internal event received: Patient created ID {}", event.patientId());
        medicalRecordService.createMedicalRecordShell(event.patientId(), event.firstName(), event.lastName());
        return ResponseEntity.ok().build();
    }

    // Record definition remains the same
    static record PatientCreatedEvent(Long patientId, String firstName, String lastName){}
}