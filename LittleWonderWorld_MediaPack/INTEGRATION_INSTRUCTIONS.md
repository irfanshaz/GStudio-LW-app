# Little Wonder World 2.0 — Batch05 ChatGPT & Android Integration Guide

## 1. Merging Batch05 Into Your Android Project
Extract `LWW_MediaPack_Batch05.zip` into `app/src/main/assets/LittleWonderWorld_MediaPack/`.
- All folder paths match `LWW_MediaPack_Batch01.zip`.
- Merge `metadata/animals.json`, `metadata/objects.json`, `metadata/lessons.json`, and `metadata/translations_{en,te,hi}.json` by appending the `items` array / object keys into the master runtime store.

## 2. Offline Playback & Graceful Fallback
- Check `image_status`, `audio_status`, and `video_status` in `metadata/animals.json` (or check file existence against `manifest.json`).
- Render packaged `.webp`, `.svg`, and `.mp4` files directly via `WebViewAssetLoader`.
- For items listed in `reports/missing_assets.csv`, display the multilingual text card and activity prompt while hiding the media button until the drop-in file is added.
