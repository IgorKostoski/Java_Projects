dependencies {
    // Add specific dependencies for Eureka Server now or later
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // Needed by Eureka Server
}