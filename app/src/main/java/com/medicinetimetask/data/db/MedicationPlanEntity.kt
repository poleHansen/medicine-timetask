package com.medicinetimetask.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_plans")
data class MedicationPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineName: String,
    val dosage: String,
    val note: String,
    val startDateEpochDay: Long,
    val reminderHour: Int,
    val reminderMinute: Int,
    val repeatType: String,
    val intervalDays: Int,
    val snoozeMinutes: Int,
    val maxRepeats: Int,
    val enabled: Boolean,
)
