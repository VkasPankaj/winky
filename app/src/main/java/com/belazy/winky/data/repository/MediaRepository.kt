package com.belazy.winky.data.repository

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.belazy.winky.data.model.MediaFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor() {

    // Use StateFlow instead of callbacks for reactive programming
    private val _videosFlow = MutableStateFlow<List<MediaFile>>(emptyList())
    val videosFlow: StateFlow<List<MediaFile>> = _videosFlow.asStateFlow()

    private val _imagesFlow = MutableStateFlow<List<MediaFile>>(emptyList())
    val imagesFlow: StateFlow<List<MediaFile>> = _imagesFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Cache with proper memory management
    private var cachedVideos: List<MediaFile>? = null
    private var cachedImages: List<MediaFile>? = null

    private var videoObserver: ContentObserver? = null
    private var imageObserver: ContentObserver? = null

    companion object {
        private const val PAGE_SIZE = 50
        private const val CACHE_EXPIRY_MS = 5 * 60 * 1000L // 5 minutes
    }

    private var lastCacheTime: Long = 0

    suspend fun getVideos(
        context: Context,
        page: Int = 0,
        forceRefresh: Boolean = false
    ): Result<List<MediaFile>> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true

            if (shouldRefreshCache(forceRefresh)) {
                cachedVideos = null
            }

            if (cachedVideos == null) {
                cachedVideos = loadVideosFromMediaStore(context)
                lastCacheTime = System.currentTimeMillis()
            }

            val videos = cachedVideos ?: emptyList()
            val paginatedVideos = paginateResults(videos, page)

            _videosFlow.value = if (page == 0) paginatedVideos else _videosFlow.value + paginatedVideos

            Result.success(paginatedVideos)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun getImages(
        context: Context,
        page: Int = 0,
        forceRefresh: Boolean = false
    ): Result<List<MediaFile>> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true

            if (shouldRefreshCache(forceRefresh)) {
                cachedImages = null
            }

            if (cachedImages == null) {
                cachedImages = loadImagesFromMediaStore(context)
                lastCacheTime = System.currentTimeMillis()
            }

            val images = cachedImages ?: emptyList()
            val paginatedImages = paginateResults(images, page)

            _imagesFlow.value = if (page == 0) paginatedImages else _imagesFlow.value + paginatedImages

            Result.success(paginatedImages)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    private fun shouldRefreshCache(forceRefresh: Boolean): Boolean {
        return forceRefresh || (System.currentTimeMillis() - lastCacheTime > CACHE_EXPIRY_MS)
    }

    private fun paginateResults(items: List<MediaFile>, page: Int): List<MediaFile> {
        val start = page * PAGE_SIZE
        val end = minOf(start + PAGE_SIZE, items.size)
        return if (start < items.size) items.subList(start, end) else emptyList()
    }

    private suspend fun loadVideosFromMediaStore(context: Context): List<MediaFile> {
        return loadMediaFromStore(
            context = context,
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            isVideo = true
        )
    }

    private suspend fun loadImagesFromMediaStore(context: Context): List<MediaFile> {
        return loadMediaFromStore(
            context = context,
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            isVideo = false
        )
    }

    private suspend fun loadMediaFromStore(
        context: Context,
        uri: Uri,
        isVideo: Boolean
    ): List<MediaFile> = withContext(Dispatchers.IO) {
        val mediaFiles = mutableListOf<MediaFile>()
        val seenUris = mutableSetOf<String>()

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.MIME_TYPE
        )

        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"
        val selection = "${MediaStore.MediaColumns.SIZE} > ?"
        val selectionArgs = arrayOf("0") // Filter out 0-byte files

        try {
            context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
                val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val mimeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val name = cursor.getString(nameIndex) ?: "Unknown"
                    val dateAdded = cursor.getLong(dateIndex)
                    val size = cursor.getLong(sizeIndex)
                    val mimeType = cursor.getString(mimeIndex) ?: ""

                    val mediaUri = Uri.withAppendedPath(uri, id.toString())

                    if (seenUris.add(mediaUri.toString()) && size > 0) {
                        mediaFiles.add(
                            MediaFile(
                                uri = mediaUri,
                                isVideo = isVideo,
                                displayName = name,
                                dateAdded = Date(dateAdded * 1000),
                                size = size,
                                mimeType = mimeType,
                            )
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            // Handle permission issues gracefully
            throw IllegalStateException("Missing media access permissions", e)
        }

        mediaFiles
    }

    fun clearCache() {
        cachedVideos = null
        cachedImages = null
        lastCacheTime = 0
        _videosFlow.value = emptyList()
        _imagesFlow.value = emptyList()
    }

    fun registerMediaObserver(context: Context) {
        unregisterMediaObserver(context)

        videoObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                cachedVideos = null
            }
        }

        imageObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                cachedImages = null
            }
        }

        context.contentResolver.registerContentObserver(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            true,
            videoObserver!!
        )

        context.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            imageObserver!!
        )
    }

    fun unregisterMediaObserver(context: Context) {
        videoObserver?.let { context.contentResolver.unregisterContentObserver(it) }
        imageObserver?.let { context.contentResolver.unregisterContentObserver(it) }
        videoObserver = null
        imageObserver = null
    }
}