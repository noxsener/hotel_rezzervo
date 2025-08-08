FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY eureka-server/pom.xml ./eureka-server/
COPY gateway/pom.xml ./gateway/
COPY hotel-service/pom.xml ./hotel-service/
COPY notification-service/pom.xml ./notification-service/
COPY reservation-service/pom.xml ./reservation-service/
RUN mvn -f /app/pom.xml -pl gateway -am dependency:go-offline
COPY gateway/src ./gateway/src
RUN mvn -f /app/pom.xml -pl gateway -am clean package -DskipTests
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/gateway/target/*.jar app.jar
EXPOSE 20005
ENTRYPOINT ["java", "-jar", "app.jar"]