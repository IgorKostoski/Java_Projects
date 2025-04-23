package com.healthcare.medicalrecordsservice.repository;

import com.healthcare.medicalrecordsservice.document.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {

    // Custom query method derived from name (Spring Data MongoDB supports this)
    Optional<MedicalRecord> findByPatientId(Long patientId);

    // More complex queries can be defined using @Query annotation with MongoDB query syntax
    // @Query("{ 'patientId': ?0, 'entries.entryType': ?1 }")
    // Optional<MedicalRecord> findByPatientIdAndEntryType(Long patientId, String entryType);
}