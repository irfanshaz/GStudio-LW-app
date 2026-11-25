package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActivityProgressEntity
import com.example.data.WonderCatalogRegistry
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.WonderScreen
import com.example.viewmodel.WonderViewModel

@Composable
private fun CalmCatalog(title: String, labels: List<String>, viewModel: WonderViewModel,
    initialPage: Int = 0, tag: String, onChoose: (Int) -> Unit) {
    var page by remember(labels) { mutableIntStateOf(initialPage.coerceIn(0, ((labels.size - 1) / 4).coerceAtLeast(0))) }
    BoxWithConstraints(Modifier.fillMaxSize().background(SoftCreamCard).testTag(tag)) {
        val columns = if (maxWidth >= 650.dp) 4 else 2
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Button(onClick = { viewModel.navigateBack() }, modifier = Modifier.size(90.dp)) { Text("Back") }
                Text(title, color = DeepSlateInk, style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f))
                ParentHoldTarget("catalog_parent_hold") { viewModel.navigateTo(WonderScreen.ParentCorner(unlocked = true)) }
            }
            val visible = labels.indices.drop(page * 4).take(4)
            visible.chunked(columns).forEach { indices ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    indices.forEach { index ->
                        Button(onClick = { onChoose(index) }, shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PlaySurfaceWhite, contentColor = DeepSlateInk),
                            modifier = Modifier.weight(1f).heightIn(min = 110.dp).testTag("catalog_item_$index")) {
                            Text(labels[index], fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            if (labels.size > 4) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Button(onClick = { page-- }, enabled = page > 0,
                    modifier = Modifier.weight(1f).height(90.dp).testTag("catalog_previous")) { Text("Previous") }
                Button(onClick = { page++ }, enabled = (page + 1) * 4 < labels.size,
                    modifier = Modifier.weight(1f).height(90.dp).testTag("catalog_next")) { Text("More") }
            }
        }
    }
}

@Composable
fun DetailedHomeScreen(viewModel: WonderViewModel, ageBand: AgeBand, buddy: LearningBuddy,
    progressList: List<ActivityProgressEntity>, reducedMotion: Boolean) {
    CalmCatalog("Choose a world", LandId.entries.map { "${it.emoji}  ${it.title}" }, viewModel,
        tag = "calm_all_lands") { viewModel.navigateTo(WonderScreen.LandWorlds(LandId.entries[it])) }
}

@Composable
fun LandWorldsScreen(landId: LandId, viewModel: WonderViewModel, progressList: List<ActivityProgressEntity>) {
    val worlds = remember(landId) { WonderCatalogRegistry.getWorldsForLand(landId) }
    CalmCatalog(landId.title, worlds.map { "${it.emoji}  ${it.title}" }, viewModel,
        tag = "land_worlds_screen_${landId.id}") { viewModel.navigateTo(WonderScreen.WorldActivities(worlds[it])) }
}

@Composable
fun WorldActivitiesScreen(world: WorldSpec, pageIndex: Int, viewModel: WonderViewModel,
    progressList: List<ActivityProgressEntity>) {
    val activities = remember(world.id) { WonderCatalogRegistry.getActivitiesForWorld(world.id) }
    CalmCatalog(world.title, activities.map { "${it.emoji}  ${it.title}" }, viewModel,
        initialPage = pageIndex, tag = "world_activities_screen_${world.id}") {
        viewModel.navigateTo(WonderScreen.ActivityPlay(activities[it]))
    }
}


@Composable
fun ToyBoxScreen(viewModel: WonderViewModel, progressList: List<ActivityProgressEntity>,
    savedArtworks: List<com.example.data.SavedArtworkEntity>) {
    var fullShelf by remember { mutableStateOf(false) }
    if (fullShelf) {
        LegacyToyBoxScreen(viewModel, progressList, savedArtworks)
        return
    }
    val toys = remember(progressList) {
        val favoriteIds = progressList.filter { it.isFavorite }.map { it.activityId }
        val recentIds = progressList.filter { it.lastPlayedEpochMs > 0 }.sortedByDescending { it.lastPlayedEpochMs }.map { it.activityId }
        (favoriteIds + recentIds + WonderCatalogRegistry.getToyBoxShortcuts().map { it.id }).distinct()
            .mapNotNull { WonderCatalogRegistry.getActivityById(it) }
    }
    CalmCatalog("My toys", toys.map { "${it.emoji}  ${it.title}" } + "Saved art & complete shelf",
        viewModel, tag = "toy_box_screen") { index ->
        if (index == toys.size) fullShelf = true
        else viewModel.navigateTo(WonderScreen.ActivityPlay(toys[index]))
    }
}
