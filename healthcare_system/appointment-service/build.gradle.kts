// healthcare-system/appointment-service/build.gradle.kts
plugins {
    id("java")
    // Boot/DepMgmt applied by parent build
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-amqp") // For RabbitMQ

    // Spring Cloud Starters
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign") // For Feign clients
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j") // For Resilience4j

    // Database Driver
    runtimeOnly("org.postgresql:postgresql")

    // Observability
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Resilience4j specific dependencies (versions managed by BOM)
    // implementation("io.github.resilience4j:resilience4j-spring-boot3") // Included via starter-circuitbreaker
    // implementation("io.github.resilience4j:resilience4j-feign") // Included via starter-circuitbreaker

    // Project Dependencies (Assuming DTOs will live in common-lib eventually)
    // implementation(project(":common-lib"))

    // Utilities & Testing (Inherited)
}