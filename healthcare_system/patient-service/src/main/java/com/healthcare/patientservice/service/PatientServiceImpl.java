package com.healthcare.patientservice.service;

import com.healthcare.patientservice.dto.PatientDTO;
import com.healthcare.patientservice.entity.Patient;
import com.healthcare.patientservice.exception.ResourceNotFoundException;
import com.healthcare.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

    private final PatientRepository patientRepository;


    @Override
    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO) {
        logger.info("Creating a new patient: {} {}", patientDTO.getFirstName(), patientDTO.getLastName());
        Patient patient = convertToEntity(patientDTO);

        Patient savedPatient = patientRepository.save(patient);
        logger.info("Patient created with id {}", savedPatient.getId());
        return convertToDTO(savedPatient);
    }

    @Override
    @Transactional(readOnly = true) // Optimize for read operations
    public PatientDTO getPatientById(Long id) {
        logger.info("Fetching patient with ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Patient not found with ID: {}", id);
                    return new ResourceNotFoundException("Patient not found with id: " + id);
                });
        return convertToDTO(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        logger.info("Fetching all patients");
        return patientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());


    }

    @Override
    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        logger.info("Updating patient with ID: {}", id);
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Patient not found for update with ID: {}", id);
                    return new ResourceNotFoundException("Patient not found for update with id: " + id);
                });
        BeanUtils.copyProperties(patientDTO, existingPatient);

        Patient updatedPatient = patientRepository.save(existingPatient);
        logger.info("Patient updated with id {}", updatedPatient.getId());
        return convertToDTO(updatedPatient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        logger.info("Deleting patient with ID: {}", id);
        if (!patientRepository.existsById(id)) {
            logger.warn("Patient not found for delete with ID: {}", id);
            throw new ResourceNotFoundException("Patient not found for delete with id: " + id);
        }
        patientRepository.deleteById(id);
        logger.info("Patient deleted with id {}", id);

    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        BeanUtils.copyProperties(patient, dto);
        return dto;
    }

    private Patient convertToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        BeanUtils.copyProperties(dto, patient);
        return patient;
    }

}
