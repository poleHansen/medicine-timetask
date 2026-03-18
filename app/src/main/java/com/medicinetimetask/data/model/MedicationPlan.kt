package com.medicinetimetask.data.model

import java.time.LocalDate
import java.time.LocalTime

data class MedicationPlan(
    val id: Long = 0,
    val medicineName: String,
    val dosage: String,
    val note: String,
    val startDate: LocalDate,
    val reminderTime: LocalTime,
    val repeatType: RepeatType,
    val intervalDays: Int,
    val snoozeMinutes: Int,
    val maxRepeats: Int,
    val enabled: Boolean = true,
)
