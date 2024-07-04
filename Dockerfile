# Use a base image with JDK and Gradle installed
FROM gradle:8.5-jdk17-alpine AS build

# Set working directory in the container
WORKDIR /app

# Copy build.gradle and settings.gradle to install dependencies
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src ./src

# Build the application
RUN gradle build -x test

# Create final image with JRE and executable JAR
FROM eclipse-temurin:22-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# Command to run the application
CMD ["java", "-jar", "app.jar"]