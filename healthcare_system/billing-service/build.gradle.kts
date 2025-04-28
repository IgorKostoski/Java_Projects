// healthcare-system/billing-service/build.gradle.kts
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

    // Spring Cloud Starters
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    // implementation("org.springframework.cloud:spring-cloud-starter-openfeign") // Add if needed later
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Database Driver
    runtimeOnly("org.postgresql:postgresql")

    // Observability
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Project Dependencies (If using common-lib)
    // implementation(project(":common-lib"))

    // Utilities & Testing (Inherited)
}