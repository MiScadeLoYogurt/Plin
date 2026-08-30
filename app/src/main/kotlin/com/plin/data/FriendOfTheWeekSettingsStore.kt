package com.plin.data

import android.content.Context

/**
 * Persists Friend of the Week feature settings.
 */
class FriendOfTheWeekSettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, DEFAULT_ENABLED)

    fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    companion object {
        private const val PREFS_NAME = "plin_friend_of_the_week"
        private const val KEY_ENABLED = "enabled"
        private const val DEFAULT_ENABLED = true
    }
}
