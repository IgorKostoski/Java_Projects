package com.healthcare.appointmentservice.config; // Adjust package if needed

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // <-- Import this
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults; // For default JWT config

@Configuration
@EnableWebSecurity // Explicitly enable Web MVC security configuration
@EnableMethodSecurity(prePostEnabled = true) // <-- ADD THIS to enable @PreAuthorize etc.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow unauthenticated access to actuator endpoints for monitoring
                        .requestMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus").permitAll()
                        // Secure all other endpoints (adjust as needed for your service)
                        .anyRequest().authenticated()
                )
                // Assuming appointment-service is also an OAuth2 Resource Server like the gateway
                // Configure it to validate JWTs. Relies on jwk-set-uri from properties (bootstrap.yml)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())) // Use default JWT validation
                // Disable CSRF for stateless microservices
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // Optional: Explicit JwtDecoder bean (keep commented unless auto-configuration fails)
    /*
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }
    */
}