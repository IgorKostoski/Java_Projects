package com.healthcare.appointmentservice.client;

import com.healthcare.appointmentservice.dto.PatientDTO; // Use the local DTO for now
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name MUST match the spring.application.name of the target service
@FeignClient(name = "patient-service", fallback = PatientServiceClient.PatientServiceFallback.class)
public interface PatientServiceClient {

    Logger log = LoggerFactory.getLogger(PatientServiceClient.class);

    // Method signature matches PatientController's endpoint
    @GetMapping("/api/patients/{id}")
    @CircuitBreaker(name = "patientServiceCB", fallbackMethod = "fallbackGetPatientById") // Resilience4j integration
    ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Long id);

    // Define fallback logic directly in the interface or a separate class
    default ResponseEntity<PatientDTO> fallbackGetPatientById(Long id, Throwable t) {
        log.error("Fallback for getPatientById: ID {}, Error: {}", id, t.getMessage());
        // Return an appropriate error response or default object
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null); // Or a default DTO
    }

    // --- Separate Fallback Class Implementation (Alternative/Often Cleaner) ---
    @Component // Make it a Spring bean so Feign can find it
    class PatientServiceFallback implements PatientServiceClient {
        private static final Logger log = LoggerFactory.getLogger(PatientServiceFallback.class);

        @Override
        public ResponseEntity<PatientDTO> getPatientById(Long id) {
            log.error("PatientServiceFallback: Fallback triggered for getPatientById ID: {}", id);
            // You could return a default DTO, null, or throw a specific exception here
            // Returning status code is better handled by @CircuitBreaker fallbackMethod directly in interface or service layer
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
        // Implement other methods if needed for fallback
    }
}