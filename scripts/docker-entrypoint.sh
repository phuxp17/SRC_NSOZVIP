#!/bin/sh
set -eu

cd /app

mkdir -p \
  /app/logs \
  /app/logs/ipblock \
  /app/logs/trade \
  /app/logs/sqlbackup \
  /app/chongddos \
  /app/diemdanh \
  /app/nhanquamocnap \
  /app/nhanquaviptuan \
  /app/new

if [ ! -f /app/config.properties ]; then
  echo "Missing /app/config.properties" >&2
  echo "Mount deploy/config.aapanel.properties or provide your own config file." >&2
  exit 1
fi

exec java ${JAVA_OPTS:--server -Xms2G -Xmx2G -Dfile.encoding=UTF-8 -Djava.awt.headless=true} -jar /app/app.jar
