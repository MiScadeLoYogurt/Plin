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
        WHERE id = :taskId AND assignedWeek = :weekKey
        ORDER BY instanceNumber DESC
        LIMIT 1
        """,
    )
    suspend fun getInstanceForWeek(taskId: Long, weekKey: String): Task?

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
        WHERE assignedWeek = :weekKey
        ORDER BY createdAt DESC
        """,
    )
    fun observeForAssignedWeek(weekKey: String): Flow<List<Task>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE assignedWeek != '' AND assignedWeek < :weekKey
        ORDER BY createdAt DESC
        """,
    )
    fun observeBeforeWeek(weekKey: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE assignedWeek = '' OR assignedWeek IS NULL")
    suspend fun getTasksMissingAssignedWeek(): List<Task>

    @Query(
        """
        UPDATE tasks
        SET assignedWeek = :newWeekKey
        WHERE isWeekly = 0
        AND completed = 0
        AND assignedWeek != :newWeekKey
        """,
    )
    suspend fun carryIncompleteNonWeeklyToWeek(newWeekKey: String)
}
