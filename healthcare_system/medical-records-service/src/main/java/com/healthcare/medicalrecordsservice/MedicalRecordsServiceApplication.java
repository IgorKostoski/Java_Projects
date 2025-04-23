package com.healthcare.medicalrecordsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Exclude DB config if not needed (though Mongo starter usually handles this)
// import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
// import org.springframework.cloud.openfeign.EnableFeignClients; // Add if using Feign

@SpringBootApplication //(exclude = {DataSourceAutoConfiguration.class}) // Usually not needed for Mongo
@EnableDiscoveryClient
// @EnableFeignClients // Add if calling other services
public class MedicalRecordsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalRecordsServiceApplication.class, args);
        System.out.println("---- Medical Records Service Started ----");
    }
}