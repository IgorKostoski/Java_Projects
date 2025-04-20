// /Users/igorkostoski/Desktop/Java_Projects/healthcare-system/build.gradle.kts
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    id("org.springframework.boot") version "3.1.5" apply false
    id("io.spring.dependency-management") version "1.1.3" apply false
    id("java")
}

val springCloudVersion = "2022.0.4"
val resilience4jVersion = "2.1.0"

// Configurations applied to the healthcare-system project AND its subprojects (modules)
allprojects { // This applies to healthcare-system root AND its subprojects
    group = "com.healthcare"
    version = "0.0.1-SNAPSHOT"

    // *** ADD REPOSITORIES HERE for healthcare-system and its modules ***
    repositories {
        mavenCentral()
    }
}

// Configurations applied ONLY to the modules within healthcare-system
subprojects {
    // Apply common plugins FIRST
    apply(plugin = "java")

    // --- Conditionally apply Spring Boot and related config ---
    if (project.name != "common-lib") {
        apply(plugin = "org.springframework.boot")
        apply(plugin = "io.spring.dependency-management")

        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        configure<DependencyManagementExtension> {
            imports {
                mavenBom(SpringBootPlugin.BOM_COORDINATES)
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.4")
                mavenBom("io.github.resilience4j:resilience4j-bom:${resilience4jVersion}")
            }
        }

        dependencies {
            compileOnly("org.projectlombok:lombok")
            annotationProcessor("org.projectlombok:lombok")
            testImplementation("org.springframework.boot:spring-boot-starter-test") {
                exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
            }
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }

    } else { // --- Configuration SPECIFICALLY for common-lib ---
        apply(plugin = "java-library")
        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        dependencies {
            compileOnly("org.projectlombok:lombok")
            annotationProcessor("org.projectlombok:lombok")
        }
        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
} // End of subprojects block