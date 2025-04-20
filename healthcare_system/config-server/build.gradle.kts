plugins { id("java") }
configurations { implementation { exclude(group = "org.springframework.cloud", module = "spring-cloud-starter-config") } }
dependencies { /* config server deps */
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client") // If registering
}