FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle

#For caching
RUN gradle dependencies || true

COPY src ./src

RUN gradle build -PdockerBuild -x test

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar book-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "book-service.jar"]