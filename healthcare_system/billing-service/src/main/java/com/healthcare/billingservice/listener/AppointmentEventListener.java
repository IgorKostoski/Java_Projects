package com.healthcare.billingservice.listener;

import com.healthcare.billingservice.dto.AppointmentDTO;
import com.healthcare.billingservice.dto.CreateInvoiceRequestDTO;
import com.healthcare.billingservice.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AppointmentEventListener {

    private static final Logger log = LoggerFactory.getLogger(AppointmentEventListener.class);

    // Inject InvoiceService to create invoices
    private final InvoiceService invoiceService;

    // Define the queue name from properties
    public static final String BILLING_QUEUE = "${healthcare.amqp.queue.billing}";

    @Autowired
    public AppointmentEventListener(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @RabbitListener(queues = "#{environment['healthcare.amqp.queue.billing'] ?: 'billing-invoice-queue'}")
    public void handleAppointmentCompleted(AppointmentDTO completedAppointment) {
        String queueNameResolved = "billing-invoice-queue";
        log.info("Received Appointment COMPLETED event from queue '{}': Appointment ID={}, Patient ID={}",
                queueNameResolved,
                completedAppointment.getId(),
                completedAppointment.getPatientId());

        // Basic logic: Create a draft invoice when an appointment is completed
        // TODO: Determine the actual amount - fetch appointment details, consult price list, etc.
        BigDecimal dummyAmount = new BigDecimal("100.00"); // Placeholder amount

        try {
            CreateInvoiceRequestDTO request = new CreateInvoiceRequestDTO();
            request.setPatientId(completedAppointment.getPatientId());
            request.setAppointmentId(completedAppointment.getId());
            request.setAmount(dummyAmount); // Use calculated/fetched amount

            invoiceService.createInvoice(request);
            log.info("Draft invoice creation triggered for completed appointment ID: {}", completedAppointment.getId());

        } catch (Exception e) {
            // Handle potential errors during invoice creation (e.g., duplicate check failed, validation error)
            // Important: Decide on error handling strategy. Re-queue? Dead-letter? Log only?
            log.error("Failed to create invoice for completed appointment ID {}: {}", completedAppointment.getId(), e.getMessage(), e);
            // Depending on broker config, uncaught exceptions might cause re-queuing.
            // Consider throwing AmqpRejectAndDontRequeueException for non-retryable errors.
        }
    }
}