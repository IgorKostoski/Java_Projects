package com.healthcare.doctorservice.service;


import com.healthcare.doctorservice.dto.DoctorDto;

import java.util.List;

public interface DoctorService {
    DoctorDto createDoctor(DoctorDto doctorDto);
    DoctorDto getDoctorById(Long id);
    List<DoctorDto> getAllDoctors();
    DoctorDto updateDoctor(Long id, DoctorDto doctorDto);
    void deleteDoctor(Long id);
    List<DoctorDto> findDoctorsBySpecialty(String specialty);
}
