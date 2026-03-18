package com.medicinetimetask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.medicinetimetask.data.repo.MedicineRepositoryProvider
import com.medicinetimetask.reminder.ReminderInitializer
import com.medicinetimetask.ui.MedicineReminderApp
import com.medicinetimetask.ui.theme.MedicineTimeTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ReminderInitializer.initialize(applicationContext)
        setContent {
            MedicineTimeTaskTheme {
                MedicineReminderApp(MedicineRepositoryProvider.provide(applicationContext))
            }
        }
    }
}
