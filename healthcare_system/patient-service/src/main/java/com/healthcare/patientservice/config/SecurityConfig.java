package com.healthcare.patientservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Define authorization rules
                .authorizeHttpRequests(authorize -> authorize
                        // Allow access to actuator health/info endpoints without authentication
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        // Require authentication for all API endpoints
                        .requestMatchers("/api/patients/**").authenticated()
                        // Secure any other endpoint (if any) - good default
                        .anyRequest().authenticated()
                )
                // Configure OAuth2 Resource Server validation for JWTs
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()) // Use JWT validation based on application properties
                )
                // Configure stateless session management (essential for APIs)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Disable CSRF for stateless APIs (tokens provide protection)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
