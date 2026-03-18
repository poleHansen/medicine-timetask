package com.medicinetimetask.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.medicinetimetask.data.repo.MedicineRepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val recordId = intent.getLongExtra(EXTRA_RECORD_ID, -1)
        if (recordId == -1L) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = MedicineRepositoryProvider.provide(context)
                when (intent.action) {
                    ACTION_TAKEN -> repo.markRecordTaken(recordId)
                    ACTION_SKIPPED -> repo.markRecordSkipped(recordId)
                }
                NotificationManagerCompat.from(context).cancel(recordId.toInt())
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_TAKEN = "com.medicinetimetask.action.TAKEN"
        const val ACTION_SKIPPED = "com.medicinetimetask.action.SKIPPED"
        const val EXTRA_RECORD_ID = "extra_record_id"
    }
}
