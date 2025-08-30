FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle build -x test

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar image-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "image-service.jar"]