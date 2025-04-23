package com.healthcare.medicalrecordsservice.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
// --- Jackson annotations for Polymorphism ---
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "entryType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VisitEntryDTO.class, name = "VISIT"),
        @JsonSubTypes.Type(value = LabResultEntryDTO.class, name = "LAB_RESULT")
        // Add other subtypes here
})
public abstract class RecordEntryDTO {
    private LocalDateTime entryTimestamp; // Keep timestamp here
    private Long recordedByDoctorId;
    private String entryType; // Matches property name in @JsonTypeInfo

    // Modified constructor for subclasses to pass common fields
    protected RecordEntryDTO(String entryType, LocalDateTime entryTimestamp, Long recordedByDoctorId) {
        this.entryType = entryType;
        // Set timestamp if provided, otherwise default (could also default in subclasses)
        this.entryTimestamp = (entryTimestamp != null) ? entryTimestamp : LocalDateTime.now();
        this.recordedByDoctorId = recordedByDoctorId;
    }

    // Simpler constructor if subclasses set timestamp/doctor themselves
    protected RecordEntryDTO(String entryType) {
        this.entryType = entryType;
        this.entryTimestamp = LocalDateTime.now(); // Default timestamp
    }
}