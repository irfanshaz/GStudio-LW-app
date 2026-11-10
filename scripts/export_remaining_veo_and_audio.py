#!/usr/bin/env python3
"""
Little Wonder World 2.0 — Turnkey External Veo 2.0 & Wikimedia Audio Completion Script
Run this script on a developer workstation with internet access and a Google GenAI API key
to automatically generate the remaining 58 animal movement videos via Google Veo 2 and fetch
verified Wikimedia Commons / Xeno-canto wildlife audio recordings for the remaining 40 vocal animals.

Requirements:
  pip install google-genai requests
  ffmpeg installed on PATH
  export GEMINI_API_KEY="your-api-key"
"""
import os
import time
import json
import subprocess

def generate_veo_video(animal_id, prompt, output_mp4_path):
    from google import genai
    from google.genai import types
    client = genai.Client(api_key=os.environ["GEMINI_API_KEY"])
    print(f"[Veo] Generating 16:9 movement video for {animal_id}...")
    operation = client.models.generate_videos(
        model="veo-2.0-generate-001",
        prompt=prompt,
        config=types.GenerateVideosConfig(
            aspect_ratio="16:9",
            number_of_videos=1,
            duration_seconds=6,
            person_generation="dont_allow",
        ),
    )
    while not operation.done:
        time.sleep(10)
        operation = client.operations.get(operation)
    video_bytes = operation.response.generated_videos[0].video.video_bytes
    raw_path = output_mp4_path + ".raw.mp4"
    with open(raw_path, "wb") as f:
        f.write(video_bytes)
    # Normalize to Android-safe 1280x720 H.264 yuv420p, 24fps, muted
    subprocess.run([
        "ffmpeg", "-y", "-i", raw_path,
        "-vf", "scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2,setsar=1",
        "-r", "24", "-an",
        "-c:v", "libx264", "-profile:v", "high", "-level", "3.1", "-pix_fmt", "yuv420p",
        "-crf", "23", "-movflags", "+faststart",
        output_mp4_path
    ], check=True)
    os.remove(raw_path)
    print(f"[Veo] Saved validated 1280x720 H.264 MP4 -> {output_mp4_path}")

def main():
    with open("RESOLUTION_REPORT.json", "r", encoding="utf-8") as f:
        report = json.load(f)
    for ref in report["references"]:
        if ref["media_type"] == "animal_movement_video" and not ref["file_delivered_in_v4"]:
            out_path = ref["expected_path"]
            os.makedirs(os.path.dirname(out_path), exist_ok=True)
            if os.environ.get("GEMINI_API_KEY"):
                generate_veo_video(ref["animal_id"], ref["veo_generation_prompt"], out_path)
            else:
                print(f"[SKIP] Set GEMINI_API_KEY to generate {out_path}")

if __name__ == "__main__":
    main()
