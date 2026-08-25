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

    /**
     * Adds [Task.assignedWeek]. Empty values are backfilled from [Task.createdAt] on next app open.
     */
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE tasks ADD COLUMN assignedWeek TEXT NOT NULL DEFAULT ''",
            )
        }
    }

    /**
     * Replaces status enum with [Task.completed] boolean.
     * COMPLETED → true; PENDING and ARCHIVED → false.
     */
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE tasks_new (
                    id INTEGER NOT NULL,
                    instanceNumber INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    completed INTEGER NOT NULL,
                    category TEXT NOT NULL,
                    points INTEGER NOT NULL,
                    isWeekly INTEGER NOT NULL,
                    assignedWeek TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    PRIMARY KEY(id, instanceNumber)
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                INSERT INTO tasks_new (
                    id, instanceNumber, title, completed, category,
                    points, isWeekly, assignedWeek, createdAt
                )
                SELECT
                    id,
                    instanceNumber,
                    title,
                    CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END,
                    category,
                    points,
                    isWeekly,
                    assignedWeek,
                    createdAt
                FROM tasks
                """.trimIndent(),
            )
            db.execSQL("DROP TABLE tasks")
            db.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
        }
    }
}
