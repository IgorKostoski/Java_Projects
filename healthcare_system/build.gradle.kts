// healthcare-system/build.gradle.kts (REVISED - Leaner Root)

import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("org.springframework.boot") version "3.1.5" apply false
    id("io.spring.dependency-management") version "1.1.3" apply false
    // kotlin("jvm") version "1.8.22" apply false // Uncomment if using Kotlin
    // kotlin("plugin.spring") version "1.8.22" apply false // Uncomment if using Kotlin
    id("java")
}

val springCloudVersion = "2022.0.4"
val resilience4jVersion = "2.1.0" // Define Resilience4j version centrally

allprojects {
    group = "com.healthcare"
    version = "0.0.1-SNAPSHOT"
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    // apply(plugin = "kotlin") // Uncomment if using Kotlin
    // apply(plugin = "kotlin-spring") // Uncomment if using Kotlin

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // tasks.withType<KotlinCompile> { // If using Kotlin
    //     kotlinOptions { jvmTarget = "17" }
    // }

    dependencyManagement {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
            mavenBom("io.github.resilience4j:resilience4j-bom:${resilience4jVersion}")
        }
    }

    // Define truly universal dependencies here (Optional - often better in module build files)
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

    // --- Module Type Specific Configuration ---
    // Apply java-library plugin and remove Spring Boot plugin from common-lib
    if (project.name == "common-lib") {
        apply(plugin = "java-library")
        plugins.remove(plugins.findPlugin("org.springframework.boot"))
    }
    // Exclude standard web starter from API Gateway as it uses WebFlux
    if (project.name == "api-gateway") {
        configurations.implementation.get().exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
    }
    // Exclude dependencies not needed/conflicting in Eureka Server
    if (project.name == "eureka-server") {
        configurations.implementation.get().exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
        configurations.implementation.get().exclude(group = "org.springframework.cloud", module = "spring-cloud-starter-config")
        configurations.implementation.get().exclude(group = "org.springframework.cloud", module = "spring-cloud-starter-netflix-eureka-client")
    }
    // Exclude dependencies not needed/conflicting in Config Server
    if (project.name == "config-server") {
        configurations.implementation.get().exclude(group = "org.springframework.cloud", module = "spring-cloud-starter-config")
        // Keep eureka-client if config-server should register itself
    }
}
