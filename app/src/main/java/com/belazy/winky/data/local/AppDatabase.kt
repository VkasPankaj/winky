package com.belazy.winky.data.local

import android.content.Context
import androidx.room.*
import com.belazy.winky.data.local.dao.DeletedMediaDao
import com.belazy.winky.data.local.dao.HiddenMediaDao
import com.belazy.winky.data.local.entity.DeletedMedia
import com.belazy.winky.data.local.entity.HiddenMedia

@Database(
    entities = [DeletedMedia::class, HiddenMedia::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deletedMediaDao(): DeletedMediaDao
    abstract fun hiddenMediaDao(): HiddenMediaDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "winky_db"
            ).build()
    }
}
