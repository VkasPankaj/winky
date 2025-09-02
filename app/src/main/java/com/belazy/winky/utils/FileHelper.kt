package com.belazy.winky.utils

import java.io.File

object FileHelper {

    fun getParentFolderName(path: String): String {
        val file = File(path)
        return file.parentFile?.name ?: "Unknown"
    }

    fun deleteFile(path: String): Boolean {
        val file = File(path)
        return file.exists() && file.delete()
    }
}
