package com.belazy.winky.data.local.dao

import androidx.room.*
import com.belazy.winky.data.local.entity.HiddenMedia
import java.util.*

@Dao
interface HiddenMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: HiddenMedia)

    @Delete
    suspend fun delete(media: HiddenMedia)

    @Query("SELECT * FROM hidden_media ORDER BY hiddenAt DESC")
    suspend fun getAllHiddenMedia(): List<HiddenMedia>

    @Query("DELETE FROM hidden_media WHERE hiddenAt < :thresholdDate")
    suspend fun deleteOlderThan(thresholdDate: Date)
}
