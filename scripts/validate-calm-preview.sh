#!/usr/bin/env bash
set -euo pipefail
app_id=com.aistudio.littlewonderworld.wvkqzp.calm
collect_evidence() {
  adb logcat -d > calm-logcat.txt || true
  adb exec-out run-as "$app_id" \
    tar -cf - files/calm-evidence > calm-evidence.tar || true
}
# Collect while the emulator action still owns the running device, including on a test failure.
trap collect_evidence EXIT
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb logcat -c
timeout 300s adb shell am instrument -w -r -e class com.example.qa.CalmPlayJourneyTest \
  "$app_id.test/androidx.test.runner.AndroidJUnitRunner" | tee calm-instrumentation.txt
grep -Eq '^OK \(4 tests\)' calm-instrumentation.txt
