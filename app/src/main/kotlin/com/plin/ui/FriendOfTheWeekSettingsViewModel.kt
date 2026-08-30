package com.plin.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plin.data.FriendOfTheWeekSettingsStore
import com.plin.data.PlinDatabase
import com.plin.data.TaskService
import com.plin.domain.WeekCalendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendOfTheWeekSettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsStore = FriendOfTheWeekSettingsStore(application)
    private val taskService = TaskService(PlinDatabase.getInstance(application).taskDao())

    private val _enabled = MutableStateFlow(settingsStore.isEnabled())
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    fun setEnabled(enabled: Boolean) {
        settingsStore.setEnabled(enabled)
        _enabled.value = enabled
        if (enabled) {
            viewModelScope.launch {
                taskService.ensureFriendOfTheWeekTask(WeekCalendar.forDate())
            }
        }
    }
}
