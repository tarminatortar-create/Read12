#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
export ANDROID_HOME="${ANDROID_HOME:-/c/Users/ahsuh/AppData/Local/Android/Sdk}"
export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
"$ROOT/scripts/create-dev-keystore.sh"
"$ROOT/gradlew" -p "$ROOT" --no-daemon :app:assembleRelease --console=plain \
  -PREADORA_STORE_FILE="$ROOT/readora-dev-release.jks" \
  -PREADORA_STORE_PASSWORD=readora-dev-pass \
  -PREADORA_KEY_ALIAS=readora \
  -PREADORA_KEY_PASSWORD=readora-dev-pass
cp "$ROOT/app/build/outputs/apk/release/app-release.apk" "$ROOT/Readora-release.apk"
echo "APK: $ROOT/Readora-release.apk"
