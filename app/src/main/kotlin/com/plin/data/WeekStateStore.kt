package com.plin.data

import android.content.Context

/**
 * Persists which week the app last processed on open.
 */
class WeekStateStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getLastWeekKey(): String? = prefs.getString(KEY_LAST_WEEK, null)

    fun setLastWeekKey(weekKey: String) {
        prefs.edit().putString(KEY_LAST_WEEK, weekKey).apply()
    }

    companion object {
        private const val PREFS_NAME = "plin_week_state"
        private const val KEY_LAST_WEEK = "last_week_key"
    }
}
