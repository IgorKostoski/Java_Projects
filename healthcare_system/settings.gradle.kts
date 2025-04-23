pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name="healthcare_system"

include(
    "eureka-server",
    "config-server",
//    "api-gateway",
    "doctor-service",
    "appointment-service",
    "medical-records-service",
//    "billing-service",
//    "auth-service",
    "notification-service",
//    "common-lib",
    "patient-service",

)