FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY build/libs/*.jar app.jar
# Ensure this port matches the server.port configured for the application
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]