# Example for config-server/Dockerfile
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# Copy the built JAR (path is relative to build context)
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]