# Stage 1: Build Spring Boot
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# Stage 2: Run Spring Boot
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/Backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=65.0", "-jar", "app.jar"]
