package com.plin.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room schema migrations. Prefer additive changes (ADD COLUMN with DEFAULT)
 * so existing tasks on device are kept.
 */
object PlinMigrations {
    /**
     * Adds [Task.points] with default 1 for existing rows.
     */
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE tasks ADD COLUMN points INTEGER NOT NULL DEFAULT 1",
            )
        }
    }
}
