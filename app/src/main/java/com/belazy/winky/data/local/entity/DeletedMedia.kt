package com.belazy.winky.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "deleted_media")
data class DeletedMedia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val path: String,
    val deletedAt: Date
)
