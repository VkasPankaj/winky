package com.belazy.winky.data.repository

import com.belazy.winky.data.local.dao.DeletedMediaDao
import com.belazy.winky.data.local.entity.DeletedMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class TrashRepository(private val deletedMediaDao: DeletedMediaDao) {

    suspend fun moveToTrash(mediaPath: String) = withContext(Dispatchers.IO) {
        val deletedMedia = DeletedMedia(
            path = mediaPath,
            deletedAt = Date()
        )
        deletedMediaDao.insert(deletedMedia)
    }

    suspend fun getDeletedMedia(): List<DeletedMedia> = withContext(Dispatchers.IO) {
        deletedMediaDao.getAllDeletedMedia()
    }

    suspend fun restoreMedia(media: DeletedMedia) = withContext(Dispatchers.IO) {
        deletedMediaDao.delete(media)
    }

    suspend fun deleteExpiredMedia(daysThreshold: Int = 30) = withContext(Dispatchers.IO) {
        val thresholdDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -daysThreshold)
        }.time
        deletedMediaDao.deleteOlderThan(thresholdDate)
    }
}
