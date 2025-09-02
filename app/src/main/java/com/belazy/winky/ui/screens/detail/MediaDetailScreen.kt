package com.belazy.winky.ui.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.belazy.winky.ui.screens.detail.GalleryViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment

@Composable
fun MediaDetailScreen(
    clickedMediaUri: String,
    navController: NavController,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val images by viewModel.images.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Find the initial index of the clicked image
    val initialIndex = remember(clickedMediaUri, images) {
        images.indexOfFirst { it.uri.toString() == clickedMediaUri }.takeIf { it >= 0 } ?: 0
    }

    // Create the full list of image URIs in the correct order
    val imageUris = remember(images) {
        images.map { it.uri.toString() }
    }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { imageUris.size }
    )

    // Lazy-load more images when approaching the end
    LaunchedEffect(pagerState.currentPage, images.size) {
        if (pagerState.currentPage >= images.size - 5 && !isLoading) {
            viewModel.loadImages()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (imageUris.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    val uri = imageUris.getOrNull(page)
                    if (uri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { /* TODO: Delete */ }) { Text("Delete") }
                    OutlinedButton(onClick = { /* TODO: Hide */ }) { Text("Hide") }
                    OutlinedButton(onClick = { /* TODO: Info */ }) { Text("Info") }
                    OutlinedButton(onClick = { /* TODO: Edit */ }) { Text("Edit") }
                }
            }
        }
    }
}