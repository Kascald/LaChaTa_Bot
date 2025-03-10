# syntax=docker/dockerfile:1

###########################
# Builder Stage (Alpine)
###########################
FROM alpine:latest as builder
LABEL authors="yoruni"

# 빌드에 필요한 패키지 설치
RUN apk update && apk --no-cache add gcompat libstdc++ bash git curl unzip zip openjdk17

# SDKMAN과 Gradle 설치
RUN curl -s "https://get.sdkman.io" | bash && \
    bash -c "source /root/.sdkman/bin/sdkman-init.sh && sdk install gradle"

ENV SDKMAN_DIR="/root/.sdkman"
ENV PATH="$SDKMAN_DIR/bin:$PATH"

WORKDIR /app

# 빌드 설정 파일 복사 및 초기 빌드 (캐시 생성용, 실패해도 계속 진행)
COPY build.gradle settings.gradle /app/
RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && gradle build --no-daemon --parallel || true"

# GitHub 저장소 클론
RUN git clone https://github.com/Kascald/LaChaTa_Bot.git

WORKDIR /app/LaChaTa_Bot
# shadowJar 태스크로 -all.jar 생성
RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && gradle clean shadowJar --no-daemon --parallel"

# startup 스크립트 복사 및 실행 권한 부여
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

###########################
# Runtime Stage (Alpine)
###########################
FROM alpine:latest

# 런타임에 필요한 패키지 설치
RUN apk update && apk --no-cache add gcompat libstdc++ bash openjdk17

WORKDIR /app
# Builder에서 생성한 JAR 파일과 startup 스크립트 복사
COPY --from=builder /app/LaChaTa_Bot/build/libs/*.jar /app/LachataBot.jar
COPY --from=builder /app/start.sh /app/start.sh

# start.sh 스크립트가 JAR 파일을 자동으로 찾아 실행하도록 함
ENTRYPOINT ["/app/start.sh"]
