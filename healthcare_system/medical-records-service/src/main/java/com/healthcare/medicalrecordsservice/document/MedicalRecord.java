package com.healthcare.medicalrecordsservice.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList; // Import ArrayList
import java.util.List;      // Import List

@Document(collection = "medical_records")
@Data
@NoArgsConstructor
public class MedicalRecord {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long patientId;

    private String patientFirstName;
    private String patientLastName;

    // Add the entries field
    private List<RecordEntry> entries = new ArrayList<>(); // Initialize the list

    // Make constructor public
    public MedicalRecord(Long patientId, String patientFirstName, String patientLastName) {
        this.patientId = patientId;
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.entries = new ArrayList<>(); // Also initialize here
    }
}