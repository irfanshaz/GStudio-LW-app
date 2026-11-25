# Calm play preview

Baseline: `0bb573860dcd3199cbc48deecaebec7f3c646f20` on `fix/toddler-tracing-media`.

## Behavior

- Four large home destinations use existing bundled artwork. The full land/world/activity catalog is paged, with four choices at a time. Classic home, Surprise Play and buddy greeting remain in the home hold menu.
- Shared play controls have one replay-instruction target and a continuous three-second adult hold for Show Me, favorite, replay, next activity, Back, Home and Parent Corner.
- Discovery offers Look, Find and Watch. Look uses full-picture horizontal paging and guarded label/recording playback. Find uses two picture-backed choices, automatically spoken questions and gentle retries. The detailed original collection and category filters remain in media options.
- Reels keep vertical paging; the side control panel is optional through a long hold. Adjacent pages are composed in advance; this is not a guarantee of decoded-video preloading or zero buffering.
- Balloons use two or three nonintersecting reserved positions, restrained vertical movement and full-sized hit regions during spawn. Existing worlds, modes, languages, discovery/challenge stages, progression, surprises and optional scenery remain reachable through the balloon hold menu.
- Coloring opens its palette and full tool set on demand, with enlarged swatches and a scrolling tool sheet.
- Stories default to narrated scene interactions. Existing text-question read-along mode and parent tips remain in story options. The door safety lesson has a purpose-drawn room and door, smooth restrained motion, and a trusted-adult sequence: the child keeps the door closed and calls a grown-up; opening occurs only in the final adult-assisted scene. Other stories reuse existing artwork and narration; bespoke animated scenes have not been authored for every story.
- The calm home, paged catalog, media, stories, balloons, music, coloring and memory support both orientations. Older spatial engines and parent/setup screens retain their original landscape orientation after emulator images exposed clipped portrait labels. Existing media is fitted to the available space; alternate portrait/landscape source variants have not been invented. Immersive system bars can still be revealed by Android's system gesture.

## Preservation

No catalog entry, activity ID, database entity, schema version, media resource, family-photo record, artwork format or media override format is changed. `scripts/verify-calm-preservation.sh` enforces those source-level invariants.

The preview has the separate application ID `com.aistudio.littlewonderworld.wvkqzp.calm`, so installation does not replace an existing signed installation. It starts with its own local progress; it does not copy data from another app ID. An in-place production update still requires the installed app's original signing key.

## Validation

The workflow runs the existing JVM regression suite plus a scaled-balloon geometry test. The focused emulator journey opens all fourteen engines, checks balloon separation after motion, exercises narrated door progression and picture choices, and captures home orientation screenshots. Results must be read from the completed workflow; adding these checks does not itself establish that they passed.

The entire older activity-specific UI has not been replaced: some advanced instrument, drawing, tracing and quiz controls still use their original layouts. Further visual adjustments must follow actual emulator evidence. Missing supplied artwork is not replaced with generated animal animation.
