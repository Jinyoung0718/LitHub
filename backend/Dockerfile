# 첫 번째 스테이지: 빌드 스테이지
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Gradle 설정 파일 복사
COPY build.gradle .
COPY settings.gradle .

# Gradle 래퍼 생성 및 권한 부여
RUN gradle wrapper

# 종속성 설치 (캐싱 최적화용)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew build -x test --no-daemon

# 불필요한 plain.jar 삭제
RUN rm -rf /app/build/libs/*-plain.jar

# 두 번째 스테이지: 실행 스테이지
FROM eclipse-temurin:17-jdk

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 실행 명령
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
