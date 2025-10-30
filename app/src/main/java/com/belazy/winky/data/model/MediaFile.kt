package com.belazy.winky.data.model

import android.net.Uri
import java.util.Date


data class MediaFile(
    val uri: Uri,
    val displayName: String,
    val dateAdded: Date,
    val size: Long,
    val mimeType: String,
    val isVideo: Boolean,
    val durationMs: Long = 0L,
    val folderName: String = "Unknown"
)