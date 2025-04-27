package com.healthcare.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Value("${security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder reactiveJwtDecoder) { // Inject the decoder


        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/health/**").permitAll()
                        .pathMatchers("/oauth2/**", "/login", "/.well-known/openid-configuration", "/oauth2/jwks").permitAll()
                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2

                        .jwt(jwt -> jwt.jwtDecoder(reactiveJwtDecoder))
                );


        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler())
        );


        return http.build();
    }


    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {

        return NimbusReactiveJwtDecoder.withJwkSetUri(this.jwkSetUri).build();


    }
}