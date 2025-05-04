// healthcare_system/api-gateway/build.gradle

plugins {
    id("java")
    // Boot/DepMgmt applied by parent build
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-webflux") // Correctly included for Gateway
    implementation("org.springframework.cloud:spring-cloud-starter-gateway") // Correctly included

    // --- ADD THIS DEPENDENCY ---
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server") // Needed for JWT validation and WebFlux security components

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-amqp") // Keep if gateway needs AMQP

    // Spring Cloud Starters
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

    // Observability
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Project Dependencies (Assuming DTOs will live in common-lib eventually)
    // implementation(project(":common-lib"))

    // Utilities & Testing (Inherited)
}