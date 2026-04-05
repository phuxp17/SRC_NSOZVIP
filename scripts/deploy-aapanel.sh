#!/bin/sh
set -eu

DEPLOY_PATH="${DEPLOY_PATH:-/www/nso}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.aapanel.yml}"
ENV_FILE="${ENV_FILE:-.env.aapanel}"

if ! command -v rsync >/dev/null 2>&1; then
  echo "Missing rsync on deployment server." >&2
  exit 1
fi

mkdir -p "${DEPLOY_PATH}"

rsync -a --delete \
  --exclude '.git/' \
  --exclude '.github/' \
  --exclude '.idea/' \
  --exclude 'target/' \
  --exclude 'logs/' \
  --exclude 'diemdanh/' \
  --exclude 'nhanquamocnap/' \
  --exclude 'nhanquaviptuan/' \
  --exclude 'new/' \
  --exclude 'chongddos/' \
  --exclude '.env.aapanel' \
  ./ "${DEPLOY_PATH}/"

cd "${DEPLOY_PATH}"

if [ ! -f "${ENV_FILE}" ]; then
  cp .env.aapanel.example "${ENV_FILE}"
fi

mkdir -p logs diemdanh nhanquamocnap nhanquaviptuan new chongddos

docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}" up -d --build --remove-orphans
