# Use a base image with JDK and Gradle installed
FROM gradle:8.8-jdk22 AS build

# Install dependencies
RUN apt-get update && apt-get install -y wget

# Download and install JDK 22
RUN wget https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.tar.gz \
    && tar -xzf jdk-22_linux-x64_bin.tar.gz -C /usr/local/ \
    && mv /usr/local/jdk-22* /usr/local/java-22-openjdk \
    && rm jdk-22_linux-x64_bin.tar.gz

# JDK 22를 사용하도록 Gradle 환경 변수 설정
ENV JAVA_HOME=/usr/local/java-22-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Set working directory in the container
WORKDIR /app
# COPY . .

# Copy build.gradle and settings.gradle to install dependencies
COPY build.gradle settings.gradle ./
# COPY build.gradle .
# COPY settings.gradle .

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
