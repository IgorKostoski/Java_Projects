package com.healthcare.billingservice.dto;

import com.healthcare.billingservice.entity.InvoiceStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InvoiceDTO {

    private Long id;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    private LocalDate issueDate; // Often set by system on creation/issue
    private LocalDate dueDate;
    private InvoiceStatus status; // Status might be updated via separate actions

    // Timestamps usually not part of request DTOs, but included in responses
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}