// healthcare-system/common-lib/build.gradle.kts
// No 'plugins' block needed here anymore

// No plugin removal logic needed here anymore

dependencies {
    // Keep only dependencies truly needed by common-lib
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // implementation("jakarta.validation:jakarta.validation-api:...") // Example
}