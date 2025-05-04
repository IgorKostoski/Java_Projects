# Use the official Spring Boot base image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the JAR file from your build output (adjust the path if necessary)
COPY build/libs/*.jar app.jar

EXPOSE 8761

# Set the command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
