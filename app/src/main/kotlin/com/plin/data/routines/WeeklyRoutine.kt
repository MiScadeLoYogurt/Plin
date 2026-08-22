package com.plin.data.routines

import com.plin.domain.models.WeekLayout

/**
 * Runs when the app enters a new calendar week.
 * Add implementations here to generate weekly tasks, reset lists, etc.
 */
interface WeeklyRoutine {
    suspend fun run(currentWeek: WeekLayout, previousWeek: WeekLayout)
}
