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
@EnableWebFluxSecurity
public class SecurityConfig {


    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") // Read the property
    private String jwkSetUri;




    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder jwtDecoder) throws Exception { // Inject decoder
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus").permitAll()
                        .pathMatchers("/oauth2/**", "/login", "/.well-known/openid-configuration", "/oauth2/jwks").permitAll()
                        // Re-enable authentication requirement
                        .anyExchange().authenticated()
                )
                // Configure resource server to use our manually created jwtDecoder bean
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(jwtDecoder)))
                .csrf(ServerHttpSecurity.CsrfSpec::disable); // Disable CSRF for stateless API

        return http.build();
    }

    // --- Define the JWT Decoder Bean Manually ---
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        // Create a decoder that fetches keys from the configured JWK Set URI
        return NimbusReactiveJwtDecoder.withJwkSetUri(this.jwkSetUri).build();


    }
}