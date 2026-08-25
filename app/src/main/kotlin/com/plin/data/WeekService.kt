package com.plin.data

import com.plin.data.routines.WeeklyRoutine
import com.plin.domain.WeekCalendar
import com.plin.domain.models.WeekLayout

/**
 * Resolves the current week and runs routines when the week changes.
 */
class WeekService(
    private val weekStateStore: WeekStateStore,
    private val routines: List<WeeklyRoutine>,
    private val taskService: TaskService,
) {

    fun currentWeek(): WeekLayout = WeekCalendar.forDate()

    suspend fun onAppOpen(): WeekLayout {
        taskService.backfillAssignedWeeksIfNeeded()

        val week = currentWeek()
        val lastWeekKey = weekStateStore.getLastWeekKey()

        if (lastWeekKey != null && lastWeekKey != week.key) {
            routines.forEach { routine -> routine.run(week) }
        }

        if (lastWeekKey != week.key) {
            weekStateStore.setLastWeekKey(week.key)
        }

        return week
    }
}
