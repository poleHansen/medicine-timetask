package com.medicinetimetask.data.repo

import com.medicinetimetask.data.model.MedicationPlan
import com.medicinetimetask.data.model.ReminderRecord
import kotlinx.coroutines.flow.Flow

interface MedicineRepository {
    fun observePlans(): Flow<List<MedicationPlan>>
    fun observeHistory(): Flow<List<ReminderRecord>>
    fun observeTodayRecords(): Flow<List<ReminderRecord>>
    suspend fun addPlan(plan: MedicationPlan): Long
    suspend fun getRecord(recordId: Long): ReminderRecord?
    suspend fun markRecordTaken(recordId: Long)
    suspend fun markRecordSkipped(recordId: Long)
    suspend fun markRecordMissed(recordId: Long, repeatCount: Int)
    suspend fun createRecordForPlan(planId: Long, scheduledAtMillis: Long, repeatCount: Int): Long
    suspend fun getPlan(planId: Long): MedicationPlan?
    suspend fun getEnabledPlans(): List<MedicationPlan>
}
