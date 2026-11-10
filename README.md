# Little Wonder World 2.0 — Animal Media V4 Batch 02 (Additional Verified Farm & Aquatic Media)

Contains the remaining source-verified authentic wildlife audio (Cow, Horse, Whale) and CC0 1.0 real-animal movement videos (Cow, Horse, Goat), plus the full 128-reference resolution report.

## Contents of This Package
- `audio/animal_sounds/*.mp3`: 3 authentic, source-verified wildlife vocalization MP3(s) (44.1 kHz, mono, 128 kbps, normalized).
- `videos/animals/*.mp4`: 3 real-animal CC0 1.0 wildlife movement video(s) converted to Android-compatible `1280 × 720` (`16:9`) H.264 MP4, 24 fps, muted.
- `metadata/animals.json`: Updated animal catalog preserving all 64 existing IDs and paths while adding V4 status fields (`audio_status`, `v4_sound_ui_recommendation`, `video_status`, `v4_video_origin`).
- `RESOLUTION_REPORT.json`: Complete 128-reference status classification across all 64 animals (`GENERATED_AND_VALIDATED`, `AUTHENTIC_SOUND_UNAVAILABLE`, `SOURCE_OR_LICENSE_PENDING`, `GENERATION_FAILED`, `REQUIRED`, `OPTIONAL`).
- `QA_REPORT.md`: Detailed technical (FFprobe) and scientific/visual QA report.
- `LICENSES.csv`: Source archive URLs, authors, licenses (Public Domain, CC0 1.0, CC BY-SA 3.0), SHA-256 hashes, and verification notes.
- `scripts/export_remaining_veo_and_audio.py`: Turnkey Python utility for generating the remaining Veo 2.0 videos on an internet-enabled workstation.

## Integration into Little Wonder World 2.0 (`v3.zip` / `LWW_MediaPack_Complete_v2.0.zip`)
1. Extract this ZIP directly into `LittleWonderWorld_MediaPack/` from `v3.zip` (`LWW_MediaPack_Complete_v2.0.zip`). All relative paths (`audio/animal_sounds/...`, `videos/animals/...`, `metadata/animals.json`) match the existing manifest 1:1.
2. In your Android UI bindings, check `v4_sound_ui_recommendation` in `metadata/animals.json`:
   - `SHOW_SOUND_BUTTON`: Play `audio_filename` immediately.
   - `HIDE_SOUND_BUTTON`: Hide the audio button for the 18 biologically non-vocal/naturally silent species (`AUTHENTIC_SOUND_UNAVAILABLE`).
   - `DISABLE_SOUND_BUTTON_UNTIL_IMPORTED`: Keep the sound button disabled/grayed out until the remaining 40 vocal species MP3s are imported.
