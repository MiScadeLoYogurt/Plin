package com.plin.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plin.data.PlinDatabase
import com.plin.data.TaskService
import com.plin.data.WeekService
import com.plin.data.WeekStateStore
import com.plin.data.routines.GenerateWeeklyTasksRoutine
import com.plin.domain.enums.TaskCategory
import com.plin.domain.enums.TaskStatus
import com.plin.domain.models.Task
import com.plin.domain.models.WeekLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Holds UI state for the task list and talks to [TaskService].
 * On start, resolves the current week and runs weekly routines if needed.
 */
class TaskListViewModel(application: Application) : AndroidViewModel(application) {
    private val taskService = TaskService(
        PlinDatabase.getInstance(application).taskDao(),
    )
    private val weekService = WeekService(
        weekStateStore = WeekStateStore(application),
        routines = listOf(GenerateWeeklyTasksRoutine(taskService)),
    )

    private val _currentWeek = MutableStateFlow<WeekLayout?>(null)
    val currentWeek: StateFlow<WeekLayout?> = _currentWeek.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = _currentWeek
        .flatMapLatest { week ->
            if (week == null) {
                flowOf(emptyList())
            } else {
                taskService.observeTasksForWeek(week)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    init {
        viewModelScope.launch {
            _currentWeek.value = weekService.onAppOpen()
        }
    }

    fun addTask(title: String, category: TaskCategory, isWeekly: Boolean = false) {
        viewModelScope.launch {
            taskService.addTask(title, category, isWeekly)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskService.toggleCompletion(task)
        }
    }

    fun setStatus(task: Task, status: TaskStatus) {
        viewModelScope.launch {
            taskService.setStatus(task, status)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskService.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskService.deleteTask(task)
        }
    }
}
