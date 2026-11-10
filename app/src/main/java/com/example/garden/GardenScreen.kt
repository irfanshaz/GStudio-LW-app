package com.example.garden

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ProfileSettingsEntity
import com.example.model.AgeBand
import com.example.viewmodel.WonderViewModel

@Composable
fun SubjectLearningControls(settings: ProfileSettingsEntity, viewModel: WonderViewModel) {
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
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                .testTag("subject_${subject.name}_${band.id}")
                        )
                    }
                }
            }
            Text("Garden instructions and story language", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("en" to "English", "te" to "తెలుగు").forEach { (code, label) ->
                    FilterChip(selected = settings.instructionLanguage == code,
                        onClick = { viewModel.setInstructionLanguage(code) },
                        label = { Text(label) }, modifier = Modifier.heightIn(min = 48.dp))
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
            Text("Sunny voice audition", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(false to "Calm", true to "Playful").forEach { (playful, label) ->
                    Button(onClick = { viewModel.audio.playGardenAudition(playful) }) { Text(label) }
                }
                OutlinedButton(onClick = { viewModel.audio.stopSpeechOnly() }) { Text("Stop") }
            }
            Text("These are two deliveries using this device's selected voice. Studio recordings will be auditioned separately.")
        }
    }
}

private fun gardenPrompt(state: GardenProgress): String = when (state.stage) {
    0 -> when (state.readingLevelId) {
        "2-3" -> "Seed starts with S. Find S."
        "5-6" -> if (state.readingIndex == 0) "Plants need sunlight. Find SUN." else "We plant a seed. Find SEED."
        else -> "Build SUN. Find ${GardenMission.readingSequence(state.readingLevelId)[state.readingIndex]}."
    }
    1 -> if (state.mathLevelId == "5-6") "Plant three seeds, then five more. How many altogether?" else "Put ${GardenMission.seedTarget(state.mathLevelId)} seeds in the pot."
    2 -> "What gives the plant light?"
    3 -> "Sunny planted seeds. The soil is dry. What should Sunny give the seeds?"
    else -> "Your garden is growing! You helped Sunny read, count and care for a plant."
}

private fun gardenTelugu(state: GardenProgress): String = when (state.stage) {
    1 -> if (state.mathLevelId == "5-6") "మూడు విత్తనాలు, తరువాత మరో ఐదు విత్తనాలు నాటు. మొత్తం ఎన్ని?" else "కుండలో ${GardenMission.seedTarget(state.mathLevelId)} విత్తనాలు వేయి."
    2 -> "మొక్కకు వెలుతురు ఏది ఇస్తుంది?"
    3 -> "సన్నీ విత్తనాలు నాటింది. మట్టి పొడిగా ఉంది. విత్తనాలకు ఏమి ఇవ్వాలి?"
    else -> "నీ తోట పెరుగుతోంది! మొక్కను పెంచడానికి సన్నీకి సహాయం చేశావు."
}

