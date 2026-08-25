package com.plin.data

import androidx.room.TypeConverter
import com.plin.domain.enums.TaskCategory

/**
 * Teaches Room how to store types SQLite does not support natively.
 */
class Converters {
    @TypeConverter
    fun fromTaskCategory(category: TaskCategory): String = category.name

    @TypeConverter
    fun toTaskCategory(value: String): TaskCategory = TaskCategory.valueOf(value)
}
