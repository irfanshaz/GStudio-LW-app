#!/usr/bin/env bash
set -euo pipefail

app_id=${APP_ID:-com.aistudio.littlewonderworld.wvkqzp}
output=child-interaction-evidence
mkdir -p "$output"

adb shell wm size 1080x1920
adb shell wm density 420
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

adb logcat -c
collect_evidence() {
  adb logcat -d > "$output/logcat.txt" || true
  adb exec-out run-as "$app_id" tar -C files -cf - garden-evidence > "$output/garden.tar" || true
  if [ -s "$output/garden.tar" ]; then tar -xf "$output/garden.tar" -C "$output" || true; fi
}
trap collect_evidence EXIT
adb shell am instrument -w -r -e class com.example.qa.ChildInteractionJourneyTest \
  "$app_id.test/androidx.test.runner.AndroidJUnitRunner" | tee "$output/instrumentation.txt"
grep -Eq '^OK \(5 tests\)' "$output/instrumentation.txt"
