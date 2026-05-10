#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
KEYSTORE="$ROOT/readora-dev-release.jks"
if [[ -f "$KEYSTORE" ]]; then
  echo "Keystore already exists: $KEYSTORE"
  exit 0
fi
keytool -genkeypair \
  -v \
  -keystore "$KEYSTORE" \
  -storepass readora-dev-pass \
  -keypass readora-dev-pass \
  -alias readora \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -dname "CN=Readora Dev,O=Readora,C=US"
echo "Created: $KEYSTORE"
