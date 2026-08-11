package com.plin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.plin.domain.models.Task
import kotlinx.coroutines.flow.Flow

/**
 * Low-level database operations for tasks (insert, query).
 *
 * Used by [TaskService]; the UI should not call this directly.
 */
@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task): Long

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Task>>
}