@Composable
fun GardenScreen(viewModel: WonderViewModel) {
    val saved by viewModel.gardenState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    LaunchedEffect(Unit) { viewModel.startGarden() }
    DisposableEffect(Unit) { onDispose { viewModel.audio.stopAllAudio() } }
    val state = saved
    if (state == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    var practice by remember { mutableStateOf<GardenProgress?>(null) }
    val active = practice ?: state
    var feedback by remember(active.stage, practice != null) { mutableStateOf("") }
    var replayDialog by remember { mutableStateOf(false) }
    val english = gardenPrompt(active)
    val telugu = settings.instructionLanguage == "te" && active.stage != 0
    val prompt = if (telugu) gardenTelugu(active) else english
    fun speak() {
        if (telugu) viewModel.audio.speakLocalized(prompt, prompt, english, "te")
        else viewModel.audio.speakInstruction(english)
    }
    LaunchedEffect(active.stage, active.readingIndex, practice != null) { speak() }
    fun act(action: GardenAction) {
        val next = GardenMission.reduce(active, action)
        feedback = if (action == GardenAction.Hint) when (active.stage) {
            0 -> "Look for ${GardenMission.readingSequence(active.readingLevelId)[active.readingIndex]}."
            1 -> "Touch one seed at a time. We need ${GardenMission.seedTarget(active.mathLevelId)}."
            2 -> "Look up in the daytime sky."
            else -> "Dry soil needs water."
        } else if (next.stage == active.stage && action !is GardenAction.AddSeed && action !is GardenAction.RemoveSeed && next.readingIndex == active.readingIndex) "Try another choice. Take your time." else ""
        if (practice != null) {
            if (next.stage != active.stage) { practice = null; viewModel.audio.playSuccessChime() }
            else practice = next
        } else viewModel.actInGarden(action)
    }
    Column(Modifier.fillMaxSize().background(Color(0xFFE8F5E9)).padding(12.dp).testTag("garden_screen")) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { viewModel.navigateBack() }, modifier = Modifier.testTag("garden_back")) { Text("Back") }
            Text("Sunny's Garden", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { viewModel.audio.stopSpeechOnly() }) { Text("Quiet") }
            Button(onClick = { speak() }, modifier = Modifier.testTag("garden_listen")) { Text("Hear again") }
        }
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(0.36f).fillMaxHeight().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                SunnyGardenScene(state.stage, settings.reducedMotion)
                if (state.completedGardens > 0) Text("🌻 ${state.completedGardens} garden${if (state.completedGardens == 1) "" else "s"} grown", fontWeight = FontWeight.Bold)
                GardenMission.stageNames.forEachIndexed { index, title ->
                    val complete = state.stage > index
                    OutlinedButton(
                        onClick = { if (complete) practice = state.copy(stage = index, readingIndex = 0, seedCount = 0) },
                        enabled = complete,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("garden_stop_$index")
                    ) { Text("${if (complete) "⚑ ✓" else if (state.stage == index) "▶" else "○"} $title") }
                }
                Text("Finished stops can be played again.", fontSize = 12.sp)
            }
            Column(Modifier.weight(0.64f).fillMaxHeight().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (practice != null) OutlinedButton(onClick = { practice = null }) { Text("Return to my garden") }
                Text(prompt, style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("garden_prompt"))
                when (active.stage) {
                    0 -> {
                        val choices = remember(active.readingLevelId, active.readingIndex) {
                            when (active.readingLevelId) {
                                "2-3" -> listOf("S", "M")
                                "5-6" -> listOf("SUN", "SEED", "HAT")
                                else -> listOf("S", "U", "N")
                            }.shuffled()
                        }
                        if (active.readingLevelId == "4-5") Text("SUN  ☀", fontSize = 32.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            choices.forEach { value -> Button(onClick = { act(GardenAction.ReadingChoice(value)) }, modifier = Modifier.weight(1f).height(64.dp).testTag("garden_read_$value")) { Text(value, fontSize = 22.sp) } }
                        }
                        if (active.readingLevelId == "2-3") OutlinedButton(onClick = { viewModel.audio.speakPhonicsLetter('S', true) }) { Text("Hear S sound") }
                    }
                    1 -> {
                        Text("${active.seedCount} seed${if (active.seedCount == 1) "" else "s"}  ${"• ".repeat(active.seedCount)}", fontSize = 26.sp, modifier = Modifier.testTag("garden_seed_count"))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { act(GardenAction.AddSeed) }, enabled = active.seedCount < 10, modifier = Modifier.height(56.dp).testTag("garden_add_seed")) { Text("Add seed") }
                            OutlinedButton(onClick = { act(GardenAction.RemoveSeed) }, enabled = active.seedCount > 0, modifier = Modifier.height(56.dp).testTag("garden_remove_seed")) { Text("Take one out") }
                        }
                        Button(onClick = { act(GardenAction.PlantSeeds) }, modifier = Modifier.height(56.dp).testTag("garden_plant")) { Text("Plant my seeds") }
                    }
                    2, 3 -> {
                        val choices = remember(active.stage) {
                            (if (active.stage == 2) listOf("sun" to "☀ Sun", "box" to "▣ Closed box") else listOf("water" to "💧 Water", "sand" to "🏖 Dry sand")).shuffled()
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            choices.forEach { (id, label) -> Button(onClick = { if (active.stage == 2) act(GardenAction.NeedChoice(id)) else act(GardenAction.StoryChoice(id)) }, modifier = Modifier.weight(1f).heightIn(min = 64.dp).testTag("garden_choice_$id")) { Text(label, fontSize = 20.sp) } }
                        }
                    }
                    else -> {
                        Text("🌻 Your garden is ready!", fontSize = 26.sp, modifier = Modifier.testTag("garden_complete"))
                        Text("With a grown-up, find three leaves at home. Look at their shapes together.")
                        OutlinedButton(onClick = { replayDialog = true }) { Text("Grow another garden") }
                    }
                }
                if (feedback.isNotBlank()) Text(feedback, color = Color(0xFF154734), modifier = Modifier.testTag("garden_feedback"))
                if (active.stage < 4) OutlinedButton(onClick = { act(GardenAction.Hint); viewModel.audio.speakInstruction(feedback) }, modifier = Modifier.testTag("garden_hint")) { Text("Help me") }
            }
        }
    }
    if (replayDialog) AlertDialog(onDismissRequest = { replayDialog = false }, title = { Text("Grow another garden?") }, text = { Text("Begin a new adventure using the Reading and Math levels selected in Parent Corner.") }, confirmButton = { TextButton(onClick = { viewModel.startGarden(true); replayDialog = false }) { Text("Let's grow") } }, dismissButton = { TextButton(onClick = { replayDialog = false }) { Text("Keep my garden") } })
}

@Composable
private fun SunnyGardenScene(stage: Int, reducedMotion: Boolean) {
    val travel by animateFloatAsState(if (stage == 4) 28f else stage * 6f, animationSpec = tween(if (reducedMotion) 0 else 700), label = "sunny_travel")
    Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.BottomCenter) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Color(0xFFB3E5FC))
            drawCircle(Color(0xFFFFD54F), 18.dp.toPx(), Offset(size.width * .82f, 24.dp.toPx()))
            drawRect(Color(0xFF795548), Offset(0f, size.height * .8f), androidx.compose.ui.geometry.Size(size.width, size.height * .2f))
            if (stage >= 2) {
                val top = if (stage == 4) .28f else .58f
                drawLine(Color(0xFF388E3C), Offset(size.width * .72f, size.height * .82f), Offset(size.width * .72f, size.height * top), 6.dp.toPx(), StrokeCap.Round)
                drawOval(Color(0xFF66BB6A), Offset(size.width * .64f, size.height * .56f), androidx.compose.ui.geometry.Size(26.dp.toPx(), 12.dp.toPx()))
                if (stage == 4) {
                    drawCircle(Color(0xFFFDD835), 18.dp.toPx(), Offset(size.width * .72f, size.height * top))
                    drawCircle(Color(0xFF795548), 8.dp.toPx(), Offset(size.width * .72f, size.height * top))
                }
            }
        }
        Image(painterResource(R.drawable.img_launcher_wonder_cub_1794160808743), "Sunny the cub", Modifier.align(Alignment.BottomStart).offset(x = travel.dp).size(76.dp))
        if (stage == 4) Text("⚑ ✓", Modifier.align(Alignment.BottomEnd), fontSize = 26.sp)
    }
}
