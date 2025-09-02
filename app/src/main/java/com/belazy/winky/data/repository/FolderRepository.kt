package com.belazy.winky.data.repository

import android.content.Context
import android.provider.MediaStore
import com.belazy.winky.data.model.Folder

object FolderRepository {
    fun getFolders(context: Context): List<Folder> {
        // TODO: Implement grouping logic by folder
        return listOf(
            Folder("DCIM", 20),
            Folder("Downloads", 15)
        )
    }
}