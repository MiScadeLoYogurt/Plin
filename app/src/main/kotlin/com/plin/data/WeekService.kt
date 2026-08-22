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
) {

    fun currentWeek(): WeekLayout = WeekCalendar.forDate()

    suspend fun onAppOpen(): WeekLayout {
        val week = currentWeek()
        val lastWeekKey = weekStateStore.getLastWeekKey()

        if (lastWeekKey != null && lastWeekKey != week.key) {
            val previousWeek = WeekCalendar.forDate(week.startDate.minusDays(1))
            routines.forEach { routine -> routine.run(week, previousWeek) }
        }

        if (lastWeekKey != week.key) {
            weekStateStore.setLastWeekKey(week.key)
        }

        return week
    }
}
