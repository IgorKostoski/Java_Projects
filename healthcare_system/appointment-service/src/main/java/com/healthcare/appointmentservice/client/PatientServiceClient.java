package com.healthcare.appointmentservice.client;

import com.healthcare.appointmentservice.dto.PatientDTO; // Use the local DTO for now (Ideally from common-lib)
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// Removed: import org.springframework.stereotype.Component; // No longer needed for separate fallback class
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name MUST match the spring.application.name of the target service
// REMOVED: fallback = PatientServiceClient.PatientServiceFallback.class
@FeignClient(name = "patient-service")
public interface PatientServiceClient {

    Logger log = LoggerFactory.getLogger(PatientServiceClient.class);

    // Method signature matches PatientController's endpoint
    @GetMapping("/api/patients/{id}")
    // Rely solely on Resilience4j fallback method
    @CircuitBreaker(name = "patientServiceCB", fallbackMethod = "fallbackGetPatientById")
    ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Long id);

    // Define fallback logic directly in the interface using a default method
    // Signature must match the original method + an optional Throwable parameter
    default ResponseEntity<PatientDTO> fallbackGetPatientById(Long id, Throwable t) {
        log.error("Fallback for getPatientById triggered: ID {}, Error Type: {}, Message: {}",
                id, t.getClass().getSimpleName(), t.getMessage());
        // Return an appropriate error response or default object
        // Consider creating a default PatientDTO with an indication of fallback status if needed downstream
        PatientDTO fallbackDto = null; // Or new PatientDTO with default/error values
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackDto);
    }

    // --- REMOVED Separate Fallback Class Implementation ---
    /*
    @Component // No longer needed
    class PatientServiceFallback implements PatientServiceClient {
        private static final Logger log = LoggerFactory.getLogger(PatientServiceFallback.class);

        @Override
        public ResponseEntity<PatientDTO> getPatientById(Long id) {
            log.error("PatientServiceFallback: Fallback triggered for getPatientById ID: {}", id);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
    }
    */
}