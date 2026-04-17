#!/usr/bin/env bash
set -euo pipefail

if [ "$(id -u)" -ne 0 ]; then
  echo "Run this script with sudo or as root." >&2
  exit 1
fi

DEPLOY_USER="${SUDO_USER:-${USER:-root}}"
DEPLOY_PATH="${DEPLOY_PATH:-/opt/nso}"

export DEBIAN_FRONTEND=noninteractive

apt update
apt install -y \
  ca-certificates \
  curl \
  git \
  rsync \
  unzip \
  openssh-server \
  mariadb-client \
  docker.io \
  docker-compose-plugin

systemctl enable --now ssh
systemctl enable --now docker

if id -u "${DEPLOY_USER}" >/dev/null 2>&1; then
  usermod -aG docker "${DEPLOY_USER}" || true
fi

mkdir -p "${DEPLOY_PATH}"
chown -R "${DEPLOY_USER}:${DEPLOY_USER}" "${DEPLOY_PATH}" || true

cat <<EOF
Bootstrap completed.

Next steps:
1. Log out and back in so user '${DEPLOY_USER}' gets Docker group permissions.
2. Clone the repo into ${DEPLOY_PATH}.
3. Copy .env.vps.example to .env.vps and update secrets.
4. Run ./scripts/deploy-vps.sh for the first deployment.
EOF
