package com.medicinetimetask.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.medicinetimetask.data.model.MedicationPlan
import com.medicinetimetask.data.model.RepeatType
import java.time.ZoneId
import kotlin.math.absoluteValue

class ReminderScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedulePlan(plan: MedicationPlan) {
        if (!plan.enabled) return
        val triggerAt = ReminderTimeCalculator.nextTriggerMillis(plan)
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PLAN_ID, plan.id)
            putExtra(EXTRA_REPEAT_COUNT, 0)
            putExtra(EXTRA_TRIGGER_AT, triggerAt)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plan.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    fun scheduleRepeat(plan: MedicationPlan, recordId: Long, repeatCount: Int) {
        val triggerAt = System.currentTimeMillis() + plan.snoozeMinutes * 60_000L
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PLAN_ID, plan.id)
            putExtra(EXTRA_RECORD_ID, recordId)
            putExtra(EXTRA_REPEAT_COUNT, repeatCount)
            putExtra(EXTRA_TRIGGER_AT, triggerAt)
        }
        val requestCode = (plan.id.toInt() * 1000 + repeatCount).absoluteValue
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    fun rescheduleAll(plans: List<MedicationPlan>) {
        plans.forEach(::schedulePlan)
    }

    companion object {
        const val EXTRA_PLAN_ID = "extra_plan_id"
        const val EXTRA_RECORD_ID = "extra_record_id"
        const val EXTRA_REPEAT_COUNT = "extra_repeat_count"
        const val EXTRA_TRIGGER_AT = "extra_trigger_at"
    }
}

object ReminderTimeCalculator {
    fun nextTriggerMillis(plan: MedicationPlan): Long {
        val zone = ZoneId.systemDefault()
        val now = java.time.ZonedDateTime.now(zone)
        var candidateDate = maxOf(plan.startDate, now.toLocalDate())

        while (true) {
            val candidate = candidateDate.atTime(plan.reminderTime).atZone(zone)
            val isAfterNow = candidate.isAfter(now)
            val matches = when (plan.repeatType) {
                RepeatType.DAILY -> true
                RepeatType.EVERY_N_DAYS -> {
                    val days = java.time.temporal.ChronoUnit.DAYS.between(plan.startDate, candidateDate)
                    days >= 0 && days % plan.intervalDays == 0L
                }
            }
            if (matches && isAfterNow) return candidate.toInstant().toEpochMilli()
            candidateDate = candidateDate.plusDays(1)
        }
    }
}
