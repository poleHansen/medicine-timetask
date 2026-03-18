package com.medicinetimetask.data.repo

import android.content.Context
import androidx.room.Room
import com.medicinetimetask.data.db.AppDatabase

object MedicineRepositoryProvider {
    @Volatile
    private var repository: MedicineRepository? = null

    fun provide(context: Context): MedicineRepository {
        return repository ?: synchronized(this) {
            repository ?: buildRepository(context.applicationContext).also { repository = it }
        }
    }

    private fun buildRepository(context: Context): MedicineRepository {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "medicine-time-task.db"
        ).build()
        return MedicineRepositoryImpl(db.medicationPlanDao(), db.reminderRecordDao())
    }
}
