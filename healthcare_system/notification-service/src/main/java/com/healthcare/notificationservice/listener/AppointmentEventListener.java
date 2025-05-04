package com.healthcare.notificationservice.listener;

import com.healthcare.notificationservice.dto.AppointmentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventListener {

    private static final Logger log = LoggerFactory.getLogger(AppointmentEventListener.class);

    // Define the queue name directly or reference the bean name/property
    // Using SpEL to reference the queue name property defined in RabbitMQConfig


    /**
     * Listens to the specified queue for messages.
     * Spring AMQP, using the configured MessageConverter (Jackson2JsonMessageConverter),
     * will automatically deserialize the JSON message body into an AppointmentDTO object.
     *
     * @param appointmentDTO The deserialized appointment data from the message.
     */
    @RabbitListener(queues = "#{environment['healthcare.amqp.queue.notifications'] ?: 'appointment-notifications-queue'}")
    public void handleAppointmentEvent(AppointmentDTO appointmentDTO) {
        String resolvedQueueName = "appointment-notifications-queue";
        // Acknowledge receipt (logging serves as acknowledgment for now)
        log.info("Received appointment event from queue '{}': ID={}, Status={}, PatientID={}, DoctorID={}, DateTime={}",
                resolvedQueueName, // Log the actual queue name being listened to
                appointmentDTO.getId(),
                appointmentDTO.getStatus(),
                appointmentDTO.getPatientId(),
                appointmentDTO.getDoctorId(),
                appointmentDTO.getAppointmentDateTime());

        // TODO: Implement actual notification logic based on status
        switch (appointmentDTO.getStatus()) {
            case PENDING:
                // Maybe log only, or send internal alert?
                log.info("Processing PENDING appointment ID: {}", appointmentDTO.getId());
                // sendInternalAlert("New pending appointment...");
                break;
            case CONFIRMED: // Assuming AppointmentService might send CONFIRMED events later
                log.info("Sending confirmation notification for appointment ID: {}", appointmentDTO.getId());
                // sendEmail(patientId, "Your appointment is confirmed...");
                // sendSms(patientId, "Appointment confirmed...");
                break;
            case CANCELLED_BY_PATIENT:
            case CANCELLED_BY_DOCTOR:
                log.info("Sending cancellation notification for appointment ID: {}", appointmentDTO.getId());
                // sendEmail(patientId, "Your appointment has been cancelled...");
                break;
            // Handle other statuses as needed (COMPLETED, NO_SHOW)
            default:
                log.warn("Received unhandled appointment status: {}", appointmentDTO.getStatus());
        }

        // Simulate processing time
        // try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        log.debug("Finished processing event for appointment ID: {}", appointmentDTO.getId());
        // If message processing fails unexpectedly, Spring AMQP might re-queue the message
        // depending on configuration (error handlers, dead-letter queues).
    }

    // Placeholder methods for actual notification sending
    // private void sendEmail(Long recipientId, String message) { ... inject JavaMailSender ... }
    // private void sendSms(Long recipientId, String message) { ... use Twilio/SNS etc... }
    // private void sendInternalAlert(String message) { ... log or send to another system ... }
}