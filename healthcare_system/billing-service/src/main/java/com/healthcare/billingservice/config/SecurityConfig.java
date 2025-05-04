package com.healthcare.billingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize etc.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Permit actuators for monitoring
                        .requestMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus").permitAll()
                        // Require authentication for API endpoints
                        .requestMatchers("/api/**").authenticated()
                        // Secure any other request (might not be necessary if /api/** covers all)
                        .anyRequest().authenticated()
                )
                // Configure as OAuth2 Resource Server validating JWTs
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())) // Uses jwk-set-uri from properties
                .csrf(csrf -> csrf.disable()); // Disable CSRF for stateless service

        return http.build();
    }

    // Optional: Explicit JwtDecoder bean if auto-configuration fails
    /*
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }
    */
}