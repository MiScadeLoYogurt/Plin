package com.plin.data

import com.plin.domain.enums.TaskCategory
import com.plin.domain.enums.TaskStatus
import com.plin.domain.models.Task
import com.plin.domain.models.WeekLayout

/**
 * Main entry point the UI uses for tasks.
 *
 * Sits between the front end and the database: screens call this
 * instead of talking to Room / SQLite directly.
 */
class TaskService(private val taskDao: TaskDao) {

    fun observeTasksForWeek(week: WeekLayout) =
        taskDao.observeForWeekRange(week.startMillis(), week.endMillisExclusive())

    suspend fun addTask(title: String, category: TaskCategory, isWeekly: Boolean = false): Long {
        val trimmed = title.trim()
        require(trimmed.isNotEmpty()) { "title is required" }

        val taskId = taskDao.getNextTaskId()
        taskDao.insert(
            Task(
                id = taskId,
                instanceNumber = 0,
                title = trimmed,
                category = category,
                isWeekly = isWeekly,
            ),
        )
        return taskId
    }

    suspend fun rollWeeklyTasksIntoNewWeek(currentWeek: WeekLayout, previousWeek: WeekLayout) {
        val taskIds = taskDao.getWeeklyTaskIds()
        for (taskId in taskIds) {
            val previousInstance = taskDao.getInstanceInWeekRange(
                taskId = taskId,
                startMillis = previousWeek.startMillis(),
                endMillisExclusive = previousWeek.endMillisExclusive(),
            )

            if (previousInstance != null && previousInstance.status == TaskStatus.PENDING) {
                taskDao.update(previousInstance.copy(status = TaskStatus.ARCHIVED))
            }

            val alreadyInCurrentWeek = taskDao.getInstanceInWeekRange(
                taskId = taskId,
                startMillis = currentWeek.startMillis(),
                endMillisExclusive = currentWeek.endMillisExclusive(),
            )
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
                    status = TaskStatus.PENDING,
                ),
            )
        }
    }

    suspend fun setStatus(task: Task, status: TaskStatus) {
        taskDao.update(task.copy(status = status))
    }

    suspend fun toggleCompletion(task: Task) {
        val newStatus = if (task.status == TaskStatus.COMPLETED) {
            TaskStatus.PENDING
        } else {
            TaskStatus.COMPLETED
        }
        setStatus(task, newStatus)
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
