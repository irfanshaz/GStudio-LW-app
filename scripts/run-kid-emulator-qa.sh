#!/usr/bin/env bash
set -uo pipefail
mkdir -p kid-qa-evidence
adb shell getprop > kid-qa-evidence/device.txt
adb shell wm size >> kid-qa-evidence/device.txt
adb shell wm density >> kid-qa-evidence/device.txt
./gradlew --no-daemon :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.qa.KidJourneyTest
qa_status=$?
adb pull /sdcard/Android/data/com.aistudio.littlewonderworld.wvkqzp/files/kid-qa kid-qa-evidence/ || true
adb logcat -d -v time > kid-qa-evidence/logcat.txt
adb logcat -d -b crash > kid-qa-evidence/crashes.txt
exit "$qa_status"
