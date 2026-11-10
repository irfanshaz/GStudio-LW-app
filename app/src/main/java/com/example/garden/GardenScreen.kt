package com.example.garden

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.R
import com.example.audio.VoiceAssetManifest
import com.example.data.ProfileSettingsEntity
import com.example.model.AgeBand
import com.example.viewmodel.WonderViewModel
import kotlinx.coroutines.delay

@Composable
fun SubjectLearningControls(settings: ProfileSettingsEntity, viewModel: WonderViewModel) {
    val activeAuditionStyle by viewModel.audio.activeAuditionStyle.collectAsState()
    val lastAuditionClipId by viewModel.audio.lastAuditionClipId.collectAsState()
    var selectedAuditionPlayful by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Reading and Math paths", style = MaterialTheme.typography.titleLarge)
            Text("Choose each subject separately. A garden already in progress keeps its starting levels until replay.")
            LearningSubject.entries.forEach { subject ->
                Text(if (subject == LearningSubject.READING) "Reading" else "Math", fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AgeBand.entries.forEach { band ->
                        val labels = if (subject == LearningSubject.READING)
                            listOf("Letter sounds", "Build words", "Read phrases")
                        else listOf("Count to 3", "Count to 5", "Add to 8")
                        FilterChip(
                            selected = settings.subjectLevel(subject) == band,
                            onClick = { viewModel.setSubjectLevel(subject, band.id) },
                            label = { Text(labels[band.ordinal]) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("subject_${subject.name}_${band.id}")
                        )
                    }
                }
            }
            Text("Garden instructions and story language", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("en" to "English", "te" to "తెలుగు").forEach { (code, label) ->
                    FilterChip(
                        selected = settings.instructionLanguage == code,
                        onClick = { viewModel.setInstructionLanguage(code) },
                        label = { Text(label) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("garden_lang_$code")
                    )
                }
            }
            Text("Telugu speech uses an installed offline Telugu voice. If unavailable, the instruction plays in English. English reading sounds stay in English.")
            val garden by viewModel.gardenState.collectAsState()
            garden?.let { progress ->
                Text("Garden play record", style = MaterialTheme.typography.titleMedium)
                Text("Gardens grown: ${progress.completedGardens}")
                GardenMission.stageNames.take(progress.stage).forEachIndexed { index, title ->
                    val support = when {
                        progress.hintsMask and (1 shl index) != 0 -> "finished with a hint"
                        progress.retriesMask and (1 shl index) != 0 -> "finished after another try"
                        else -> "finished on the first try"
                    }
                    Text("$title — $support")
                }
                if (progress.stage == 0) Text("Garden started. First stop in progress.")
            }
            Text("Sunny voice audition (6-line script)", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        selectedAuditionPlayful = false
                        viewModel.audio.playGardenAudition(playful = false)
                    },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("garden_audition_calm")
                ) {
                    Text("Calm")
                }
                Button(
                    onClick = {
                        selectedAuditionPlayful = true
                        viewModel.audio.playGardenAudition(playful = true)
                    },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("garden_audition_playful")
                ) {
                    Text("Playful")
                }
                OutlinedButton(
                    onClick = { viewModel.audio.stopSpeechOnly() },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("garden_audition_stop")
                ) {
                    Text("Stop")
                }
            }
            val auditionClips = if (selectedAuditionPlayful) {
                VoiceAssetManifest.gardenAuditionPlayfulClips
            } else {
                VoiceAssetManifest.gardenAuditionCalmClips
            }
            val anyBundledAudition = remember(auditionClips) {
                auditionClips.any { viewModel.audio.hasBundledRawVoiceClip(it.clipId) }
            }
            Text(
                text = if (anyBundledAudled(anyBundledAudition)) {
                    "Playing bundled studio WAV audition recordings (${if (selectedAuditionPlayful) "Playful" else "Calm"})."
                } else {
                    "Fallback device speech audition (${if (activeAuditionStyle == "playful" || selectedAuditionPlayful) "Playful" else "Calm"} delivery; studio WAV recordings pending review)."
                },
                fontSize = 13.sp,
                modifier = Modifier.testTag("garden_audition_status")
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                auditionClips.forEachIndexed { idx, entry ->
                    val isLastPlayed = lastAuditionClipId == entry.clipId
                    OutlinedButton(
                        onClick = { viewModel.audio.playGardenAuditionLine(entry, selectedAuditionPlayful) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("garden_audition_line_$idx"),
                        border = BorderStroke(
                            width = if (isLastPlayed) 2.dp else 1.dp,
                            color = if (isLastPlayed) Color(0xFF1B5E20) else Color(0xFF78909C)
                        )
                    ) {
                        Text(
                            text = "${idx + 1}. ${entry.displayCaption}",
                            fontSize = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

private fun anyBundledAudled(flag: Boolean): Boolean = flag

@Composable
fun GardenScreen(viewModel: WonderViewModel) {
    val saved by viewModel.gardenState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) { viewModel.startGarden() }

    val state = saved
    if (state == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var practice by remember { mutableStateOf<GardenProgress?>(null) }
    val active = practice ?: state
    var feedback by remember(active.stage, practice != null) { mutableStateOf("") }
    var replayDialog by remember { mutableStateOf(false) }

    // Sunny pose state machine:
    // Celebration triggers ONLY on an actual active mission completion event in act(),
    // never on initial composition, recomposition, practice/replay entry, or resuming a completed garden.
    var sunnyPose by remember { mutableStateOf(SunnyPose.IDLE) }
    var celebrationTriggerCount by remember { mutableIntStateOf(0) }
    var lastCelebratedTargetStage by remember { mutableIntStateOf(-1) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                sunnyPose = SunnyPose.IDLE
                viewModel.audio.stopAllAudio()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sunnyPose = SunnyPose.IDLE
            viewModel.audio.stopAllAudio()
        }
    }

    LaunchedEffect(celebrationTriggerCount, settings.reducedMotion) {
        if (celebrationTriggerCount <= 0) return@LaunchedEffect
        if (settings.reducedMotion) {
            sunnyPose = SunnyPose.IDLE
            return@LaunchedEffect
        }
        sunnyPose = SunnyPose.SHORT_RUN
        delay(SunnyPose.SHORT_RUN.durationMs.toLong())
        if (lastCelebratedTargetStage >= 4) {
            sunnyPose = SunnyPose.FLAG_PLANT
            delay(SunnyPose.FLAG_PLANT.durationMs.toLong())
            sunnyPose = SunnyPose.WAVE
            delay(SunnyPose.WAVE.durationMs.toLong())
        } else {
            sunnyPose = SunnyPose.WAVE
            delay(SunnyPose.WAVE.durationMs.toLong())
        }
        sunnyPose = SunnyPose.IDLE
    }

    val teluguClipId = GardenMission.teluguClipIdFor(active)
    val teluguAvailable = remember(settings.instructionLanguage, active.stage, active.mathLevelId) {
        viewModel.audio.canPlayTeluguPrompt(teluguClipId)
    }
    val promptSpec = remember(active, settings.instructionLanguage, teluguAvailable) {
        GardenMission.promptSpecFor(
            state = active,
            requestedLanguage = settings.instructionLanguage,
            teluguAvailable = teluguAvailable
        )
    }
    val prompt = promptSpec.visibleText

    fun speakCurrentPrompt() {
        val wantsTelugu = settings.instructionLanguage == "te" && active.stage != 0
        if (wantsTelugu) {
            viewModel.audio.speakLocalized(
                nativeCaption = GardenMission.teluguPromptFor(active),
                nativeSpeech = GardenMission.teluguPromptFor(active),
                phoneticFallbackSpeech = promptSpec.englishFallbackText,
                languageCode = "te",
                clipId = teluguClipId,
                fallbackClipId = promptSpec.englishFallbackClipId,
                category = promptSpec.category
            )
        } else {
            viewModel.audio.speakDirected(
                text = promptSpec.visibleText,
                category = promptSpec.category,
                clipId = promptSpec.clipId,
                interrupt = true
            )
        }
    }

    // Enter a step -> play one short instruction. No idle timers or nagging loops.
    LaunchedEffect(active.stage, active.readingIndex, practice != null, settings.instructionLanguage) {
        speakCurrentPrompt()
    }

    fun act(action: GardenAction) {
        val next = GardenMission.reduce(active, action)
        val hintSpec = GardenMission.hintSpecFor(active)
        feedback = if (action == GardenAction.Hint) {
            hintSpec.visibleText
        } else if (
            next.stage == active.stage &&
            action !is GardenAction.AddSeed &&
            action !is GardenAction.RemoveSeed &&
            next.readingIndex == active.readingIndex
        ) {
            "Try another choice. Take your time."
        } else {
            ""
        }

        if (practice != null) {
            // Practice mode: never changes the saved mission, never awards a second completion,
            // and returns cleanly to the active garden when the practiced stop finishes.
            if (next.stage != active.stage) {
                practice = null
                viewModel.audio.playSuccessChime()
            } else {
                practice = next
                if (feedback.isNotBlank() && action != GardenAction.Hint) {
                    viewModel.audio.speakPatientRetry(feedback)
                }
            }
        } else {
            val completedStopNow = next.stage > active.stage
            viewModel.actInGarden(action)
            if (completedStopNow) {
                lastCelebratedTargetStage = next.stage
                celebrationTriggerCount += 1
                viewModel.audio.playSuccessChime()
            } else if (feedback.isNotBlank() && action != GardenAction.Hint) {
                viewModel.audio.speakPatientRetry(feedback)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("garden_screen")
    ) {
        // Top navigation & speech bar: Back, Title, Quiet, Hear again
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    sunnyPose = SunnyPose.IDLE
                    viewModel.audio.stopAllAudio()
                    viewModel.navigateBack()
                },
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("garden_back")
                    .semantics { contentDescription = "Back to Little Wonder World home" }
            ) {
                Text("Back", fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Sunny's Garden",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF1B5E20),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = { viewModel.audio.stopSpeechOnly() },
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("garden_quiet")
                    .semantics { contentDescription = "Quiet: stop spoken voice" }
            ) {
                Text("Quiet", fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = { speakCurrentPrompt() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("garden_listen")
                    .semantics { contentDescription = "Hear instruction again" }
            ) {
                Text("Hear again", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Left column: Sunny Garden Scene + 4 visually distinct stops in a compact 2x2 grid
            // so all 4 stops fit above the fold on phone landscape while still scrolling for large fonts.
            Column(
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SunnyGardenScene(
                    stage = state.stage,
                    seedCount = active.seedCount,
                    sunnyPose = if (settings.reducedMotion) SunnyPose.IDLE else sunnyPose,
                    reducedMotion = settings.reducedMotion
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.completedGardens > 0) {
                            "🌻 ${state.completedGardens} garden${if (state.completedGardens == 1) "" else "s"} grown"
                        } else {
                            "🌱 Garden path (4 stops)"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "Finished stops can be practiced",
                        fontSize = 11.sp,
                        color = Color(0xFF33691E)
                    )
                }

                // 2x2 grid keeps all 4 stops above the fold on 411dp/360dp phone landscape screens
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (rowIdx in 0 until 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (colIdx in 0 until 2) {
                                val stopIndex = rowIdx * 2 + colIdx
                                val title = GardenMission.stageNames[stopIndex]
                                val stopState = GardenMission.stopStateFor(state.stage, stopIndex)
                                val isPracticingThis = practice?.stage == stopIndex
                                GardenStopCard(
                                    stopIndex = stopIndex,
                                    title = title,
                                    stopState = stopState,
                                    isPracticingThis = isPracticingThis,
                                    onClick = {
                                        if (stopState == GardenStopState.FINISHED) {
                                            practice = state.copy(
                                                stage = stopIndex,
                                                readingIndex = 0,
                                                seedCount = 0
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Right column: Active mission card, primary interactive choices, feedback, and Help me
            Column(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (practice != null) {
                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFF57F17)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Practicing finished stop (saved progress is safe)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF4E342E),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(
                                onClick = { practice = null },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("garden_return_active")
                            ) {
                                Text("Return to my garden")
                            }
                        }
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Color(0xFFA5D6A7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF1B5E20),
                            modifier = Modifier.testTag("garden_prompt")
                        )

                        when (active.stage) {
                            0 -> {
                                val choices = remember(active.readingLevelId, active.readingIndex) {
                                    when (active.readingLevelId) {
                                        "2-3" -> listOf("S", "M")
                                        "5-6" -> listOf("SUN", "SEED", "HAT")
                                        else -> listOf("S", "U", "N")
                                    }.shuffled()
                                }
                                if (active.readingLevelId == "4-5") {
                                    val builtSoFar = GardenMission.readingSequence("4-5")
                                        .take(active.readingIndex)
                                        .joinToString("")
                                    Text(
                                        text = "SUN  ☀   ${if (builtSoFar.isNotEmpty()) "($builtSoFar)" else ""}",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    choices.forEach { value ->
                                        Button(
                                            onClick = { act(GardenAction.ReadingChoice(value)) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF1E88E5),
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .heightIn(min = 60.dp)
                                                .testTag("garden_read_$value")
                                        ) {
                                            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    when (active.readingLevelId) {
                                        "2-3" -> {
                                            OutlinedButton(
                                                onClick = { viewModel.audio.speakPhonicsLetter('S', true) },
                                                modifier = Modifier
                                                    .heightIn(min = 48.dp)
                                                    .testTag("garden_hear_phonics_s")
                                            ) {
                                                Text("Hear S sound")
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.audio.speakPhonicsLetter('S', false) },
                                                modifier = Modifier
                                                    .heightIn(min = 48.dp)
                                                    .testTag("garden_hear_letter_s_name")
                                            ) {
                                                Text("Letter S name")
                                            }
                                        }
                                        "4-5" -> {
                                            OutlinedButton(
                                                onClick = { viewModel.audio.speakPhonicsClip("phonics_blend_sun_segmented") },
                                                modifier = Modifier
                                                    .heightIn(min = 48.dp)
                                                    .testTag("garden_hear_blend_sun")
                                            ) {
                                                Text("Blend S-U-N")
                                            }
                                        }
                                        "5-6" -> {
                                            val wholeWordClip = if (active.readingIndex == 0) {
                                                "phonics_word_sun_whole"
                                            } else {
                                                "phonics_word_seed_whole"
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.audio.speakPhonicsClip(wholeWordClip) },
                                                modifier = Modifier
                                                    .heightIn(min = 48.dp)
                                                    .testTag("garden_hear_word")
                                            ) {
                                                Text(if (active.readingIndex == 0) "Hear word SUN" else "Hear word SEED")
                                            }
                                        }
                                    }
                                }
                            }
                            1 -> {
                                Text(
                                    text = "${active.seedCount} seed${if (active.seedCount == 1) "" else "s"}  ${"• ".repeat(active.seedCount)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723),
                                    modifier = Modifier.testTag("garden_seed_count")
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { act(GardenAction.AddSeed) },
                                        enabled = active.seedCount < 10,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2E7D32),
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .heightIn(min = 52.dp)
                                            .testTag("garden_add_seed")
                                    ) {
                                        Text("Add seed", fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { act(GardenAction.RemoveSeed) },
                                        enabled = active.seedCount > 0,
                                        modifier = Modifier
                                            .heightIn(min = 52.dp)
                                            .testTag("garden_remove_seed")
                                    ) {
                                        Text("Take one out")
                                    }
                                    Button(
                                        onClick = { act(GardenAction.PlantSeeds) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFEF6C00),
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .heightIn(min = 52.dp)
                                            .testTag("garden_plant")
                                    ) {
                                        Text("Plant my seeds", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            2, 3 -> {
                                val choices = remember(active.stage) {
                                    (if (active.stage == 2) {
                                        listOf("sun" to "☀ Sun", "box" to "▣ Closed box")
                                    } else {
                                        listOf("water" to "💧 Water", "sand" to "🏖 Dry sand")
                                    }).shuffled()
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    choices.forEach { (id, label) ->
                                        Button(
                                            onClick = {
                                                if (active.stage == 2) {
                                                    act(GardenAction.NeedChoice(id))
                                                } else {
                                                    act(GardenAction.StoryChoice(id))
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF00897B),
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .heightIn(min = 60.dp)
                                                .testTag("garden_choice_$id")
                                        ) {
                                            Text(label, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            else -> {
                                Text(
                                    text = "🌻 Your garden is ready!",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20),
                                    modifier = Modifier.testTag("garden_complete")
                                )
                                Text(
                                    text = "With a grown-up, find three leaves at home. Look at their shapes together.",
                                    color = Color(0xFF2E4D36)
                                )
                                OutlinedButton(
                                    onClick = { replayDialog = true },
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("garden_grow_another")
                                ) {
                                    Text("Grow another garden", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (feedback.isNotBlank()) {
                    Text(
                        text = feedback,
                        color = Color(0xFF154734),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.testTag("garden_feedback")
                    )
                }
                if (active.stage < 4) {
                    OutlinedButton(
                        onClick = {
                            val hintSpec = GardenMission.hintSpecFor(active)
                            act(GardenAction.Hint)
                            viewModel.audio.speakInstruction(
                                text = hintSpec.visibleText,
                                interrupt = true,
                                clipId = hintSpec.clipId
                            )
                        },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("garden_hint")
                            .semantics { contentDescription = "Help me with a gentle hint" }
                    ) {
                        Text("Help me", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (replayDialog) {
        AlertDialog(
            onDismissRequest = { replayDialog = false },
            title = { Text("Grow another garden?") },
            text = {
                Text("Begin a new adventure using the Reading and Math levels selected in Parent Corner.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        sunnyPose = SunnyPose.IDLE
                        viewModel.startGarden(true)
                        replayDialog = false
                    },
                    modifier = Modifier.testTag("garden_replay_confirm")
                ) {
                    Text("Let's grow")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { replayDialog = false },
                    modifier = Modifier.testTag("garden_replay_dismiss")
                ) {
                    Text("Keep my garden")
                }
            }
        )
    }
}

@Composable
private fun GardenStopCard(
    stopIndex: Int,
    title: String,
    stopState: GardenStopState,
    isPracticingThis: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = when (stopState) {
        GardenStopState.FINISHED -> RoundedCornerShape(16.dp)
        GardenStopState.CURRENT -> CutCornerShape(topEnd = 10.dp, bottomStart = 10.dp)
        GardenStopState.UP_NEXT -> RoundedCornerShape(8.dp)
    }
    val containerColor = when {
        isPracticingThis -> Color(0xFFFFE082)
        stopState == GardenStopState.FINISHED -> Color(0xFFC8E6C9)
        stopState == GardenStopState.CURRENT -> Color(0xFFFFF8E1)
        else -> Color(0xFFF1F5F2)
    }
    val borderColor = when {
        isPracticingThis -> Color(0xFFE65100)
        stopState == GardenStopState.FINISHED -> Color(0xFF2E7D32)
        stopState == GardenStopState.CURRENT -> Color(0xFFF57F17)
        else -> Color(0xFF90A4AE)
    }
    val borderWidth = if (stopState == GardenStopState.CURRENT || isPracticingThis) 2.5.dp else 1.5.dp
    val contentColor = when (stopState) {
        GardenStopState.FINISHED -> Color(0xFF1B5E20)
        GardenStopState.CURRENT -> Color(0xFF3E2723)
        GardenStopState.UP_NEXT -> Color(0xFF455A64)
    }
    val statusSubtext = when {
        isPracticingThis -> "Practicing"
        stopState == GardenStopState.FINISHED -> "Finished • Practice"
        stopState == GardenStopState.CURRENT -> "Current stop"
        else -> "Up next"
    }
    val a11yDescription = "Stop ${stopIndex + 1}: $title. ${stopState.statusLabel}."

    OutlinedButton(
        onClick = onClick,
        enabled = stopState == GardenStopState.FINISHED,
        shape = shape,
        border = BorderStroke(borderWidth, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = modifier
            .heightIn(min = 48.dp)
            .testTag("garden_stop_$stopIndex")
            .semantics { contentDescription = a11yDescription }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "${stopState.badgeSymbol} $title",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = statusSubtext,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Renders the three matching scene states (`BARE_SOIL`, `SPROUT`, `BLOOM`) with consistent
 * horizon, terracotta pot, soil mound, and Sunny foot alignment (`TransformOrigin(0.5f, 0.92f)`).
 * Decorative canvas/artwork does not steal focus or taps from interactive controls.
 */
@Composable
private fun SunnyGardenScene(
    stage: Int,
    seedCount: Int,
    sunnyPose: SunnyPose,
    reducedMotion: Boolean
) {
    val sceneStage = GardenMission.sceneStageFor(stage)
    val targetTravelDp = when (stage) {
        0 -> 4f
        1 -> 14f
        2 -> 24f
        3 -> 34f
        else -> 44f
    }
    val travel by animateFloatAsState(
        targetValue = targetTravelDp,
        animationSpec = tween(durationMillis = if (reducedMotion) 0 else SunnyPose.SHORT_RUN.durationMs),
        label = "sunny_travel"
    )
    // Pose-specific subtle tilt around bottom-center foot pivot (0.5f, 0.92f) so feet stay aligned
    val poseTiltDegrees = if (reducedMotion) {
        0f
    } else {
        when (sunnyPose) {
            SunnyPose.IDLE -> 0f
            SunnyPose.SHORT_RUN -> 5f
            SunnyPose.WAVE -> -4f
            SunnyPose.FLAG_PLANT -> 3f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .border(1.5.dp, Color(0xFF81C784), RoundedCornerShape(14.dp))
            .background(Color(0xFFB3E5FC), RoundedCornerShape(14.dp))
            .testTag("garden_scene_${sceneStage.id}")
            .semantics {
                contentDescription = "${sceneStage.label}. ${sunnyPose.label}."
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        // Non-focusable state tags for deterministic UI verification
        Box(
            modifier = Modifier
                .size(1.dp)
                .testTag("garden_scene_stage_${sceneStage.id}")
        )
        Box(
            modifier = Modifier
                .size(1.dp)
                .testTag("garden_sunny_pose_${sunnyPose.id}")
        )

        Canvas(Modifier.fillMaxSize()) {
            val horizonY = size.height * 0.78f
            val potCenterX = size.width * 0.72f

            // Sky backdrop
            drawRect(Color(0xFFB3E5FC))

            // Warm sun in upper right
            val sunCenter = Offset(size.width * 0.86f, 20.dp.toPx())
            drawCircle(Color(0xFFFFD54F), 15.dp.toPx(), sunCenter)

            // Sunbeams appear once sunlight stop is reached (stage >= 3)
            if (stage >= 3) {
                drawLine(
                    color = Color(0x88FFE082),
                    start = sunCenter,
                    end = Offset(potCenterX, horizonY - 12.dp.toPx()),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Consistent ground horizon
            drawRect(
                color = Color(0xFF6D4C41),
                topLeft = Offset(0f, horizonY),
                size = Size(size.width, size.height - horizonY)
            )

            // Consistent terracotta pot & dark soil mound
            val potWidth = 42.dp.toPx()
            val potHeight = 22.dp.toPx()
            val potTopY = horizonY - potHeight * 0.65f
            drawRoundRect(
                color = Color(0xFFD84315),
                topLeft = Offset(potCenterX - potWidth / 2f, potTopY),
                size = Size(potWidth, potHeight),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
            // Soil mound inside top of pot
            drawOval(
                color = Color(0xFF3E2723),
                topLeft = Offset(potCenterX - potWidth * 0.44f, potTopY - 4.dp.toPx()),
                size = Size(potWidth * 0.88f, 9.dp.toPx())
            )

            // Stage 1: Show counted seeds resting in the pot soil
            if (stage == 1 && seedCount > 0) {
                val visibleSeeds = seedCount.coerceAtMost(8)
                for (i in 0 until visibleSeeds) {
                    val seedX = (potCenterX - potWidth * 0.32f) + (i * (potWidth * 0.64f / visibleSeeds.coerceAtLeast(1)))
                    drawCircle(
                        color = Color(0xFFFFCC80),
                        radius = 2.6.dp.toPx(),
                        center = Offset(seedX, potTopY)
                    )
                }
            }

            // Stage 2..3 (SPROUT) and Stage 4 (BLOOM)
            if (sceneStage == GardenSceneStage.SPROUT || sceneStage == GardenSceneStage.BLOOM) {
                val stemTopY = if (sceneStage == GardenSceneStage.BLOOM) {
                    size.height * 0.24f
                } else if (stage == 3) {
                    size.height * 0.44f
                } else {
                    size.height * 0.54f
                }
                // Green stem
                drawLine(
                    color = Color(0xFF2E7D32),
                    start = Offset(potCenterX, potTopY),
                    end = Offset(potCenterX, stemTopY),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Left and right sprout leaves
                drawOval(
                    color = Color(0xFF66BB6A),
                    topLeft = Offset(potCenterX - 22.dp.toPx(), (potTopY + stemTopY) * 0.52f),
                    size = Size(20.dp.toPx(), 10.dp.toPx())
                )
                drawOval(
                    color = Color(0xFF43A047),
                    topLeft = Offset(potCenterX + 2.dp.toPx(), (potTopY + stemTopY) * 0.48f),
                    size = Size(20.dp.toPx(), 10.dp.toPx())
                )

                // Stage 4 (BLOOM): Full golden sunflower blossom + water droplets
                if (sceneStage == GardenSceneStage.BLOOM) {
                    val flowerCenter = Offset(potCenterX, stemTopY)
                    for (petalIdx in 0 until 8) {
                        val angle = (petalIdx * 45.0) * (Math.PI / 180.0)
                        val petalOffset = Offset(
                            x = flowerCenter.x + (kotlin.math.cos(angle) * 11.dp.toPx()).toFloat(),
                            y = flowerCenter.y + (kotlin.math.sin(angle) * 11.dp.toPx()).toFloat()
                        )
                        drawCircle(Color(0xFFFDD835), 6.5.dp.toPx(), petalOffset)
                    }
                    drawCircle(Color(0xFF5D4037), 7.dp.toPx(), flowerCenter)

                    // Flag pole beside bloom
                    val flagX = size.width * 0.90f
                    drawLine(
                        color = Color(0xFF1B5E20),
                        start = Offset(flagX, horizonY),
                        end = Offset(flagX, size.height * 0.30f),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    val flagPath = Path().apply {
                        moveTo(flagX, size.height * 0.30f)
                        lineTo(flagX + 16.dp.toPx(), size.height * 0.38f)
                        lineTo(flagX, size.height * 0.46f)
                        close()
                    }
                    drawPath(flagPath, Color(0xFFFF7043))
                }
            }
        }

        // Sunny character with aligned bottom-center foot pivot (0.5f, 0.92f)
        Image(
            painter = painterResource(R.drawable.img_launcher_wonder_cub_1794160808743),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = travel.dp, y = (-4).dp)
                .size(64.dp)
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0.5f, 0.92f)
                    rotationZ = poseTiltDegrees
                }
        )

        // Pose & stage indicator badge
        val badgeText = when {
            sunnyPose == SunnyPose.WAVE -> "👋"
            sunnyPose == SunnyPose.FLAG_PLANT || stage == 4 -> "⚑ ✓"
            sunnyPose == SunnyPose.SHORT_RUN -> "🐾"
            else -> ""
        }
        if (badgeText.isNotEmpty()) {
            Text(
                text = badgeText,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 4.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
        }
    }
}
