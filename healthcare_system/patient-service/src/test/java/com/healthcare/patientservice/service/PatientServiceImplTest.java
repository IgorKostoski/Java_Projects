package com.healthcare.patientservice.service;

import com.healthcare.patientservice.dto.PatientDTO;
import com.healthcare.patientservice.entity.Patient;
import com.healthcare.patientservice.exception.ResourceNotFoundException;
import com.healthcare.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
public class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;
    private PatientDTO patientDTO;

    @BeforeEach
    void setUp() {
        patient = new Patient(1L, "John", "Doe", LocalDate.of(1990,1,1), "12345","123 Street");
        patientDTO = new PatientDTO();
        patientDTO.setId(1L);
        patientDTO.setFirstName("John");
        patientDTO.setLastName("Doe");
        patientDTO.setDateOfBirth(LocalDate.of(1990,1,1));
        patientDTO.setContactNumber("12345");
        patientDTO.setAddress("123 Street");

    }

    @Test
    @DisplayName("Test createPatient - Success")
    void givenPatientDTO_whenCreatePatient_thenReturnPatientDTO() {

        given(patientRepository.save(any(Patient.class))).willReturn(patient);

        PatientDTO savedDTO = patientService.createPatient(patientDTO);

        assertThat(savedDTO).isNotNull();
        assertThat(savedDTO.getFirstName()).isEqualTo("John");
        assertThat(savedDTO.getId()).isEqualTo(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Test get PatientById - Found")
    void givenPatientDTO_whenGetPatientById_thenReturnPatientDTO() {

        given(patientRepository.findById(1L)).willReturn(Optional.of(patient));

        PatientDTO foundDTO = patientService.getPatientById(1L);

        assertThat(foundDTO).isNotNull();
        assertThat(foundDTO.getId()).isEqualTo(1L);
        verify(patientRepository,times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test getPatientById - Not Found")
    void givenNonExistentPatientId_whenGetPatientById_thenThrowResourceNotFoundException() {
        long nonExistentId = 99L;
        given(patientRepository.findById(nonExistentId)).willReturn(Optional.empty());

       assertThrows(ResourceNotFoundException.class, () -> {
           patientService.getPatientById(nonExistentId);
       });

       verify(patientRepository,times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Test getAllPatients")
    void whenGetAllPatients_thenReturnPatientDTOList() {

        Patient patient2 = new Patient(2L,"Jane","Smith",LocalDate.of(1992,3,3), "67553","566 Lane");

        given(patientRepository.findAll()).willReturn(Arrays.asList(patient,patient2));

        List<PatientDTO> patientDTOList = patientService.getAllPatients();

        assertThat(patientDTOList).isNotNull();
        assertThat(patientDTOList.size()).isEqualTo(2);
        assertThat(patientDTOList.get(0).getFirstName()).isEqualTo("John");
        assertThat(patientDTOList.get(1).getFirstName()).isEqualTo("Jane");
        verify(patientRepository, times(1)).findAll();
    }

    @Test@DisplayName("Test deletePatient - Success")
    void givenPatientId_whenDeletePatient_thenRepositoryDeleteCalled() {
        long patientId = 1L;
        given(patientRepository.existsById(patientId)).willReturn(true);
        willDoNothing().given(patientRepository).deleteById(patientId);

        patientService.deletePatient(patientId);


        verify(patientRepository, times(1)).existsById(patientId);
        verify(patientRepository, times(1)).deleteById(patientId);
    }

    @Test
    @DisplayName("Test deletePatient - Not Found")
    void givenNonexistentPatientId_whenDeletePatient_thenThrowResourceNotFoundException() {
        long nonExistentId = 99L;
        given(patientRepository.existsById(nonExistentId)).willReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.deletePatient(nonExistentId);
        });

        verify(patientRepository, times(1)).existsById(nonExistentId);
//        verify(patientRepository, times(1)).deleteById(anyLong());
    }

}
