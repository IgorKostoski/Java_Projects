package com.healthcare.medicalrecordsservice.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VisitEntry extends RecordEntry {
    private String reason;
    private String diagnosis;
    @Field("doctor_notes")
    private String notes;

    public VisitEntry(Long recordedByDoctorId, String reason, String diagnosis, String notes) {
        super("VISIT", recordedByDoctorId);
        this.reason = reason;
        this.diagnosis = diagnosis;
        this.notes = notes;
    }
}