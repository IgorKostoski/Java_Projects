package com.healthcare.medicalrecordsservice.document;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Base class for different entry types
@NoArgsConstructor
public abstract class RecordEntry {
    // Common fields for all entries
    private LocalDateTime entryTimestamp = LocalDateTime.now();
    private Long recordedByDoctorId;
    private String entryType;

    protected RecordEntry(String entryType, Long recordedByDoctorId) {
        this.entryType = entryType;
        this.recordedByDoctorId = recordedByDoctorId;
        this.entryTimestamp = LocalDateTime.now();
    }
}

