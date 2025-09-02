package com.belazy.winky.ui.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun MediaDetailScreen(
    startIndex: Int,
    type: String,
    navController: NavController,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val images by viewModel.images.collectAsState()
    var isLoading by remember { mutableStateOf(images.isEmpty()) }

    LaunchedEffect(Unit) {
        viewModel.loadImages()
    }

    LaunchedEffect(images) {
        isLoading = images.isEmpty()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val pagerState = rememberPagerState(
                initialPage = startIndex.coerceIn(0, images.size - 1),
                initialPageOffsetFraction = 0f,
                pageCount = { images.size }
            )

            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    val media = images[page]
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(media.uri)
                                .size(1080, 1080)
                                .memoryCacheKey("image_thumb_${media.uri}")
                                .diskCacheKey("image_thumb_${media.uri}")
                                .build()
                        ),
                        contentDescription = media.displayName,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { /* TODO: Delete */ }) {
                        Text("Delete")
                    }
                    OutlinedButton(onClick = { /* TODO: Hide */ }) {
                        Text("Hide")
                    }
                    OutlinedButton(onClick = { /* TODO: Info */ }) {
                        Text("Info")
                    }
                    OutlinedButton(onClick = { /* TODO: Edit */ }) {
                        Text("Edit")
                    }
                }
            }
        }
    }
}