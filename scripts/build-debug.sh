#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
export ANDROID_HOME="${ANDROID_HOME:-/c/Users/ahsuh/AppData/Local/Android/Sdk}"
export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
"$ROOT/gradlew" -p "$ROOT" --no-daemon :app:assembleDebug --console=plain
cp "$ROOT/app/build/outputs/apk/debug/app-debug.apk" "$ROOT/Readora-debug.apk"
echo "APK: $ROOT/Readora-debug.apk"
