# Multi-stage Dockerfile for Spring Boot Application

# Stage 1: Build the application using Maven and JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build package (skipping tests for speed)
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image using lightweight JRE 17
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create directory for file database persistence
RUN mkdir -p /app/data

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render automatically provides $PORT)
ENV PORT=8080
EXPOSE 8080

# Start command: runs the Spring Boot application and binds to Render's $PORT
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
