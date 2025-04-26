package com.healthcare.billingservice.repository;

import com.healthcare.billingservice.entity.Invoice;
import com.healthcare.billingservice.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByPatientId(Long patientId);
    List<Invoice> findByStatus(InvoiceStatus status);
    Optional<Invoice> findByAppointmentId(Long appointmentId); // Find invoice for a specific appointment

    // Add more finders as needed, e.g., by date range
}