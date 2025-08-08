
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY eureka-server/pom.xml ./eureka-server/
COPY gateway/pom.xml ./gateway/
COPY hotel-service/pom.xml ./hotel-service/
COPY notification-service/pom.xml ./notification-service/
COPY reservation-service/pom.xml ./reservation-service/
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/reservation-service/target/reservation-service-0.0.1-Alpha.jar app.jar
EXPOSE 20001
ENTRYPOINT ["java","-jar","/app/app.jar"]