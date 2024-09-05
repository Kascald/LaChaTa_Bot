FROM openjdk:17-jdk-alpine
LABEL authors="KG"
WORKDIR /app

# 애플리케이션 jar 파일을 복사
COPY lib/LachataBot-1.0-SNAPSHOT.jar /app/LachataBot-1.0-SNAPSHOT.jar

# 환경 변수를 설정
ENV BOT_TOKEN=${BOT_TOKEN}

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/LachataBot-1.0-SNAPSHOT.jar"]