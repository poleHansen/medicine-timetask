package com.medicinetimetask.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MedicationPlanEntity::class, ReminderRecordEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationPlanDao(): MedicationPlanDao
    abstract fun reminderRecordDao(): ReminderRecordDao
}
