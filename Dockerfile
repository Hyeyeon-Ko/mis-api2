# JDK 및 Gradle이 설치된 기본 이미지를 사용
FROM gradle:8.8-jdk22 AS build

# 의존성 설치 및 JDK 22 다운로드
RUN apt-get update && apt-get install -y wget && \
    wget https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.tar.gz && \
    tar -xzf jdk-22_linux-x64_bin.tar.gz -C /usr/local/ && \
    mv /usr/local/jdk-22* /usr/local/java-22-openjdk && \
    rm jdk-22_linux-x64_bin.tar.gz && \
    apt-get remove -y wget && apt-get autoremove -y && rm -rf /var/lib/apt/lists/*

# 기본 Java 버전을 JDK 22로 설정
ENV JAVA_HOME=/usr/local/java-22-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# 컨테이너 내 작업 디렉터리 설정
WORKDIR /app

# 의존성 설치를 위한 build.gradle 및 settings.gradle 복사
COPY build.gradle settings.gradle ./

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드
RUN gradle build -x test

# 최종 이미지 생성(JRE 및 실행 가능한 JAR 포함)
FROM eclipse-temurin:22-jre-alpine

# 최종 이미지 내 작업 디렉터리 설정
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일을 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 실행 명령어
CMD ["java", "-jar", "app.jar"]
