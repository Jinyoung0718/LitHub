# ----------- 1단계: Build Stage -----------
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# gradle 설정만 먼저 복사 (캐시 최적화)
COPY build.gradle settings.gradle gradle.properties ./

# gradle wrapper 추가 및 실행 권한 부여
COPY gradlew ./
RUN chmod +x gradlew

# wrapper 및 기본 의존성 캐시
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon || true

# 전체 소스 복사
COPY . .

RUN ./gradlew clean build --no-daemon

# 불필요한 plain.jar 제거 (실행 jar만 남김)
RUN rm -f /app/build/libs/*-plain.jar

# ----------- 2단계: Run Stage -----------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# 실행할 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 실행 명령 (prod 환경 지정)
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
