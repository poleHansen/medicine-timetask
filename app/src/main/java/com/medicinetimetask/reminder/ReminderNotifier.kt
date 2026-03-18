package com.medicinetimetask.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.medicinetimetask.MainActivity
import com.medicinetimetask.data.model.MedicationPlan

class ReminderNotifier(private val context: Context) {
    fun showReminder(plan: MedicationPlan, recordId: Long, repeatCount: Int) {
        ensureChannel()
        val contentIntent = PendingIntent.getActivity(
            context,
            recordId.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val takenIntent = actionPendingIntent(NotificationActionReceiver.ACTION_TAKEN, recordId)
        val skipIntent = actionPendingIntent(NotificationActionReceiver.ACTION_SKIPPED, recordId)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(if (repeatCount == 0) "该吃药了" else "你还没有确认服药")
            .setContentText("${plan.medicineName} ${plan.dosage} · ${plan.reminderTime}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .addAction(0, "已服药", takenIntent)
            .addAction(0, "跳过", skipIntent)
            .build()

        NotificationManagerCompat.from(context).notify(recordId.toInt(), notification)
    }

    private fun actionPendingIntent(action: String, recordId: Long): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            this.action = action
            putExtra(NotificationActionReceiver.EXTRA_RECORD_ID, recordId)
        }
        return PendingIntent.getBroadcast(
            context,
            (recordId.toInt() + action.hashCode()),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "用药提醒",
                NotificationManager.IMPORTANCE_HIGH,
            )
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "medicine_reminders"
    }
}
