package com.belazy.winky.ui.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.belazy.winky.data.model.MediaFile
import com.belazy.winky.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    application: Application,
    private val mediaRepository: MediaRepository
) : AndroidViewModel(application) {

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // State flows for UI
    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    private val _images = MutableStateFlow<List<MediaFile>>(emptyList())
    val images: StateFlow<List<MediaFile>> = _images.asStateFlow()

    private val _videos = MutableStateFlow<List<MediaFile>>(emptyList())
    val videos: StateFlow<List<MediaFile>> = _videos.asStateFlow()

    // Grouped data for UI
    val groupedImages: StateFlow<Map<String, List<MediaFile>>> = _images
        .map { images -> groupMediaByDate(images) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val groupedVideos: StateFlow<Map<String, List<MediaFile>>> = _videos
        .map { videos -> groupMediaByDate(videos) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Loading and error states
    val isLoading: StateFlow<Boolean> = mediaRepository.isLoading

    private var currentImagePage = 0
    private var currentVideoPage = 0
    private var isLoadingMore = false

    init {
        // Observe repository flows
        viewModelScope.launch {
            mediaRepository.imagesFlow.collect { images ->
                _images.value = images
            }
        }

        viewModelScope.launch {
            mediaRepository.videosFlow.collect { videos ->
                _videos.value = videos
            }
        }

        // Register content observer
        mediaRepository.registerMediaObserver(getApplication())
    }

    fun loadImages(forceRefresh: Boolean = false) {
        if (isLoadingMore && !forceRefresh) return

        viewModelScope.launch {
            isLoadingMore = true
            val page = if (forceRefresh) 0 else currentImagePage

            mediaRepository.getImages(
                context = getApplication(),
                page = page,
                forceRefresh = forceRefresh
            ).fold(
                onSuccess = {
                    if (forceRefresh) {
                        currentImagePage = 0
                    }
                    currentImagePage++
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message ?: "Failed to load images"
                    )
                }
            )
            isLoadingMore = false
        }
    }

    fun loadVideos(forceRefresh: Boolean = false) {
        if (isLoadingMore && !forceRefresh) return

        viewModelScope.launch {
            isLoadingMore = true
            val page = if (forceRefresh) 0 else currentVideoPage

            mediaRepository.getVideos(
                context = getApplication(),
                page = page,
                forceRefresh = forceRefresh
            ).fold(
                onSuccess = {
                    if (forceRefresh) {
                        currentVideoPage = 0
                    }
                    currentVideoPage++
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message ?: "Failed to load videos"
                    )
                }
            )
            isLoadingMore = false
        }
    }

    fun refreshMedia() {
        currentImagePage = 0
        currentVideoPage = 0
        mediaRepository.clearCache()
        loadImages(forceRefresh = true)
        loadVideos(forceRefresh = true)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun groupMediaByDate(mediaFiles: List<MediaFile>): Map<String, List<MediaFile>> {
        return mediaFiles.groupBy { mediaFile ->
            dateFormatter.format(mediaFile.dateAdded)
        }.toSortedMap(compareByDescending { it })
    }

    override fun onCleared() {
        super.onCleared()
        mediaRepository.unregisterMediaObserver(getApplication())
    }
}

data class GalleryUiState(
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)