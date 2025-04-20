// /Users/igorkostoski/Desktop/Java_Projects/healthcare-system/build.gradle.kts
import org.springframework.boot.gradle.plugin.SpringBootPlugin // Make sure this import is present
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension // Import for explicit configuration type

plugins {
    // Define plugin versions available to healthcare modules
    id("org.springframework.boot") version "3.1.5" apply false
    // Apply the dependency management plugin HERE directly to the healthcare-system project itself
    // Also keep apply false so subprojects can apply it with the correct version if needed.
    id("io.spring.dependency-management") version "1.1.3" apply true // Apply TRUE here for the healthcare project
    id("java")
}

val springCloudVersion = "2022.0.4"
val resilience4jVersion = "2.1.0"

// Configurations applied to the healthcare-system project AND its subprojects (modules)
allprojects { // This applies to healthcare-system root AND its subprojects
    group = "com.healthcare"
    version = "0.0.1-SNAPSHOT"

    // Apply the plugin here as well for all subprojects if needed,
    // though applying in subprojects block below is usually sufficient
    // apply(plugin = "io.spring.dependency-management")

    // Repositories already defined in top-level build's allprojects
}

// Configurations applied ONLY to the modules within healthcare-system
subprojects {
    // Apply common plugins FIRST
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot") // Apply Boot by default
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
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        }
        // Maybe add actuator here if truly needed by *all* subprojects
        // implementation("org.springframework.boot:spring-boot-starter-actuator")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // --- NO MORE conditional plugin application or configuration exclusions here ---

} // End of subprojects block