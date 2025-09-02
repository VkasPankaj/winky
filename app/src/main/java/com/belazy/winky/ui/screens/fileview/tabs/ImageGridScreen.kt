package com.belazy.winky.ui.screens.fileview.tabs

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.belazy.winky.data.model.MediaFile
import com.belazy.winky.ui.screens.detail.GalleryViewModel
import com.belazy.winky.ui.screens.detail.ImageLoaderSingleton

@Composable
fun ImageGridScreen(
    navController: NavController,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val groupedImages by viewModel.groupedImages.collectAsState()
    val images by viewModel.images.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val gridState = rememberLazyGridState()

    // Load initial data
    LaunchedEffect(Unit) {
        if (images.isEmpty()) {
            viewModel.loadImages()
        }
    }

    // Infinite scrolling
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= images.size - 10 && !isLoading) {
                    viewModel.loadImages()
                }
            }
    }

    // Handle errors
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error UI
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            images.isEmpty() && isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            images.isEmpty() && !isLoading -> {
                EmptyStateContent(
                    onRefresh = { viewModel.refreshMedia() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    state = gridState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                ) {
                    groupedImages.entries.forEach { (date, mediaList) ->
                        // Date header
                        item(
                            key = "header_$date",
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            DateHeader(date = date)
                        }

                        // Media items
                        items(
                            items = mediaList,
                            key = { media -> "image_${media.uri}" }
                        ) { media ->
                            ImageGridItem(
                                media = media,
                                onClick = {
                                    // Pass URI instead of index for safe navigation
                                    val encodedUri = Uri.encode(media.uri.toString())
                                    navController.navigate("detail/$encodedUri")
                                }
                            )
                        }
                    }

                    // Loading footer
                    if (isLoading) {
                        item(
                            key = "loading",
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun ImageGridItem(
    media: MediaFile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoaderSingleton.getInstance(context) }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(media.uri)
                .size(200, 200)
                .memoryCacheKey("thumb_${media.uri}")
                .diskCacheKey("thumb_${media.uri}")
                .crossfade(true)
                .build(),
            contentDescription = media.displayName,
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
        )
    }
}

@Composable
private fun EmptyStateContent(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No images found",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap the button below to refresh",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onRefresh,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Refresh")
        }
    }
}
