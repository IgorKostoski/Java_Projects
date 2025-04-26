package com.healthcare.billingservice.service;

import com.healthcare.billingservice.dto.InvoiceDTO;
import com.healthcare.billingservice.entity.Invoice;
import com.healthcare.billingservice.entity.InvoiceStatus;
import com.healthcare.billingservice.exception.ResourceNotFoundException; // Reuse or create new
import com.healthcare.billingservice.repository.InvoiceRepository;
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
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        log.info("Creating invoice for appointment ID: {}", invoiceDTO.getAppointmentId());
        // Basic creation, more complex logic would go here (e.g., check if exists)
        Invoice invoice = convertToEntity(invoiceDTO);
        // Defaults set by @PrePersist (status=DRAFT, issueDate=now)
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created with ID: {}", savedInvoice.getId());
        return convertToDTO(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceById(Long id) {
        log.info("Fetching invoice ID: {}", id);
        return invoiceRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceByAppointmentId(Long appointmentId) {
        log.info("Fetching invoice for appointment ID: {}", appointmentId);
        return invoiceRepository.findByAppointmentId(appointmentId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for appointment id: " + appointmentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByPatientId(Long patientId) {
        log.info("Fetching invoices for patient ID: {}", patientId);
        return invoiceRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvoiceDTO updateInvoiceStatus(Long id, InvoiceStatus status) {
        log.info("Updating status for invoice ID: {} to {}", id, status);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        // Add logic here - e.g., can't change status if VOID or PAID?
        invoice.setStatus(status);
        Invoice updatedInvoice = invoiceRepository.save(invoice); // @PreUpdate handles updatedAt
        log.info("Invoice ID: {} status updated to {}", id, status);
        return convertToDTO(updatedInvoice);
    }

    // --- Conversion Helpers ---
    private InvoiceDTO convertToDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        BeanUtils.copyProperties(invoice, dto);
        return dto;
    }

    private Invoice convertToEntity(InvoiceDTO invoiceDTO) {
        Invoice invoice = new Invoice();
        BeanUtils.copyProperties(invoiceDTO, invoice, "id", "createdAt", "updatedAt"); // Exclude generated fields
        return invoice;
    }
}