package com.healthcare.billingservice.service;

import com.healthcare.billingservice.dto.CreateInvoiceRequestDTO; // Use new DTO
import com.healthcare.billingservice.dto.InvoiceDTO;
import com.healthcare.billingservice.entity.Invoice;
import com.healthcare.billingservice.entity.InvoiceStatus;
import com.healthcare.billingservice.exception.ResourceConflictException; // New Exception
import com.healthcare.billingservice.exception.ResourceNotFoundException;
import com.healthcare.billingservice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; // Import LocalDate
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private final InvoiceRepository invoiceRepository;
    // private final AppointmentServiceClient appointmentServiceClient; // Inject if fetching details

    @Transactional
    @Override
    public InvoiceDTO createInvoice(CreateInvoiceRequestDTO requestDTO) { // Updated parameter
        log.info("Attempting to create invoice for appointment ID: {}", requestDTO.getAppointmentId());

        // 1. Check if invoice already exists for this appointment
        invoiceRepository.findByAppointmentId(requestDTO.getAppointmentId()).ifPresent(existing -> {
            log.warn("Invoice already exists for appointment ID: {}. Invoice ID: {}", requestDTO.getAppointmentId(), existing.getId());
            throw new ResourceConflictException("Invoice already exists for appointment ID: " + requestDTO.getAppointmentId());
        });

        // 2. Optional: Fetch appointment details via Feign to verify amount/patient/status
        // AppointmentDetails apptDetails = appointmentServiceClient.getAppointment(requestDTO.getAppointmentId());
        // if (apptDetails == null || !apptDetails.getStatus().equals("COMPLETED")) {
        //     throw new InvalidStateException("Cannot bill for appointment not yet completed.");
        // }
        // BigDecimal calculatedAmount = calculateAmount(apptDetails); // Logic to calculate amount
        // if(!calculatedAmount.equals(requestDTO.getAmount())) { // Verify amount matches if needed
        //    log.warn("Requested amount {} does not match calculated amount {}", requestDTO.getAmount(), calculatedAmount);
        // }

        // 3. Create Invoice entity
        Invoice invoice = new Invoice();
        invoice.setPatientId(requestDTO.getPatientId());
        invoice.setAppointmentId(requestDTO.getAppointmentId());
        invoice.setAmount(requestDTO.getAmount());
        // @PrePersist will set createdAt, updatedAt, default status (DRAFT), issueDate

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created with ID: {} for appointment ID: {}", savedInvoice.getId(), savedInvoice.getAppointmentId());
        return convertToDTO(savedInvoice);
    }


    @Override
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {

        CreateInvoiceRequestDTO request = new CreateInvoiceRequestDTO();
        request.setPatientId(invoiceDTO.getPatientId());
        request.setAppointmentId(invoiceDTO.getAppointmentId());
        request.setAmount(invoiceDTO.getAmount());

        return createInvoice(request);
    }


    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceById(Long id) {
        log.debug("Fetching invoice ID: {}", id); // Changed to debug
        return invoiceRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceByAppointmentId(Long appointmentId) {
        log.debug("Fetching invoice for appointment ID: {}", appointmentId);
        return invoiceRepository.findByAppointmentId(appointmentId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for appointment id: " + appointmentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByPatientId(Long patientId) {
        log.debug("Fetching invoices for patient ID: {}", patientId);
        return invoiceRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvoiceDTO updateInvoiceStatus(Long id, InvoiceStatus newStatus) {
        log.info("Attempting to update status for invoice ID: {} to {}", id, newStatus);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        // Add business logic for status transitions if needed
        // e.g., Cannot change status if already PAID or VOID
        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.VOID) {
            log.warn("Cannot update status for invoice {} because it is already {}", id, invoice.getStatus());
            // Maybe throw an InvalidStateException or just return current DTO
            throw new IllegalStateException("Cannot update status for invoice in state: " + invoice.getStatus());
        }

        // If changing to ISSUED, set the issue date if not already set
        if (newStatus == InvoiceStatus.ISSUED && invoice.getIssueDate() == null) {
            invoice.setIssueDate(LocalDate.now());
        }
        // Set due date logic could go here if applicable when issuing

        invoice.setStatus(newStatus);
        Invoice updatedInvoice = invoiceRepository.save(invoice); // @PreUpdate handles updatedAt
        log.info("Invoice ID: {} status updated to {}", id, newStatus);
        return convertToDTO(updatedInvoice);
    }

    // --- Conversion Helpers ---
    private InvoiceDTO convertToDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        BeanUtils.copyProperties(invoice, dto);
        return dto;
    }

    // Convert from CreateInvoiceRequestDTO
    private Invoice convertToEntity(CreateInvoiceRequestDTO requestDTO) {
        Invoice invoice = new Invoice();
        // Copy fields available in the request DTO
        invoice.setPatientId(requestDTO.getPatientId());
        invoice.setAppointmentId(requestDTO.getAppointmentId());
        invoice.setAmount(requestDTO.getAmount());
        // invoice.setDueDate(requestDTO.getDueDate()); // If applicable
        // Default status, issueDate, timestamps handled by @PrePersist
        return invoice;
    }
}