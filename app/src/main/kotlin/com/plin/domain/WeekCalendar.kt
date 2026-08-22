package com.plin.domain

import com.plin.domain.models.WeekLayout
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * Resolves calendar weeks from dates (ISO week: Monday start).
 */
object WeekCalendar {
    private val weekFields = WeekFields.of(DayOfWeek.MONDAY, 4)

    fun forDate(date: LocalDate = LocalDate.now()): WeekLayout {
        val weekNumber = date.get(weekFields.weekOfWeekBasedYear())
        val year = date.get(weekFields.weekBasedYear())
        val startDate = date.with(weekFields.dayOfWeek(), 1)
        val endDate = startDate.plusDays(6)
        return WeekLayout(
            key = formatKey(year, weekNumber),
            weekNumber = weekNumber,
            year = year,
            startDate = startDate,
            endDate = endDate,
        )
    }

    fun formatKey(year: Int, weekNumber: Int): String =
        String.format(Locale.US, "%d-W%02d", year, weekNumber)
}
