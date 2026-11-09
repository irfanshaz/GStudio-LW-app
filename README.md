# Little Wonder World Android

Native Compose app. The debug build uses the AI Studio test signing configuration.
For local testing, use JDK 17, Android SDK 36.1, and a local debug keystore:

```sh
sdkmanager 'platforms;android-36.1'
keytool -genkeypair -keystore debug.keystore -storepass android -keypass android \
  -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 365 -dname CN=AndroidDebug
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

The debug key stays local. Use the original key for an in-place update.

## Offline media

`python3 scripts/index_media.py` indexes the checked-in Little Wonder World media
batches and unified animal pack. The build verifies the SHA-256 of each indexed
file before staging it into the APK. Metadata-only references do not create cards.
Run the indexer for current picture, audio, and video coverage counts.
Source ZIPs contain both generated and recorded assets. Availability and byte
integrity are not authenticity verification. The pack metadata labels the lion
call as generated. The Library versions of batches 02, 04 and 05 and animal batch 02 take precedence
where they provide replacement media. Library batches 01 and 03 matched the existing
repository archives byte for byte. The Drive links could not be independently matched.

Try Real World → Animals → Lion with airplane mode on. Listen repeats the
bundled call, Hear Name speaks the label, and an upward swipe advances one card.
Then try an animal with a bundled movement video, rapid swipes, backgrounding,
Back, and Parent Corner’s automatic sound switch. Film narration uses the
separate offline narration file when available, with the video track muted to
prevent competing sound. Picture facts are not timed video captions.

Core narration continues to use the installed offline voice unless an approved
recording with the expected resource name in VoiceAssetManifest is provided.
The app does not include a new approved teacher voice.

Existing Room v2 databases migrate to v3. Unknown upgrade paths now fail instead
of silently erasing data. Earlier schema versions need a verified migration
before distributing an update to those installations.
