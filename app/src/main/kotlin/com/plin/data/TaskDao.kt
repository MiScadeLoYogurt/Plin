package com.plin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.plin.domain.models.Task
import kotlinx.coroutines.flow.Flow

/**
 * Low-level database operations for tasks (insert, update, query, delete).
 *
 * Used by [TaskService]; the UI should not call this directly.
 */
@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT COALESCE(MAX(id), 0) + 1 FROM tasks")
    suspend fun getNextTaskId(): Long

    @Query("SELECT DISTINCT id FROM tasks WHERE isWeekly = 1")
    suspend fun getWeeklyTaskIds(): List<Long>

    @Query(
        """
        SELECT * FROM tasks
        WHERE id = :taskId
        AND createdAt >= :startMillis AND createdAt < :endMillisExclusive
        ORDER BY instanceNumber DESC
        LIMIT 1
        """,
    )
    suspend fun getInstanceInWeekRange(
        taskId: Long,
        startMillis: Long,
        endMillisExclusive: Long,
    ): Task?

    @Query(
        """
        SELECT * FROM tasks
        WHERE id = :taskId
        ORDER BY instanceNumber DESC
        LIMIT 1
        """,
    )
    suspend fun getLatestForTask(taskId: Long): Task?

    @Query(
        """
        SELECT * FROM tasks
        WHERE createdAt >= :startMillis AND createdAt < :endMillisExclusive
        AND status != 'ARCHIVED'
        ORDER BY createdAt DESC
        """,
    )
    fun observeForWeekRange(
        startMillis: Long,
        endMillisExclusive: Long,
    ): Flow<List<Task>>
}
