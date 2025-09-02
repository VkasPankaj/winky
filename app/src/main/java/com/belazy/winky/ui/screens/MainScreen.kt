package com.belazy.winky.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.belazy.winky.ui.screens.detail.GalleryViewModel
import com.belazy.winky.ui.screens.detail.VideoGridViewModel
import com.belazy.winky.ui.screens.fileview.FileViewScreen
import com.belazy.winky.ui.screens.folderview.FolderViewScreen

@Composable
fun MainScreen(
    navController: NavController,
    videoViewModel: VideoGridViewModel = hiltViewModel(),
    imageViewModel: GalleryViewModel = hiltViewModel()
) {
    val tabItems = listOf("File View", "Folder View")
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        videoViewModel.loadVideos()
        imageViewModel.loadImages()
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
            0 -> FileViewScreen(navController)
            1 -> FolderViewScreen(navController)
        }
    }
}