package com.plin.domain.models

import androidx.room.Entity
import com.plin.domain.enums.TaskCategory
import com.plin.domain.enums.TaskStatus

/**
 * A single to-do item in the Plin task list.
 *
 * [id] identifies the task across weekly versions; [instanceNumber] starts at 0
 * and increments when a new weekly copy is generated. The calendar week comes from [createdAt].
 */
@Entity(tableName = "tasks", primaryKeys = ["id", "instanceNumber"])
data class Task(
    val id: Long,
    val instanceNumber: Int = 0,
    val title: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val category: TaskCategory = TaskCategory.GENERIC,
    val points: Int = 1,
    val isWeekly: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
