package com.healthcare.medicalrecordsservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // Important for Lombok inheritance
public class LabResultEntryDTO extends RecordEntryDTO {
    private String testName;
    private Map<String, String> results;
    private String labNotes;

    // Optional: Add constructor if needed, but BeanUtils works with setters
    public LabResultEntryDTO(LocalDateTime entryTimestamp, Long recordedByDoctorId, String testName, Map<String, String> results, String labNotes) {
        super("LAB_RESULT", entryTimestamp, recordedByDoctorId);
        this.testName = testName;
        this.results = results;
        this.labNotes = labNotes;
    }
}