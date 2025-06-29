# Use a base image with Java 17 installed
FROM eclipse-temurin:17-jdk-jammy

ARG JAR_FILE

WORKDIR /opt/app

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]