package com.plin.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.plin.domain.enums.TaskStatus

/**
 * A single to-do item in the Plin task list.
 *
 * This is both the app's task model and a Room table row (`tasks`).
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
)
