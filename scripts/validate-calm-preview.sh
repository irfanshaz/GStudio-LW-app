#!/usr/bin/env bash
set -euo pipefail
collect_evidence() {
  adb exec-out run-as com.aistudio.littlewonderworld.wvkqzp.calm \
    tar -cf - files/calm-evidence > calm-evidence.tar || true
}
# Collect while the emulator action still owns the running device, including on a test failure.
trap collect_evidence EXIT
gradle connectedDebugAndroidTest -PcalmPreview=true \
  -Pandroid.testInstrumentationRunnerArguments.class=com.example.qa.CalmPlayJourneyTest \
  --no-configuration-cache --no-daemon
