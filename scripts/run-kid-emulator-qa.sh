#!/usr/bin/env bash
set -euo pipefail

app_id=com.aistudio.littlewonderworld.wvkqzp
evidence_dir=kid-qa-evidence
mkdir -p "$evidence_dir"

collect_evidence() {
  adb exec-out run-as "$app_id" tar -C files -cf - kid-qa > "$evidence_dir/screens.tar" || true
  if [[ -s "$evidence_dir/screens.tar" ]]; then
    tar -xf "$evidence_dir/screens.tar" -C "$evidence_dir"
  fi
  rm -f "$evidence_dir/screens.tar"
  adb logcat -d -v time > "$evidence_dir/logcat.txt" || true
  adb logcat -d -b crash > "$evidence_dir/crashes.txt" || true
  if [[ -f "$evidence_dir/kid-qa/results.tsv" ]]; then
    awk -F '\t' '{ counts[$2]++ } END { for (status in counts) print status, counts[status] }' "$evidence_dir/kid-qa/results.tsv"
    awk -F '\t' '$2 == "FAIL" { print $1, substr($3, 1, 280) }' "$evidence_dir/kid-qa/results.tsv"
  fi
}
trap collect_evidence EXIT

adb wait-for-device
adb shell getprop > "$evidence_dir/device.txt"
adb shell wm size >> "$evidence_dir/device.txt"
adb shell wm density >> "$evidence_dir/device.txt"
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w -r \
  -e class com.example.qa.KidJourneyTest \
  "$app_id.test/androidx.test.runner.AndroidJUnitRunner" | tee "$evidence_dir/instrumentation.txt"
grep -Eq '^OK \([0-9]+ tests?\)' "$evidence_dir/instrumentation.txt"
