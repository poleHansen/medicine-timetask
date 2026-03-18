package com.medicinetimetask.reminder

import android.content.Context
import com.medicinetimetask.data.repo.MedicineRepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ReminderInitializer {
    fun initialize(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val repo = MedicineRepositoryProvider.provide(context)
            ReminderScheduler(context).rescheduleAll(repo.getEnabledPlans())
        }
    }
}
