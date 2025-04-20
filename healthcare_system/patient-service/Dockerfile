# Use a specific Java 17+ base image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR file from the build context (relative to patient-service module)
# Assumes Gradle builds the JAR into build/libs/ within the module
COPY build/libs/*.jar app.jar

# Expose the port defined in patient-service.yml (8081 in this case)
EXPOSE 8081

# Command to run the application
# ... other Dockerfile lines ...
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
# OR CMD ["java", "-jar", "/app/app.jar"]