#!/bin/sh
set -eu

DEPLOY_PATH="${DEPLOY_PATH:-/opt/nso}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.vps.yml}"
ENV_FILE="${ENV_FILE:-.env.vps}"

cd "${DEPLOY_PATH}"

if [ ! -f "${ENV_FILE}" ]; then
  cp .env.vps.example "${ENV_FILE}"
  echo "Created ${ENV_FILE} from template. Update secrets, then rerun deploy." >&2
  exit 1
fi

mkdir -p logs diemdanh nhanquamocnap nhanquaviptuan new chongddos

docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}" up -d --build --remove-orphans
