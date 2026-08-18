package com.plin.data

import androidx.room.TypeConverter
import com.plin.domain.enums.TaskStatus

/**
 * Teaches Room how to store types SQLite does not support natively.
 *
 * Here: converts [TaskStatus] enums to/from strings in the database.
 */
class Converters {
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String = status.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)
}
