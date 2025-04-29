// In RecordEntryDTO.java or a common base if preferred
// (No changes needed in the abstract DTO usually)

// --- healthcare-system/medical-records-service/src/main/java/com/healthcare/medicalrecordsservice/dto/VisitEntryDTO.java ---
package com.healthcare.medicalrecordsservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VisitEntryDTO extends RecordEntryDTO {
    private String reason;
    // Represents the *current* diagnosis for API interactions
    private String diagnosis;
    private String notes;

    // Optional: Field to expose history if needed by API consumers
    // private List<StateEntry<String>> diagnosisHistory;

    // Constructor might take only current state fields
    public VisitEntryDTO(String reason, String diagnosis, String notes) {
        super("VISIT");
        this.reason = reason;
        this.diagnosis = diagnosis; // Represents current diagnosis
        this.notes = notes;
    }
}