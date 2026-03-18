package com.medicinetimetask.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.medicinetimetask.data.repo.MedicineRepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = MedicineRepositoryProvider.provide(context)
                val planId = intent.getLongExtra(ReminderScheduler.EXTRA_PLAN_ID, -1)
                if (planId == -1L) return@launch
                val plan = repo.getPlan(planId) ?: return@launch
                val repeatCount = intent.getIntExtra(ReminderScheduler.EXTRA_REPEAT_COUNT, 0)
                val triggerAt = intent.getLongExtra(ReminderScheduler.EXTRA_TRIGGER_AT, System.currentTimeMillis())
                if (repeatCount > plan.maxRepeats) {
                    val expiredRecordId = intent.getLongExtra(ReminderScheduler.EXTRA_RECORD_ID, -1)
                    if (expiredRecordId != -1L) {
                        repo.markRecordMissed(expiredRecordId, plan.maxRepeats)
                    }
                    return@launch
                }
                val recordId = if (repeatCount == 0) {
                    repo.createRecordForPlan(planId, triggerAt, 0)
                } else {
                    intent.getLongExtra(ReminderScheduler.EXTRA_RECORD_ID, -1)
                }
                if (recordId == -1L) return@launch
                if (repeatCount > 0) {
                    val record = repo.getRecord(recordId) ?: return@launch
                    if (record.status != com.medicinetimetask.data.model.ReminderStatus.PENDING) {
                        return@launch
                    }
                }

                ReminderNotifier(context).showReminder(plan, recordId, repeatCount)

                if (repeatCount <= plan.maxRepeats) {
                    ReminderScheduler(context).scheduleRepeat(plan, recordId, repeatCount + 1)
                }

                if (repeatCount == 0) {
                    ReminderScheduler(context).schedulePlan(plan)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
