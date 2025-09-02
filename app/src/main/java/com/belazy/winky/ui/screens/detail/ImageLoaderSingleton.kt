package com.belazy.winky.ui.screens.detail

import android.content.Context
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import kotlinx.coroutines.Dispatchers

object ImageLoaderSingleton {
    @Volatile
    private var INSTANCE: ImageLoader? = null

    fun getInstance(context: Context): ImageLoader {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildImageLoader(context).also { INSTANCE = it }
        }
    }

    private fun buildImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.4)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("video_thumbnails"))
                    .maxSizeBytes(500 * 1024 * 1024)
                    .build()
            }
            .dispatcher(Dispatchers.IO)
            .crossfade(300)
            .respectCacheHeaders(false)
            .build()
    }
}