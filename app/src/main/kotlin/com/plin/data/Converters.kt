package com.plin.data

import androidx.room.TypeConverter
import com.plin.domain.enums.TaskCategory
import com.plin.domain.enums.TaskStatus

/**
 * Teaches Room how to store types SQLite does not support natively.
 */
class Converters {
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String = status.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    @TypeConverter
    fun fromTaskCategory(category: TaskCategory): String = category.name

    @TypeConverter
    fun toTaskCategory(value: String): TaskCategory = TaskCategory.valueOf(value)
}
