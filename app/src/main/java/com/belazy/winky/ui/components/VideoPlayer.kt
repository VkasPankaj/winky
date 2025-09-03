@file:OptIn(androidx.media3.common.util.UnstableApi::class)

package com.belazy.winky.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

sealed class PlayerState {
    object Loading : PlayerState()
    object Ready : PlayerState()
    data class Error(val message: String) : PlayerState()
}

@Composable
fun VideoPlayer(
    uri: Uri,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false,
    showControls: Boolean = true,
    onError: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnError by rememberUpdatedState(onError)

    var playerState by remember { mutableStateOf<PlayerState>(PlayerState.Loading) }
    var isPlayerReady by remember { mutableStateOf(false) }

    // 🔹 Aspect ratio toggle state
    var currentResizeMode by remember { mutableStateOf(AspectRatioFrameLayout.RESIZE_MODE_FILL) }

    val exoPlayer = remember(uri) {
        ExoPlayer.Builder(context).build()
    }

    fun preparePlayer() {
        try {
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = autoPlay
        } catch (e: Exception) {
            val errorMsg = "Failed to load video: ${e.message}"
            playerState = PlayerState.Error(errorMsg)
            latestOnError?.invoke(errorMsg)
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                playerState = when (playbackState) {
                    Player.STATE_READY -> {
                        isPlayerReady = true
                        PlayerState.Ready
                    }
                    Player.STATE_BUFFERING -> PlayerState.Loading
                    else -> playerState
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                val errorMsg = "Playback error: ${error.message}"
                playerState = PlayerState.Error(errorMsg)
                latestOnError?.invoke(errorMsg)
            }
        }

        exoPlayer.addListener(listener)
        preparePlayer()

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> if (isPlayerReady) exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> if (isPlayerReady && autoPlay) exoPlayer.play()
                Lifecycle.Event.ON_DESTROY -> exoPlayer.release()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        when (val state = playerState) {
            is PlayerState.Loading -> LoadingIndicator(Modifier.align(Alignment.Center))
            is PlayerState.Ready -> {
                // 🔹 Video Player
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = showControls
                            resizeMode = currentResizeMode
                            setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                        }
                    },
                    update = {
                        it.player = exoPlayer
                        it.useController = showControls
                        it.resizeMode = currentResizeMode
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // 🔹 Overlay button like YouTube (Top-right)
                IconButton(
                    onClick = {
                        currentResizeMode =
                            if (currentResizeMode == AspectRatioFrameLayout.RESIZE_MODE_FILL)
                                AspectRatioFrameLayout.RESIZE_MODE_FIT
                            else
                                AspectRatioFrameLayout.RESIZE_MODE_FILL
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = "Toggle Aspect Ratio",
                        tint = Color.White
                    )
                }
            }
            is PlayerState.Error -> {
                ErrorDisplay(
                    message = state.message,
                    onRetry = {
                        playerState = PlayerState.Loading
                        preparePlayer()
                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Loading video...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Composable
private fun ErrorDisplay(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onRetry,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Retry")
        }
    }
}
