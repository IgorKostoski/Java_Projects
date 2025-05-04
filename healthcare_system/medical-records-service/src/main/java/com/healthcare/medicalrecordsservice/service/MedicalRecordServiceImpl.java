package com.healthcare.medicalrecordsservice.service;

// --- Add necessary imports ---

import com.healthcare.medicalrecordsservice.document.LabResultEntry;
import com.healthcare.medicalrecordsservice.document.MedicalRecord;
import com.healthcare.medicalrecordsservice.document.RecordEntry;
import com.healthcare.medicalrecordsservice.document.VisitEntry;
import com.healthcare.medicalrecordsservice.dto.LabResultEntryDTO;
import com.healthcare.medicalrecordsservice.dto.MedicalRecordDTO;
import com.healthcare.medicalrecordsservice.dto.RecordEntryDTO;
import com.healthcare.medicalrecordsservice.dto.VisitEntryDTO;
import com.healthcare.medicalrecordsservice.exception.ResourceNotFoundException;
import com.healthcare.medicalrecordsservice.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

// --- End imports ---

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordServiceImpl.class);
    private final MedicalRecordRepository medicalRecordRepository;



    @Override
    public MedicalRecordDTO getRecordByPatientId(Long patientId) {
        log.info("Fetching medical record for patient ID: {}", patientId);
        MedicalRecord record = findRecordByPatientIdOrThrow(patientId);
        return convertToDTO(record);
    }

    @Override
    @Transactional
    public MedicalRecordDTO addRecordEntry(Long patientId, RecordEntryDTO entryDTO) {
        // Assuming RecordEntryDTO has getEntryType() via Lombok @Data
        log.info("Adding new record entry of type '{}' for patient ID: {}", entryDTO.getEntryType(), patientId);

        MedicalRecord record = findRecordByPatientIdOrThrow(patientId);

        RecordEntry newEntry = convertToEntryDocument(entryDTO);
        if (newEntry == null) {
            log.error("Could not convert DTO to a known RecordEntry type: {}", entryDTO.getEntryType());
            throw new IllegalArgumentException("Unknown or unsupported record entry type: " + entryDTO.getEntryType());
        }

        // Ensure timestamp is set if not already by DTO conversion/constructor
        if (newEntry.getEntryTimestamp() == null) {
            newEntry.setEntryTimestamp(LocalDateTime.now());
        }
        // Ensure doctor ID is set
        if (newEntry.getRecordedByDoctorId() == null) {
            newEntry.setRecordedByDoctorId(entryDTO.getRecordedByDoctorId());
        }


        // Access the entries list (should exist now)
        record.getEntries().add(newEntry);

        MedicalRecord updatedRecord = medicalRecordRepository.save(record);
        log.info("Added entry to medical record for patient ID: {}", patientId);

        return convertToDTO(updatedRecord);
    }

    @Override
    @Transactional
    public MedicalRecordDTO updateVisitDiagnosis(Long patientId, String newDiagnosis, Long actingDoctorId) {
        log.info("Updating medical record for patient ID: {} by doctorr ID: {}", patientId, actingDoctorId);
        MedicalRecord record = findRecordByPatientIdOrThrow(patientId);

        VisitEntry latestVisit = record.getEntries().stream()
                .filter(e -> e instanceof VisitEntry)
                .map(e -> (VisitEntry) e)
                .max((v1, v2) -> v1.getEntryTimestamp().compareTo(v2.getEntryTimestamp()))
                .orElseThrow(() -> new ResourceNotFoundException("Could not find latest visit entry for patient ID: " + patientId));

       latestVisit.updateDiagnosisHistory(newDiagnosis, actingDoctorId);

        MedicalRecord updatedRecord = medicalRecordRepository.save(record);
        log.info("Updated diagnosis for patient ID: {}", patientId);
        return convertToDTO(updatedRecord);
    }



    @Override
    public void createMedicalRecordShell(Long patientId, String firstName, String lastName) {
        log.info("Creating medical record shell for patient ID: {} ({})", patientId, lastName);
        if (medicalRecordRepository.findByPatientId(patientId).isEmpty()) {
            // Use public constructor
            MedicalRecord shell = new MedicalRecord(patientId, firstName, lastName);
            medicalRecordRepository.save(shell);
            log.info("Medical record shell created for patient ID: {}", patientId);
        } else {
            log.warn("Medical record shell already exists for patient ID: {}", patientId);
        }
    }

    private MedicalRecord findRecordByPatientIdOrThrow(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId)
                .orElseThrow(() -> {
                    log.warn("Medical Record not found for patient ID: {}", patientId);
                    // Use imported exception
                    return new ResourceNotFoundException("Medical Record not found for patient ID: " + patientId);
                });
    }

    private MedicalRecordDTO convertToDTO(MedicalRecord record) {
      if (record == null) return null;
      MedicalRecordDTO dto = new MedicalRecordDTO();

        BeanUtils.copyProperties(record, dto, "entries");


        if (record.getEntries() != null) {
            dto.setEntries(record.getEntries().stream()
                    .map(this::mapEntryDto)
                            .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private RecordEntryDTO mapEntryDto(RecordEntry entry) {
        if (entry instanceof VisitEntry visitEntry) {
            VisitEntryDTO dto = new VisitEntryDTO();
            BeanUtils.copyProperties(visitEntry, dto, "diagnosisHistory");
            dto.setDiagnosis(visitEntry.getCurrentDiagnosis());
            return dto;
        } else if (entry instanceof LabResultEntry labEntry) {
            LabResultEntryDTO dto = new LabResultEntryDTO();
            BeanUtils.copyProperties(labEntry, dto);
            return dto;
        }
        log.warn("Unknown record entry type: {}", entry.getClass());
        return null;
    }

   private RecordEntry mapDtoToEntry(RecordEntryDTO dto) {
        if (dto instanceof VisitEntryDTO visitDto) {
            VisitEntry entry = new VisitEntry();
            BeanUtils.copyProperties(visitDto, entry, "diagnosis");
            if (visitDto.getDiagnosis() != null) {
                entry.updateDiagnosisHistory(visitDto.getDiagnosis(), visitDto.getRecordedByDoctorId());
            }
            return entry;
        } else if (dto instanceof LabResultEntryDTO labDto) {
            LabResultEntry entry = new LabResultEntry();
            BeanUtils.copyProperties(labDto, entry);
            return entry;
        }
        log.warn("Unknown record entry type: {}", dto.getClass());
        return null;
   }

    private RecordEntry convertToEntryDocument(RecordEntryDTO dto) {
        // Assuming RecordEntryDTO has getEntryType(), getRecordedByDoctorId(), getEntryTimestamp() via Lombok @Data
        Long doctorId = dto.getRecordedByDoctorId();

        if ("VISIT".equals(dto.getEntryType()) && dto instanceof VisitEntryDTO visitDTO) {
          return new VisitEntry(
                  doctorId,
                  visitDTO.getReason(),
                  visitDTO.getDiagnosis(),
                  visitDTO.getNotes()
          );
        } else if ("LAB_RESULT".equals(dto.getEntryType()) && dto instanceof LabResultEntryDTO labDTO) {
            // Use top-level class and no-args constructor
            LabResultEntry entry = new LabResultEntry();
            BeanUtils.copyProperties(labDTO, entry);
            // Ensure common fields from abstract DTO are copied
          entry.setRecordedByDoctorId(doctorId);
          entry.setEntryType("LAB_RESULT");
          entry.setEntryTimestamp(LocalDateTime.now());

            return entry;
        }
        log.warn("Cannot convert DTO of type {} to a known RecordEntry document", dto.getEntryType());
        return null;
    }
}