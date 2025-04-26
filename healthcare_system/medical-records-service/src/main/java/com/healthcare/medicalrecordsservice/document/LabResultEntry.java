package com.healthcare.medicalrecordsservice.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabResultEntry extends RecordEntry{
    private String testName;
    private Map<String, String> results;
    private String labNotes;


}