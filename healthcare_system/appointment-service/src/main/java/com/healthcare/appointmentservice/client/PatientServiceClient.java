package com.healthcare.appointmentservice.client;


import com.healthcare.appointmentservice.dto.PatientDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", fallback = PatientServiceClient.PatientServiceFallback.class)
public interface PatientServiceClient {

    Logger logger = LoggerFactory.getLogger(PatientServiceClient.class);

    @GetMapping("/api/patients/{id}")
    @CircuitBreaker(name = "patientServiceCB", fallbackMethod = "fallbackGetPatientById")
    ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Long id);

    default ResponseEntity<PatientDTO> fallbackGetPatientById(Long id, Throwable t){
        logger.error("Fallback for getPatientById: ID {} ,Error {}", id, t.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @Component
    class PatientServiceFallback implements PatientServiceClient {
        private static final Logger logger = LoggerFactory.getLogger(PatientServiceFallback.class);

        @Override
        public ResponseEntity<PatientDTO> getPatientById(Long id) {
            logger.error("PatientServiceFallback: Fallback triggered for getPatientById ID: {}", id);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
    }

}
