package com.plin.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plin.data.PlinDatabase
import com.plin.data.TaskService
import com.plin.domain.models.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Holds UI state for the task list and talks to [TaskService].
 */
class TaskListViewModel(application: Application) : AndroidViewModel(application) {
    private val taskService = TaskService(
        PlinDatabase.getInstance(application).taskDao(),
    )

    val tasks: StateFlow<List<Task>> = taskService
        .observeTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun addTask(title: String) {
        viewModelScope.launch {
            taskService.addTask(title)
        }
    }
}
