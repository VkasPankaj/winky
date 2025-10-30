package com.belazy.winky.ui.screens.folderview

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
fun FolderDetailScreen(
    navController: NavController,
    folderName: String,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val groupedMedia by viewModel.groupedMediaByFolder.collectAsState()

    // Get media for this folder
    val mediaList = groupedMedia[folderName].orEmpty()
    val groupedByDate = remember(mediaList) {
        mediaList.groupBy { media ->
            viewModel.run {
                // Reuse date formatter from ViewModel
                media.dateAdded?.let { dateFormatter.format(it) } ?: "Unknown"
            }
        }.toSortedMap(compareByDescending { it })
    }

    Scaffold(

    ) { paddingValues ->
        if (mediaList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0A0A))
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No media found",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0A0A))
                    .padding(paddingValues)
            ) {
                groupedByDate.entries.forEach { (date, mediaGroup) ->
                    // Date header
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = date,
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    // Media items
                    items(mediaGroup, key = { it.uri.toString() }) { media ->
                        FolderMediaItem(media = media) {
                            val encodedUri = Uri.encode(media.uri.toString())
                            navController.navigate("detail/$encodedUri")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FolderMediaItem(media: MediaFile, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoaderSingleton.getInstance(context) }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(media.uri)
                    .size(200, 200)
                    .crossfade(true)
                    .build(),
                contentDescription = media.displayName,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                        )
                    )
            )
        }
    }
}
