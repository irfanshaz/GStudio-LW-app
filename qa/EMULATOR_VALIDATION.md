# Preschool emulator validation

This suite runs the native `com.aistudio.littlewonderworld.wvkqzp` app, based on fix branch commit `56b2d0e`. It does not test the separate WebView app.

## Reproduce

Use the **Preschool emulator QA** GitHub Actions workflow on `qa/preschool-emulator`. It installs JDK 17 and Android SDK 36.1, builds app and instrumented test APKs, and launches API 35 x86_64 emulators with KVM. Device configurations:

- Phone: 1080×1920, 420 dpi; app requests landscape.
- Tablet: 1200×1920, 240 dpi; app requests landscape.

With a local Android SDK and a running emulator, generate the repository's expected `debug.keystore`, then run `bash scripts/run-kid-emulator-qa.sh`.

## Coverage and limitations

- All 257 activity cards: visible card, open, activity screen rendered, screenshot/semantics, Back returns to the same card/page. World entry is seeded through the production view model to isolate each world; card activation and return use UI actions.
- Navigation: Home, Toy Box, parent gate and return, media hub/reels/system Back, six lands, 27 world cards, Home.
- Tracing: real touch input, Retry, Skip, Replay and Home across all three configured age bands, with before/after evidence.
- Educational contract: Eight lesson entry checks cover the advertised starting letter, number, path or word. Keep these acceptance assertions until the app is corrected.

Screenshots and semantics are diagnostic evidence, not an automatic visual approval. A card opening is not proof that its lesson, answer logic, audio, completion, reward persistence or accessibility is correct. This suite does not substitute for physical-device touch/audio tests or supervised usability sessions with children.

## Artifacts

Each device uploads `kid-qa-<device>` even if assertions fail. Look for:

- `kid-qa-evidence/kid-qa/results.tsv`: per-card navigation outcomes.
- `activity_*.png/.txt`: rendered app window captures and semantic trees. The app window capture excludes emulator system overlays.
- `cards_*.png`: every catalog page.
- `trace_*.png/.txt`: tracing gestures and controls.
- `contract_*.png/.txt`: tracing deck opening checks.
- `guide_*.png/.txt`: traversal of the four tracing decks.
- `instrumentation.txt`, device properties and crash/full logs.

Do not classify a failed build or emulator boot as an app test failure. Do not classify missing evidence as a pass.
