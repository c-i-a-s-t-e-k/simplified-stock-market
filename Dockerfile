FROM eclipse-temurin:26-jdk-alpine AS builder
WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon || true
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:26-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]