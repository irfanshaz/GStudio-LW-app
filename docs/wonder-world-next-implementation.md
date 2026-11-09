# Wonder World next implementation
## Approved scope
- Independent Reading and Math settings and progress.
- First garden mission using existing learning activities.
- Persistent garden and visual completion flags.
- Sunny run, wave and flag states with Reduced Motion.
- Voice timing, short instructional scripts and a six-line voice audition.
- Reviewed phoneme recordings and optional Telugu instruction/story tracks.
- Gemini handoff for consistent mascot, icon and garden artwork.

## Current change
Directed narration now honors interrupt=false while speech is active or pending.
Starting directed speech cancels prior TTS, delayed speech, and recorded voice.
The same cancellation is applied before phonics playback.
This change still requires CI and listening validation.

## Next implementation order
1. Add independent subject settings with a database migration preserving existing progress.
2. Build the garden mission with resumable state and independent subject difficulty.
3. Connect completion flags and Sunny animation states.
4. Prepare scripts and audition assets. Audio files must be reviewed for pronunciation.
5. Validate mission completion, wrong answers, replay, app restart and reduced motion.
6. Prepare Gemini artwork handoff with precise asset sizes and animation states.
