package com.plin.data.routines

import com.plin.data.TaskService
import com.plin.domain.models.WeekLayout

/**
 * Applies week rollover: new weekly instances + carry incomplete non-weekly tasks forward.
 */
class GenerateWeeklyTasksRoutine(
    private val taskService: TaskService,
) : WeeklyRoutine {
    override suspend fun run(currentWeek: WeekLayout) {
        taskService.applyNewWeek(currentWeek)
    }
}
