FROM ubuntu:latest

RUN apt-get update && \
    apt-get install -y git openjdk-17-jdk curl unzip && \
    apt-get clean

RUN curl -s "https://get.sdkman.io" | bash && \
    bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk install gradle"

ENV SDKMAN_DIR="$HOME/.sdkman"
ENV PATH="$SDKMAN_DIR/bin:$PATH"
RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh"

WORKDIR /app

RUN git clone https://github.com/Kascald/LaChaTa_Bot.git

WORKDIR /app/LaChaTa_Bot

RUN gradle build

ENV BOT_TOKEN=${BOT_TOKEN}

ENTRYPOINT ["java", "-jar", "/app/LaChaTa_Bot/build/libs/LachataBot-1.0-SNAPSHOT.jar"]