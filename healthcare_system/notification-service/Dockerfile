FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY build/libs/*.jar app.jar
# No EXPOSE needed if it doesn't run a web server
ENTRYPOINT ["java", "-jar", "/app/app.jar"]