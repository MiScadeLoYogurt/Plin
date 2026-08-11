package com.plin.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.plin.domain.models.Task

/**
 * Room handle for Plin's local SQLite database (`plin.db`).
 *
 * Defines which tables exist and provides access to [TaskDao].
 * The actual data file lives on the device; this class opens and manages it.
 */
@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PlinDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: PlinDatabase? = null

        fun getInstance(context: Context): PlinDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PlinDatabase::class.java,
                    "plin.db",
                ).build().also { instance = it }
            }
        }
    }
}
