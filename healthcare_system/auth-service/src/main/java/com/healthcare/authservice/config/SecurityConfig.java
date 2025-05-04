package com.healthcare.authservice.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import java.util.UUID;

@Configuration
@EnableWebSecurity // Enable Spring Security's web security support
public class SecurityConfig {

    // Inject the UserDetailsService we created
    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return (context) -> {
            // Check if the principal is an OAuth2 client (Client Credentials grant)
            // or if dealing with user authentication context
            Set<String> scopes = context.getAuthorizedScopes(); // Get the scopes already authorized for this token

            if (!scopes.isEmpty()) {
                // Add scopes to the standard 'scope' claim (space-separated string)
                // OR use 'scp' claim if preferred by resource servers
                context.getClaims().claim("scope", String.join(" ", scopes));

                // Optional: Add authorities derived from scopes (prefixed with SCOPE_)
                // This can sometimes simplify @PreAuthorize checks if you only use hasAuthority()
                // context.getClaims().claim("authorities", scopes.stream()
                //         .map(scope -> "SCOPE_" + scope)
                //         .collect(Collectors.toList()));
            }

            // You can add other custom claims here if needed
            // context.getClaims().claim("custom-claim", "custom-value");
        };
    }

    // Define the main SecurityFilterChain for the Authorization Server endpoints
    @Bean
    @Order(1) // Highest priority filter chain
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Apply the default security configurations for the Authorization Server endpoints
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // Enable OpenID Connect 1.0 features
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults()); // Enable OIDC endpoints like /userinfo

        http
                // Redirect unauthenticated users to the login page
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"), // Redirect to /login
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // Configure acceptance of access tokens for UserInfo and Client Registration endpoints
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults())); // Use JWT for resource server checks within Auth Server

        return http.build();
    }

    // Define the SecurityFilterChain for standard web authentication (login page, etc.)
    @Bean
    @Order(2) // Lower priority than the authorization server chain
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        // Allow access to actuator health/info without authentication
                        .requestMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus").permitAll()
                        // Require authentication for any other request

                        .anyRequest().authenticated()
                )
                // Enable form login with default settings (redirects to /login)
                .formLogin(Customizer.withDefaults());

        // Link our JPA UserDetailsService
        http.userDetailsService(userDetailsService);

        return http.build();
    }


    // Bean for encoding passwords (BCrypt is recommended)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ---- OAuth2 / OIDC Configuration Beans ----

    // Define registered OAuth2 clients (applications allowed to use this auth server)
    // For production, use JDBC or JPA RegisteredClientRepository
    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        // Example client: confidential client using authorization_code grant
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client") // Client ID identifies the application
                // Store client secret securely encoded! Use BCrypt.
                .clientSecret(passwordEncoder.encode("secret")) // Client secret for confidential clients
                // Authentication method allowed for this client
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                // Grant types this client is allowed to use
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // Redirect URIs allowed after successful authorization
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client") // Example for a local frontend
                .redirectUri("https://oauth.pstmn.io/v1/callback") // Example for Postman
                // Scopes this client is allowed to request
                .scope(OidcScopes.OPENID) // Request basic OIDC scopes
                .scope(OidcScopes.PROFILE) // Request profile information
                .scope("patient:read")
                .scope("patient:write")
                .scope("doctor:read")
                .scope("appointment:read")
                .scope("appointment:schedule")
                .scope("record:read")
                .scope("record:write")
                .scope("billing:read")
//
                // Client settings (e.g., require proof key for code exchange - PKCE)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        // Add more clients here (e.g., for service-to-service communication using client_credentials)
        RegisteredClient serviceClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("internal-service")
                .clientSecret(passwordEncoder.encode("service-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("patient:read")   // <-- ADD
                .scope("doctor:read")    // <-- ADD
                .scope("internal.communication") // Keep or remove based on need
                // Add other scopes like appointment:read if needed for service-to-service
                .build();


        // Using an in-memory repository for simplicity.
        // Replace with JdbcRegisteredClientRepository or JpaRegisteredClientRepository for persistence.
        return new InMemoryRegisteredClientRepository(oidcClient, serviceClient);
    }

    // Bean to provide the JWKSource for signing JWTs
    // This generates an RSA key pair on startup for demo purposes.
    // In production, load keys from a secure keystore or external source.
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    // Helper method to generate an RSA key pair
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Key size
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    // Bean for decoding JWTs (needed for the resource server part within the auth server)
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    // Bean to configure core Authorization Server settings (issuer URI, endpoints)
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        // Use default settings, which derive issuer URI from the request context.
        // You can customize endpoints here if needed: .authorizationEndpoint("/auth") etc.
        return AuthorizationServerSettings.builder().build();
    }
}