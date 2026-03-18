package com.medicinetimetask.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationPlanDao {
    @Query("SELECT * FROM medication_plans ORDER BY reminderHour, reminderMinute, medicineName")
    fun observePlans(): Flow<List<MedicationPlanEntity>>

    @Query("SELECT * FROM medication_plans WHERE enabled = 1")
    suspend fun getEnabledPlans(): List<MedicationPlanEntity>

    @Query("SELECT * FROM medication_plans WHERE id = :id LIMIT 1")
    suspend fun getPlanById(id: Long): MedicationPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(entity: MedicationPlanEntity): Long

    @Update
    suspend fun updatePlan(entity: MedicationPlanEntity)
}
