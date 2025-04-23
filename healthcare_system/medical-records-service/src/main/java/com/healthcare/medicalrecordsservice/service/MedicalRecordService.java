package com.healthcare.medicalrecordsservice.service;

import com.healthcare.medicalrecordsservice.dto.MedicalRecordDTO;
import com.healthcare.medicalrecordsservice.dto.RecordEntryDTO;

public interface MedicalRecordService {
    // Get the entire record for a patient
    MedicalRecordDTO getRecordByPatientId(Long patientId);

    // Add a new entry to a patient's record
    MedicalRecordDTO addRecordEntry(Long patientId, RecordEntryDTO entryDTO);

    // (Optional) Create the initial record document for a new patient
    void createMedicalRecordShell(Long patientId, String firstName, String lastName);

    // Maybe methods to get specific entry types?
    // List<VisitEntryDTO> getVisitHistory(Long patientId);
}