package com.belazy.winky.ui.screens.fileview

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.belazy.winky.ui.screens.fileview.tabs.ImageGridScreen
import com.belazy.winky.ui.screens.fileview.tabs.VideoGridScreen

@Composable
fun FileViewScreen(navController: NavController) {
    val tabItems = listOf("Images", "Videos")
    var selectedTab by remember { mutableIntStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabItems.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        when (selectedTab) {
            0 -> ImageGridScreen(navController)
            1 -> VideoGridScreen(navController)
        }
    }
}