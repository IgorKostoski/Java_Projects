// healthcare-system/auth-service/build.gradle.kts
plugins {
    id("java")
    // Boot/DepMgmt applied by parent build
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security") // Core Spring Security
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // For User/Client persistence
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation") // For potential user/client DTOs

    // Spring Authorization Server!
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")

    // Spring Cloud Starters
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client") // Auth server can register itself

    // Database Driver
    runtimeOnly("org.postgresql:postgresql")

    // Observability
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Project Dependencies (If needed)
    // implementation(project(":common-lib"))

    // Utilities & Testing (Inherited)
}