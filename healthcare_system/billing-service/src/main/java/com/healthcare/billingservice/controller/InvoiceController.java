package com.healthcare.billingservice.controller;


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

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO) {
        logger.info("Creating invoice: {}", invoiceDTO.getAppointmentId());
        InvoiceDTO createdInvoice = invoiceService.createInvoice(invoiceDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdInvoice.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdInvoice);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<InvoiceDTO> getInvoiceByAppointmentId(@PathVariable Long appointmentId) {
        logger.info("Fetching invoice for appointment ID: {}", appointmentId);
        return ResponseEntity.ok(invoiceService.getInvoiceByAppointmentId(appointmentId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoiceByPatientId(@PathVariable Long patientId) {
        logger.info("Fetching invoice for patient ID: {}", patientId);
        return ResponseEntity.ok(invoiceService.getInvoicesByPatientId(patientId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<InvoiceDTO> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status) { // Get status from query param
        logger.info("Request received to update status for invoice ID: {} to {}", id, status);
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(id, status));
    }
}
