package com.healthcare.billingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Invoice ID

    @Column(nullable = false)
    private Long patientId; // Link to Patient

    @Column(nullable = false)
    private Long appointmentId; // Link to the specific Appointment being billed

    @Column(nullable = false)
    private BigDecimal amount; // Use BigDecimal for monetary values

    @Column(nullable = false)
    private LocalDate issueDate; // Date the invoice was issued

    private LocalDate dueDate; // Optional due date

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status; // Current status

    @Column(updatable = false) // Timestamp when created
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt; // Timestamp for last update

    @PrePersist // JPA callback before persisting
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (issueDate == null) {
            issueDate = LocalDate.now(); // Default issue date to now if not set
        }
        if (status == null) {
            status = InvoiceStatus.DRAFT; // Default status
        }
    }

    @PreUpdate // JPA callback before updating
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}