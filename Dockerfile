FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY discovery-service/pom.xml ./discovery-service/
COPY user-service/pom.xml ./user-service/
COPY course-service/pom.xml ./course-service/
COPY enrollment-service/pom.xml ./enrollment-service/
COPY notification-service/pom.xml ./notification-service/
COPY api-gateway/pom.xml ./api-gateway/

RUN mvn dependency:go-offline

COPY discovery-service/src ./discovery-service/src
COPY user-service/src ./user-service/src
COPY course-service/src ./course-service/src
COPY enrollment-service/src ./enrollment-service/src
COPY notification-service/src ./notification-service/src
COPY api-gateway/src ./api-gateway/src

RUN mvn clean install -DskipTests

ARG JAR_FILE

RUN find /app -name "$(basename ${JAR_FILE})" -exec mv {} /app/application.jar \;
RUN find /app -name "*.jar" -exec mv {} /app/application.jar \;

FROM eclipse-temurin:17-jre-jammy

WORKDIR /opt/app

COPY --from=builder /app/application.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]