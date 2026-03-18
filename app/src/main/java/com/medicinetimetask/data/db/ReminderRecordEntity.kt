package com.medicinetimetask.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminder_records",
    indices = [Index("planId"), Index("scheduledAtEpochMillis")]
)
data class ReminderRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: Long,
    val scheduledAtEpochMillis: Long,
    val status: String,
    val actionAtEpochMillis: Long?,
    val repeatCount: Int,
)
