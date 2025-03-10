if [ -f "/app/my-bot.jar" ]; then
  exec java -jar "/app/my-bot.jar"
else
  JAR_FILE=$(ls /app/LaChaTa_Bot/build/libs/LachataBot-*.jar 2>/dev/null | head -n 1)
  if [ -n "$JAR_FILE" ]; then
    exec java -jar "$JAR_FILE"
  else
    echo "JAR 파일을 찾을 수 없습니다."
    exit 1
  fi
fi
