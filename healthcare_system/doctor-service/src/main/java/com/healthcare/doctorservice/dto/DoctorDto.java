package com.healthcare.doctorservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DoctorDto {

    private Long id;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characetrs")
    private String lastName;

    @NotBlank(message = "Speciality cannot be blank")
    @Size(max = 100, message = "Speciality cannot exceed 100 characters")
    private String specialty;

    @Size(max = 20, message = "Contact number cannot exceed 20 characters")
    private String contactNumber;
}
