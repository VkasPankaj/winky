package com.belazy.winky.ui.screens.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.videoFrameMillis

@Composable
fun VideoThumbnailItem(
    media: com.belazy.winky.data.model.MediaFile,
    imageLoader: coil.ImageLoader,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(media.uri)
            .videoFrameMillis(1000) // Get frame at 1 second instead of 0
            .crossfade(300)
            .memoryCacheKey("video_thumb_${media.uri}") // Custom cache key
            .diskCacheKey("video_thumb_${media.uri}")
            .build(),
        imageLoader = imageLoader,
        contentDescription = "Video thumbnail",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    )
}