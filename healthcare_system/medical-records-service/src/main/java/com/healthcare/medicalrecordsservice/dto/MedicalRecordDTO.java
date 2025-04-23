package com.healthcare.medicalrecordsservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class MedicalRecordDTO {
    private String id;
    private Long patientId;
    private String patientFirstName;
    private String patientLastName;
    private List<RecordEntryDTO> entries; // Use DTOs for entries as well
}