package com.healthcare.medicalrecordsservice.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VisitEntry extends RecordEntry {
    private String reason;

    @Field("diagnosis_history")
    private List<StateEntry<String>> diagnosisHistory = new ArrayList<>();

    @Field("doctor_notes")
    private String notes;

    public VisitEntry(Long recordedByDoctorId, String reason, String  initialDiagnosis, String notes) {
        super("VISIT", recordedByDoctorId);
        this.reason = reason;

        this.notes = notes;
        this.diagnosisHistory.add(new StateEntry<>(initialDiagnosis));
    }


    public String getCurrentDiagnosis() {
        if (diagnosisHistory.isEmpty()) {
            return null;
        }
        return diagnosisHistory.get(diagnosisHistory.size()-1).getState();
    }

    public void updateDiagnosisHistory(String newDiagnosis, Long updatedByDoctorId) {
        this.diagnosisHistory.add(new StateEntry<>(newDiagnosis, updatedByDoctorId));
        this.setEntryTimestamp(LocalDateTime.now());
        this.setRecordedByDoctorId(updatedByDoctorId);
    }

    public void UpdateDiagnosis(String newDiagnosis, Long updatedByDoctorId) {
        if (newDiagnosis != null && !newDiagnosis.isBlank()) {
            this.diagnosisHistory.add(new StateEntry<>(newDiagnosis, updatedByDoctorId));
            this.setEntryTimestamp(LocalDateTime.now());
            this.setRecordedByDoctorId(updatedByDoctorId);
        } else {
            System.err.println("Attempted to update diagnosis with invalid value for visit entry.");
        }
    }

    public List<StateEntry<String>> getDiagnosisHistory() {
        return diagnosisHistory;
    }
}