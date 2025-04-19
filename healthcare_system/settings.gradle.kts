pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "healthcare-system"

include(
    "eureka-server",
    "config-server",
    "api-gateway",
    "patient-service",
    "doctor-service",
    "appointment-service",
    "medical-records-service",
    "billing-service",
    "auth-service",
    "notification-service",
    "common-lib"
)