package com.healthcare.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity // Enable Spring Security for WebFlux applications
public class SecurityConfig {

    // Read the property - make sure it's defined in application.yml or via Config Server
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder jwtDecoder) throws Exception {
        http
                .authorizeExchange(exchanges -> exchanges
                        // Allow access to health, info, prometheus actuators without authentication
                        .pathMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus").permitAll()
                        // Allow standard OAuth/OIDC discovery endpoints
                        .pathMatchers("/oauth2/**", "/login", "/.well-known/openid-configuration", "/oauth2/jwks").permitAll()
                        // Allow access to gateway actuator endpoint without authentication (optional, adjust as needed)
                        // If you want to secure it, remove it from permitAll and add specific role/authority checks below
                        .pathMatchers("/actuator/gateway/**").permitAll()
                        // All other requests must be authenticated
                        .anyExchange().authenticated()
                )
                // Configure resource server validation using the JWT decoder bean
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder)) // Use our custom decoder bean
                )
                // Disable CSRF protection - common for stateless APIs/gateways
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    // --- Define the JWT Decoder Bean Manually ---
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        // Create a decoder that fetches keys from the configured JWK Set URI
        // It handles caching and refreshing of the keys automatically.
        return NimbusReactiveJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }
}