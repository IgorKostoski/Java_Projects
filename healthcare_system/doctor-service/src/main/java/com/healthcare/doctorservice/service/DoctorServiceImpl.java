package com.healthcare.doctorservice.service;


import com.healthcare.doctorservice.dto.DoctorDto;
import com.healthcare.doctorservice.entity.Doctor;
import com.healthcare.doctorservice.exception.ResourceNotFoundException;
import com.healthcare.doctorservice.repository.DoctorRepository;
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
public class DoctorServiceImpl implements DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public DoctorDto createDoctor(DoctorDto doctorDto) {
        logger.info("Creating doctor: {} {}, Spciality: {}", doctorDto.getFirstName(), doctorDto.getLastName(), doctorDto.getSpecialty());
        Doctor doctor = convertToEntity(doctorDto);
        Doctor savedDoctor = doctorRepository.save(doctor);
        logger.info("Doctor created with  ID: {}", savedDoctor.getId());
        return convertToDto(savedDoctor);

    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDto getDoctorById(Long id) {
       logger.info("Getting doctor with ID: {}", id);
       Doctor doctor = findDoctorByIdOrThrow(id);
       return convertToDto(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto> getAllDoctors() {
       logger.info("Getting all doctors");
       return doctorRepository.findAll().stream()
               .map(this::convertToDto)
               .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DoctorDto updateDoctor(Long id, DoctorDto doctorDto) {
        logger.info("Updating doctor with ID: {}", id);
        Doctor existingDoctor = findDoctorByIdOrThrow(id);
        BeanUtils.copyProperties(doctorDto, existingDoctor, "id");
        Doctor updatedDoctor = doctorRepository.save(existingDoctor);
        logger.info("Doctor updated with  ID: {}", updatedDoctor.getId());
        return convertToDto(updatedDoctor);
    }

    @Override
    @Transactional
    public void deleteDoctor(Long id) {
        logger.info("Deleting doctor with ID: {}", id);
        if (!doctorRepository.existsById(id)) {
            logger.info("Doctor with ID {} does not exist", id);
            throw new ResourceNotFoundException("Doctor with ID " + id + " does not exist");
        }
        doctorRepository.deleteById(id);
        logger.info("Doctor with ID {} deleted", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto> findDoctorsBySpecialty(String specialty) {
        logger.info("Finding doctors by specialty: {}", specialty);
        return doctorRepository.findBySpecialty(specialty).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private Doctor findDoctorByIdOrThrow(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Doctor not found with ID: {}", id);
                    return new ResourceNotFoundException("Doctor not found with ID: " + id);
                });
    }

    private DoctorDto convertToDto(Doctor doctor) {
        DoctorDto dto = new DoctorDto();
        BeanUtils.copyProperties(doctor, dto);
        return dto;
    }

    private Doctor convertToEntity(DoctorDto doctorDto) {
        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(doctorDto, doctor);
        return doctor;
    }
}
