package com.healthcare.medicalrecordsservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // Important for Lombok inheritance
public class VisitEntryDTO extends RecordEntryDTO {
    private String reason;
    private String diagnosis;
    private String notes;

    // Optional: Add constructor if needed, but BeanUtils works with setters
    public VisitEntryDTO(LocalDateTime entryTimestamp, Long recordedByDoctorId, String reason, String diagnosis, String notes) {
        super("VISIT", entryTimestamp, recordedByDoctorId);
        this.reason = reason;
        this.diagnosis = diagnosis;
        this.notes = notes;
    }
}