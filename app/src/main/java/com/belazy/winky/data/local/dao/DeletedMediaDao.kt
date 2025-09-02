package com.belazy.winky.data.local.dao

import androidx.room.*
import com.belazy.winky.data.local.entity.DeletedMedia
import java.util.*

@Dao
interface DeletedMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: DeletedMedia)

    @Delete
    suspend fun delete(media: DeletedMedia)

    @Query("SELECT * FROM deleted_media ORDER BY deletedAt DESC")
    suspend fun getAllDeletedMedia(): List<DeletedMedia>

    @Query("DELETE FROM deleted_media WHERE deletedAt < :thresholdDate")
    suspend fun deleteOlderThan(thresholdDate: Date)
}
