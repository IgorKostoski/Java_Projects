
plugins {
    id("java")

}



dependencies {

    implementation("org.springframework.cloud:spring-cloud-starter-gateway")

    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")


    implementation("org.springframework.cloud:spring-cloud-starter-config")



    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Observability
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("io.micrometer:micrometer-registry-prometheus")


    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")


}