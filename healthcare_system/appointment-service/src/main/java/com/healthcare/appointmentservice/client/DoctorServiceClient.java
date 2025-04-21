package com.healthcare.appointmentservice.client;

import com.healthcare.appointmentservice.dto.DoctorDTO; // Use local DTO
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "doctor-service", fallback = DoctorServiceClient.DoctorServiceFallback.class)
public interface DoctorServiceClient {

    @GetMapping("/api/doctors/{id}")
    @CircuitBreaker(name = "doctorServiceCB") // Circuit breaker name
    ResponseEntity<DoctorDTO> getDoctorById(@PathVariable("id") Long id);

    // Fallback implementation class
    @Component
    class DoctorServiceFallback implements DoctorServiceClient {
        private static final Logger log = LoggerFactory.getLogger(DoctorServiceFallback.class);

        @Override
        public ResponseEntity<DoctorDTO> getDoctorById(Long id) {
            log.error("DoctorServiceFallback: Fallback triggered for getDoctorById ID: {}", id);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
    }
}