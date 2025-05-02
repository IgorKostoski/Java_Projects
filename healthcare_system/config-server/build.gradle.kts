plugins { id("java") }
configurations {
    implementation {
        exclude(
            group = "org.springframework.cloud",
            module = "spring-cloud-starter-config"
        )
    }
}
dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")



}