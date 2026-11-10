# Sunny garden handoff

## Working Android implementation
The feature/wonder-garden branch contains separate Reading and Math settings, a four-stop garden mission, per-action saved state, replayable completed stops, a retained count of completed gardens, reduced-motion support and a device voice audition. The mission uses English reading tasks. Parents may select Telugu for garden instructions and the story. Telugu playback requires an installed offline Telugu voice; playback falls back to the English instruction when unavailable.

The scene currently uses code-drawn soil, sprout and flower with the existing Sunny image moving between positions. New character animation and a redesigned launcher icon are asset-production work. The audition is Android device speech, not a newly recorded performer. No claim of phoneme recording review is made by this handoff.

## Visual assets to produce
Use the existing Sunny image as the identity reference. Keep Sunny's golden fur, facial proportions and friendly expression consistent. Design for ages 2–6, phone landscape and tablets. Avoid text baked into art.

- Launcher icon: recognizable running pose, transparent foreground, separate teal background, monochrome silhouette, Android adaptive safe-area cropping. Supply 1024px masters and SVG where genuinely vector.
- Sunny idle, run, wave and flag planting: fixed camera, transparent alpha, consistent character scale and baseline. Supply frame timing, pivot point, and matching still poses. A short 700ms travel action and one brief completion wave are sufficient. Every animation needs a still Reduced Motion equivalent.
- Garden bare-soil, sprout and bloom scenes: consistent composition, no embedded labels, clean separation between scenery and interaction targets. Supply 1600x900 masters and small-screen crops.
- Flag: unchecked/current/completed variants with shape and icon differences that remain understandable without color.

Android launcher icons do not support an app-controlled perpetual run loop. Motion belongs in the launch experience and in-app mascot. Never extend startup just to finish the animation.

## Voice production
Create two auditions of one consistent adult character, calm and playful. Keep both natural and comfortable. Use the six English lines in garden-voice-scripts.csv. Deliver one dry mono WAV per line, 48kHz/16-bit, without music, reverb, clipping, or long leading silence. Preserve final consonants and natural pauses. Include duration and intended usage in the delivery manifest.

Phonics clips require separate asset review. Supply pure /s/ and /m/, short vowels /a/ /i/ /o/ /u/ /e/ appropriate to the chosen English accent, and SUN blending. Distinguish names, sounds, segmented blending and whole words. Do not pronounce phoneme spellings as letter names. Record plosives without an added vowel. Do not declare these reviewed based only on their scripts.

For Telugu, supply the garden instruction and story lines in a natural Telugu delivery. Keep English target words and English phonics in English. Confirm each spoken clip against the visible task and answer before integration.

## Runtime audio contract
- One foreground voice. Navigation and replacement instructions cancel queued speech and recorded voice.
- Piano touches stop narration; automatic piano rewards remain silent.
- Hear again is explicit. No repeating idle reminders during thinking time.
- Follow the current narration, music, effects and Reduced Motion controls.
- Stop speech and movement when the app backgrounds.
- Offline assets must work without network access.

## Asset acceptance
Check silhouettes at launcher size, transparent edges on light/dark backgrounds, natural anatomy across animation frames, stable baseline, complete loops and no flashes. Listen to every voice file, check pronunciation, compare its duration with playback and verify rapid replay/navigation cancellation. Perform full Reading/Math garden journeys on phone and tablet, including mute, hints, wrong answers, restart and resume. Report actual evidence and any gaps.
