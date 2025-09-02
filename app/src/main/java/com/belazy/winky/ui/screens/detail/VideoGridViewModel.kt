package com.belazy.winky.ui.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.belazy.winky.data.repository.MediaRepository
import com.belazy.winky.data.model.MediaFile
import com.belazy.winky.ui.screens.detail.ImageLoaderSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class VideoGridViewModel @Inject constructor(
    application: Application,
    private val mediaRepository: MediaRepository
) : AndroidViewModel(application) {

    // Use repository's StateFlow for videos
    val videos: StateFlow<List<MediaFile>> = mediaRepository.videosFlow

    // Use repository's loading state
    val isLoading: StateFlow<Boolean> = mediaRepository.isLoading

    private val _groupedVideos = MutableStateFlow<Map<String, List<MediaFile>>>(emptyMap())
    val groupedVideos: StateFlow<Map<String, List<MediaFile>>> = _groupedVideos.asStateFlow()

    private var currentPage = 0

    init {
        // Register content observer
        mediaRepository.registerMediaObserver(getApplication())

        // Observe videos flow and update grouped videos
        viewModelScope.launch {
            videos.collect { videosList ->
                updateGroupedVideos(videosList)
                if (videosList.isNotEmpty()) {
                    preloadThumbnails(videosList)
                }
            }
        }

        // Load initial videos
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch {
            try {
                val result = mediaRepository.getVideos(
                    context = getApplication(),
                    page = currentPage,
                    forceRefresh = false
                )

                result.onSuccess { newVideos ->
                    if (newVideos.isNotEmpty()) {
                        currentPage++
                    }
                }.onFailure { exception ->
                    // Handle error - you might want to emit this to a separate error flow
                    exception.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadMoreVideos() {
        if (!isLoading.value) {
            loadVideos()
        }
    }

    private fun updateGroupedVideos(videosList: List<MediaFile>) {
        val today = LocalDate.now()
        val grouped = videosList.groupBy { mediaFile ->
            val date = mediaFile.dateAdded.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            when {
                date == today -> "Today"
                date == today.minusDays(1) -> "Yesterday"
                else -> date.toString()
            }
        }
        _groupedVideos.value = grouped
    }

    private fun preloadThumbnails(videos: List<MediaFile>) {
        viewModelScope.launch(Dispatchers.IO) {
            val imageLoader = ImageLoaderSingleton.getInstance(getApplication())
            videos.forEach { media ->
                val request = ImageRequest.Builder(getApplication())
                    .data(media.uri)
                    .videoFrameMillis(1000L)
                    .size(150, 150)
                    .memoryCacheKey("video_thumb_${media.uri}")
                    .diskCacheKey("video_thumb_${media.uri}")
                    .build()
                imageLoader.enqueue(request)
            }
        }
    }

    fun refreshVideos() {
        viewModelScope.launch {
            mediaRepository.clearCache()
            currentPage = 0
            val result = mediaRepository.getVideos(
                context = getApplication(),
                page = 0,
                forceRefresh = true
            )

            result.onSuccess {
                currentPage = 1
            }.onFailure { exception ->
                exception.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Unregister content observer
        mediaRepository.unregisterMediaObserver(getApplication())
    }
}