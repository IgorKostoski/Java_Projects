// /Users/igorkostoski/Desktop/Java_Projects/healthcare-system/build.gradle.kts
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    id("org.springframework.boot") version "3.1.5" apply false
    id("io.spring.dependency-management") version "1.1.3" apply true // Keep apply true here
    id("java")
}

val springCloudVersion = "2022.0.4"
val resilience4jVersion = "2.1.0"

allprojects {
    group = "com.healthcare"
    version = "0.0.1-SNAPSHOT"
}

subprojects {
    // Apply java plugin to ALL subprojects first
    apply(plugin = "java")

    // --- Conditionally apply Spring Boot and related config ---
    if (project.name != "common-lib") { // <-- ADD Condition HERE
        apply(plugin = "org.springframework.boot")
        apply(plugin = "io.spring.dependency-management")

        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        configure<DependencyManagementExtension> {
            imports {
                mavenBom(SpringBootPlugin.BOM_COORDINATES)
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
                mavenBom("io.github.resilience4j:resilience4j-bom:${resilience4jVersion}")
            }
        }

        dependencies {
            // Add common Spring Boot dependencies here if desired
            compileOnly("org.projectlombok:lombok")
            annotationProcessor("org.projectlombok:lombok")
            testImplementation("org.springframework.boot:spring-boot-starter-test") {
                exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
            }
            // implementation("org.springframework.boot:spring-boot-starter-actuator") // Example
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }

    } else { // --- Configuration SPECIFICALLY for common-lib ---
        // common-lib needs the java-library plugin
        apply(plugin = "java-library")

        java { // Still configure Java version for common-lib
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        // common-lib might still need some dependencies
        dependencies {
            compileOnly("org.projectlombok:lombok") // Lombok might be useful here
            annotationProcessor("org.projectlombok:lombok")
            // implementation("jakarta.validation:jakarta.validation-api:...") // Example
        }
        tasks.withType<Test> { // Configure testing if common-lib has tests
            useJUnitPlatform()
        }
    }

    // --- Configuration exclusions NEED to be in MODULE build files ---
    // --- configurations block does NOT go here ---

} // End of subprojects block