package com.medicinetimetask.data.model

import java.time.Instant

data class ReminderRecord(
    val id: Long = 0,
    val planId: Long,
    val scheduledAt: Instant,
    val status: ReminderStatus,
    val actionAt: Instant? = null,
    val repeatCount: Int = 0,
)
