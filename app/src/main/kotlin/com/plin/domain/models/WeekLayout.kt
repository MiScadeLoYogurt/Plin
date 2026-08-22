package com.plin.domain.models

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * The app's current calendar week, derived from today's date.
 */
data class WeekLayout(
    val key: String,
    val weekNumber: Int,
    val year: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    val displayLabel: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("MMM d")
            return "Week $weekNumber · ${startDate.format(formatter)} – ${endDate.format(formatter)}"
        }

    fun startMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long =
        startDate.atStartOfDay(zoneId).toInstant().toEpochMilli()

    fun endMillisExclusive(zoneId: ZoneId = ZoneId.systemDefault()): Long =
        endDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
}
