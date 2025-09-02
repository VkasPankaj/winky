package com.belazy.winky.ui.screens.fileview.tabs

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.belazy.winky.ui.screens.detail.ImageLoaderSingleton
import com.belazy.winky.ui.screens.detail.VideoGridViewModel
import androidx.compose.ui.graphics.Color

@Composable
fun VideoGridScreen(
    navController: NavController,
    viewModel: VideoGridViewModel = viewModel()
) {
    val context = LocalContext.current
    val groupedVideos by viewModel.groupedVideos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val gridState = rememberLazyGridState()
    val imageLoader = remember { ImageLoaderSingleton.getInstance(context) }

    LaunchedEffect(Unit) {
        viewModel.loadVideos()
    }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= viewModel.videos.value.size - 5 && !isLoading) {
                    viewModel.loadVideos()
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // set black background
    ) {
        when {
            isLoading && groupedVideos.isEmpty() -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
            groupedVideos.isNotEmpty() -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    state = gridState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    groupedVideos.entries.forEach { (date, videos) ->
                        item(
                            key = "header_$date",
                            span = { GridItemSpan(3) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(
                                        Color.Transparent,
                                    )
                                    .padding(8.dp),
                            ) {
                                Text(
                                    text = date,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                        items(
                            items = videos,
                            key = { "video_${date}_${it.uri}" }
                        ) { media ->
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(media.uri)
                                    .videoFrameMillis(1000L)
                                    .size(150, 150)
                                    .crossfade(300)
                                    .memoryCacheKey("video_thumb_${media.uri}")
                                    .diskCacheKey("video_thumb_${media.uri}")
                                    .build(),
                                imageLoader = imageLoader,
                                contentDescription = "Video thumbnail",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .border(1.dp, Color.Gray)
                                    .clickable {
                                        val encoded = Uri.encode(media.uri.toString())
                                        navController.navigate("video_detail/$encoded")
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
