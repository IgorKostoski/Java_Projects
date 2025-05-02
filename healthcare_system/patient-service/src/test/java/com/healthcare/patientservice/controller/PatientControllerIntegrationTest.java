package com.healthcare.patientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.patientservice.dto.PatientDTO;
import com.healthcare.patientservice.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Disabled
class PatientControllerIntegrationTest {

    @Autowired // Inject MockMvc bean provided by @WebMvcTest
    private MockMvc mockMvc;

    @MockBean // Create a mock instance of PatientService in the Spring context
    private PatientService patientService;

    @Autowired // Inject ObjectMapper for JSON conversions
    private ObjectMapper objectMapper;

    private PatientDTO patientDTO;
    private PatientDTO patientDTO2;

    @BeforeEach
    void setUp() {
        patientDTO = new PatientDTO();
        patientDTO.setId(1L);
        patientDTO.setFirstName("Alice");
        patientDTO.setLastName("Smith");
        patientDTO.setDateOfBirth(LocalDate.of(1985, 10, 20));
        patientDTO.setContactNumber("555-1234");
        patientDTO.setAddress("456 Oak Ave");

        patientDTO2 = new PatientDTO();
        patientDTO2.setId(2L);
        patientDTO2.setFirstName("Bob");
        patientDTO2.setLastName("Johnson");
        patientDTO2.setDateOfBirth(LocalDate.of(1978, 3, 12));
        patientDTO2.setContactNumber("555-9876");
        patientDTO2.setAddress("101 Maple Dr");
    }

    @Test
    @DisplayName("Test POST /api/patients - Create Patient")
    void givenPatientDTO_whenCreatePatient_thenReturnSavedPatient() throws Exception {
        // Arrange
        given(patientService.createPatient(any(PatientDTO.class)))
                .willAnswer((invocation) -> {
                    PatientDTO inputDto = invocation.getArgument(0);
                    // Simulate saving and getting an ID
                    inputDto.setId(1L); // Assign dummy ID for response check
                    return inputDto;
                });

        // Act: Perform POST request
        ResultActions response = mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientDTO))); // Send DTO as JSON body

        // Assert: Check response status, headers, and body content
        response.andDo(print()) // Print request/response details to console
                .andExpect(status().isCreated()) // Expect HTTP 201
                .andExpect(header().exists("Location")) // Expect Location header
                .andExpect(jsonPath("$.id", is(1))) // Expect id field to be 1 (using Hamcrest 'is')
                .andExpect(jsonPath("$.firstName", is(patientDTO.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(patientDTO.getLastName())));
    }

    @Test
    @DisplayName("Test GET /api/patients/{id} - Get Patient By ID")
    void givenPatientId_whenGetPatientById_thenReturnPatientObject() throws Exception {
        // Arrange
        long patientId = 1L;
        given(patientService.getPatientById(patientId)).willReturn(patientDTO);

        // Act
        ResultActions response = mockMvc.perform(get("/api/patients/{id}", patientId));

        // Assert
        response.andExpect(status().isOk()) // Expect HTTP 200
                .andDo(print())
                .andExpect(jsonPath("$.id", is((int)patientId))) // Need cast for long comparison
                .andExpect(jsonPath("$.firstName", is(patientDTO.getFirstName())));
    }

    @Test
    @DisplayName("Test GET /api/patients - Get All Patients")
    void givenPatientList_whenGetAllPatients_thenReturnPatientList() throws Exception {
        // Arrange
        List<PatientDTO> listOfPatients = Arrays.asList(patientDTO, patientDTO2);
        given(patientService.getAllPatients()).willReturn(listOfPatients);

        // Act
        ResultActions response = mockMvc.perform(get("/api/patients"));

        // Assert
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(listOfPatients.size()))) // Check array size
                .andExpect(jsonPath("$[0].firstName", is(patientDTO.getFirstName())))
                .andExpect(jsonPath("$[1].firstName", is(patientDTO2.getFirstName())));
    }

    @Test
    @DisplayName("Test DELETE /api/patients/{id} - Delete Patient")
    void givenPatientId_whenDeletePatient_thenReturnNoContent() throws Exception {
        // Arrange
        long patientId = 1L;
        willDoNothing().given(patientService).deletePatient(patientId); // Mock void method

        // Act
        ResultActions response = mockMvc.perform(delete("/api/patients/{id}", patientId));

        // Assert
        response.andExpect(status().isNoContent()) // Expect HTTP 204
                .andDo(print());
        verify(patientService, times(1)).deletePatient(patientId);
    }

    // Add tests for PUT /api/patients/{id}
    // Add tests for validation errors (e.g., sending blank first name) -> expect status().isBadRequest()
}