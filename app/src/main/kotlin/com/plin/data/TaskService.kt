package com.plin.data

import com.plin.domain.WeekCalendar
import com.plin.domain.enums.TaskCategory
import com.plin.domain.models.Task
import com.plin.domain.models.WeekLayout
import java.time.Instant
import java.time.ZoneId

/**
 * Main entry point the UI uses for tasks.
 *
 * Sits between the front end and the database: screens call this
 * instead of talking to Room / SQLite directly.
 */
class TaskService(private val taskDao: TaskDao) {

    companion object {
        const val FRIEND_OF_THE_WEEK_DEFAULT_TITLE = "Friend of the Week"
    }

    /**
     * Visible list: tasks whose [Task.assignedWeek] is the current week.
     */
    fun observeTasksForWeek(week: WeekLayout) =
        taskDao.observeForAssignedWeek(week.key)

    /** Tasks assigned to weeks before [week] — for the archive tab. */
    fun observeArchiveTasks(week: WeekLayout) =
        taskDao.observeBeforeWeek(week.key)

    suspend fun addTask(
        title: String,
        category: TaskCategory,
        isWeekly: Boolean = false,
        assignedWeek: String,
    ): Long {
        val trimmed = title.trim()
        require(trimmed.isNotEmpty()) { "title is required" }
        require(assignedWeek.isNotEmpty()) { "assignedWeek is required" }

        val taskId = taskDao.getNextTaskId()
        taskDao.insert(
            Task(
                id = taskId,
                instanceNumber = 0,
                title = trimmed,
                category = category,
                isWeekly = isWeekly,
                assignedWeek = assignedWeek,
            ),
        )
        return taskId
    }

    /**
     * Creates the weekly Friend of the Week task when enabled and none exists for [week].
     */
    suspend fun ensureFriendOfTheWeekTask(week: WeekLayout) {
        val existing = taskDao.getFriendOfTheWeekForWeek(week.key)
        if (existing != null) {
            return
        }
        addTask(
            title = FRIEND_OF_THE_WEEK_DEFAULT_TITLE,
            category = TaskCategory.FRIEND_OF_THE_WEEK,
            isWeekly = true,
            assignedWeek = week.key,
        )
    }

    /**
     * Fills [Task.assignedWeek] for rows migrated without it (from [Task.createdAt]).
     */
    suspend fun backfillAssignedWeeksIfNeeded() {
        val missing = taskDao.getTasksMissingAssignedWeek()
        val zone = ZoneId.systemDefault()
        for (task in missing) {
            val date = Instant.ofEpochMilli(task.createdAt).atZone(zone).toLocalDate()
            val weekKey = WeekCalendar.forDate(date).key
            taskDao.update(task.copy(assignedWeek = weekKey))
        }
    }

    /**
     * On a new calendar week:
     * - Weekly: create a new incomplete instance for [currentWeek]
     * - Non-weekly incomplete: bump [Task.assignedWeek] to [currentWeek] so they keep showing
     */
    suspend fun applyNewWeek(currentWeek: WeekLayout) {
        rollWeeklyTasksIntoNewWeek(currentWeek)
        taskDao.carryIncompleteNonWeeklyToWeek(currentWeek.key)
    }

    private suspend fun rollWeeklyTasksIntoNewWeek(currentWeek: WeekLayout) {
        val taskIds = taskDao.getWeeklyTaskIds()
        for (taskId in taskIds) {
            val alreadyInCurrentWeek = taskDao.getInstanceForWeek(taskId, currentWeek.key)
            if (alreadyInCurrentWeek != null) {
                continue
            }

            val latest = taskDao.getLatestForTask(taskId) ?: continue
            taskDao.insert(
                Task(
                    id = taskId,
                    instanceNumber = latest.instanceNumber + 1,
                    title = latest.title,
                    category = latest.category,
                    points = latest.points,
                    isWeekly = true,
                    completed = false,
                    assignedWeek = currentWeek.key,
                ),
            )
        }
    }

    suspend fun setCompleted(task: Task, completed: Boolean) {
        val completedAt = if (completed) System.currentTimeMillis() else null
        taskDao.update(task.copy(completed = completed, completedAt = completedAt))
    }

    suspend fun toggleCompletion(task: Task) {
        setCompleted(task, !task.completed)
    }

    suspend fun updateTask(task: Task) {
        val trimmed = task.title.trim()
        require(trimmed.isNotEmpty()) { "title is required" }
        taskDao.update(task.copy(title = trimmed))
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }
}
