# Step 1: build the app with Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q clean package -DskipTests

# Step 2: run the app with a small Java image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/brunch.jar app.jar

# Render gives us a PORT variable; application.properties reads it (server.port=${PORT:8080})
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
