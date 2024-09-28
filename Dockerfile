#FROM ubuntu:slim
#
#RUN apt-get update && \
#    apt-get install -y git openjdk-17-jdk curl unzip zip && \
#    apt-get clean && rm -rf /var/lib/apt/lists/*
#
#RUN curl -s "https://get.sdkman.io" | bash && \
#    bash -c "source /root/.sdkman/bin/sdkman-init.sh && sdk install gradle"
#
#ENV SDKMAN_DIR="/root/.sdkman"
#ENV PATH="$SDKMAN_DIR/bin:$PATH"
#
#WORKDIR /app
#COPY build.gradle settings.gradle /app/
#RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && gradle build --no-daemon --parallel || return 0"
#
#RUN git clone https://github.com/Kascald/LaChaTa_Bot.git
#
#WORKDIR /app/LaChaTa_Bot
#
#RUN gradle build
#
#RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && gradle build --no-daemon --parallel"
#
#ENV BOT_TOKEN=${BOT_TOKEN}
#
##ENTRYPOINT ["java", "-jar", "/app/LaChaTa_Bot/build/libs/LachataBot-1.0-SNAPSHOT.jar"]
#
#FROM alpine:latest
#
#RUN apk update && apk --no-cache add gcompat libstdc++ bash git curl unzip zip openjdk17
#
#RUN curl -s "https://get.sdkman.io" | bash && \
#    bash -c "source /root/.sdkman/bin/sdkman-init.sh && sdk install gradle"
#
#ENV SDKMAN_DIR="/root/.sdkman"
#ENV PATH="$SDKMAN_DIR/bin:$PATH"
#
#WORKDIR /app
#COPY build.gradle settings.gradle /app/
#RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && gradle build --no-daemon --parallel || return 0"
#
#RUN git clone https://github.com/Kascald/LaChaTa_Bot.git
#
#WORKDIR /app/LaChaTa_Bot
#RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && gradle clean shadowJar --no-daemon --parallel"
#
##ENV BOT_TOKEN=${BOT_TOKEN}
#
#ENTRYPOINT ["java", "-jar", "/app/LaChaTa_Bot/build/libs/LachataBot-1.0-SNAPSHOT-all.jar"]

## DOCKER BUILD    <0.3> + latset tag add
# docker build --no-cache -t lachata-bot:<version #0.3> -t lachata-bot:latest .

## DOCKER RUN   - add Token into <your-discord-bot-token>
# docker run -d --name lachata-bot-container -e BOT_TOKEN=<your-discord-bot-token> lachata-bot:latest --cpus="0.3" --memory="512m"

###arm version
FROM --platform=linux/arm64 alpine:latest
LABEL authors="yoruni"

RUN apk update && apk --no-cache add gcompat libstdc++ bash git curl unzip zip openjdk17

RUN curl -s "https://get.sdkman.io" | bash && \
    bash -c "source /root/.sdkman/bin/sdkman-init.sh && sdk install gradle"

ENV SDKMAN_DIR="/root/.sdkman"
ENV PATH="$SDKMAN_DIR/bin:$PATH"

WORKDIR /app
COPY build.gradle settings.gradle /app/
RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && gradle build --no-daemon --parallel || return 0"

RUN git clone https://github.com/Kascald/LaChaTa_Bot.git

WORKDIR /app/LaChaTa_Bot
RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && gradle clean shadowJar --no-daemon --parallel"


#ENV BOT_TOKEN=${BOT_TOKEN}

ENTRYPOINT ["java", "-jar", "/app/LaChaTa_Bot/build/libs/LachataBot-1.0-SNAPSHOT-all.jar"]
