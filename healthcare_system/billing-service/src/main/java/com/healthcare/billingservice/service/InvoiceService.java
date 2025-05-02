package com.healthcare.billingservice.service;

import com.healthcare.billingservice.dto.CreateInvoiceRequestDTO;
import com.healthcare.billingservice.dto.InvoiceDTO;
import com.healthcare.billingservice.entity.InvoiceStatus;

import java.util.List;

public interface InvoiceService {

    InvoiceDTO createInvoice(CreateInvoiceRequestDTO requestDTO);

    InvoiceDTO createInvoice(InvoiceDTO invoiceDTO);

    InvoiceDTO getInvoiceById(Long id);
    InvoiceDTO getInvoiceByAppointmentId(Long appointmentId);
    List<InvoiceDTO> getInvoicesByPatientId(Long patientId);
    InvoiceDTO updateInvoiceStatus(Long id, InvoiceStatus status); // Example action

}