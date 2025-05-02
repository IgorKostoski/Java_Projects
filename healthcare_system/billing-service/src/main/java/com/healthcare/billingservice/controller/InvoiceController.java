package com.healthcare.billingservice.controller;

import com.healthcare.billingservice.dto.CreateInvoiceRequestDTO;
import com.healthcare.billingservice.dto.InvoiceDTO;
import com.healthcare.billingservice.entity.InvoiceStatus;
import com.healthcare.billingservice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/billing/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);
    private final InvoiceService invoiceService;

    @PostMapping
    // @PreAuthorize("hasAuthority('SCOPE_billing:write') or hasRole('ADMIN')") // Example security
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody CreateInvoiceRequestDTO requestDTO) { // Use new DTO
        log.info("Request received to create invoice for appointment ID: {}", requestDTO.getAppointmentId());
        InvoiceDTO createdInvoice = invoiceService.createInvoice(requestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdInvoice.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdInvoice);
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('SCOPE_billing:read') or hasRole('ADMIN')")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        log.info("Request received to get invoice ID: {}", id);
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/appointment/{appointmentId}")
    // @PreAuthorize("hasAuthority('SCOPE_billing:read') or hasRole('ADMIN')")
    public ResponseEntity<InvoiceDTO> getInvoiceByAppointmentId(@PathVariable Long appointmentId) {
        log.info("Request received to get invoice for appointment ID: {}", appointmentId);
        return ResponseEntity.ok(invoiceService.getInvoiceByAppointmentId(appointmentId));
    }

    @GetMapping("/patient/{patientId}")
    // @PreAuthorize("hasAuthority('SCOPE_billing:read') or hasRole('ADMIN')") // Or check if user matches patientId?
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByPatientId(@PathVariable Long patientId) {
        log.info("Request received to get invoices for patient ID: {}", patientId);
        return ResponseEntity.ok(invoiceService.getInvoicesByPatientId(patientId));
    }

    @PutMapping("/{id}/status")
    // @PreAuthorize("hasAuthority('SCOPE_billing:write') or hasRole('ADMIN')")
    public ResponseEntity<InvoiceDTO> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status) {
        log.info("Request received to update status for invoice ID: {} to {}", id, status);
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(id, status));
    }
}