package com.healthcare.billingservice.service;

import com.healthcare.billingservice.dto.InvoiceDTO;
import com.healthcare.billingservice.entity.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
    InvoiceDTO createInvoice(InvoiceDTO invoiceDTO); // Or use a CreateInvoiceRequestDTO
    InvoiceDTO getInvoiceById(Long id);
    InvoiceDTO getInvoiceByAppointmentId(Long appointmentId);
    List<InvoiceDTO> getInvoicesByPatientId(Long patientId);
    InvoiceDTO updateInvoiceStatus(Long id, InvoiceStatus status); // Example action
    // void handleAppointmentCompleted(Long appointmentId, Long patientId, BigDecimal amount); // Example for event handling
}