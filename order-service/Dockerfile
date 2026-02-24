# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package


# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app
COPY --from=builder /app/target/*jar app.jar
RUN chown spring:spring app.jar

USER spring
EXPOSE 8080

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-XX:+ExitOnOutOfMemoryError","-jar","app.jar"]
