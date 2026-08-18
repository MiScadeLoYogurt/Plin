package com.plin.data.routines

import com.plin.data.TaskService
import com.plin.domain.models.WeekLayout

/**
 * Archives incomplete weekly tasks from the previous week and creates the next instance.
 */
class GenerateWeeklyTasksRoutine(
    private val taskService: TaskService,
) : WeeklyRoutine {
    override suspend fun run(currentWeek: WeekLayout, previousWeek: WeekLayout) {
        taskService.rollWeeklyTasksIntoNewWeek(currentWeek, previousWeek)
    }
}
