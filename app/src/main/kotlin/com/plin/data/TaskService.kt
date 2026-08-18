package com.plin.data

import com.plin.domain.enums.TaskStatus
import com.plin.domain.models.Task

/**
 * Main entry point the UI uses for tasks.
 *
 * Sits between the front end and the database: screens call this
 * instead of talking to Room / SQLite directly.
 */
class TaskService(private val taskDao: TaskDao) {

    fun observeTasks() = taskDao.observeAll()

    suspend fun addTask(title: String): Long {
        val trimmed = title.trim()
        require(trimmed.isNotEmpty()) { "title is required" }
        return taskDao.insert(Task(title = trimmed))
    }

    suspend fun setStatus(task: Task, status: TaskStatus) {
        taskDao.update(task.copy(status = status))
    }

    suspend fun completeTask(task: Task) {
        setStatus(task, TaskStatus.COMPLETED)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }
}
