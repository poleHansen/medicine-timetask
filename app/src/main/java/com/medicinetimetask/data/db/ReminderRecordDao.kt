package com.medicinetimetask.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderRecordDao {
    @Query("SELECT * FROM reminder_records ORDER BY scheduledAtEpochMillis DESC")
    fun observeRecords(): Flow<List<ReminderRecordEntity>>

    @Query("SELECT * FROM reminder_records WHERE scheduledAtEpochMillis BETWEEN :fromMillis AND :toMillis ORDER BY scheduledAtEpochMillis ASC")
    fun observeRecordsInRange(fromMillis: Long, toMillis: Long): Flow<List<ReminderRecordEntity>>

    @Insert
    suspend fun insertRecord(entity: ReminderRecordEntity): Long

    @Query("UPDATE reminder_records SET status = :status, actionAtEpochMillis = :actionAtMillis, repeatCount = :repeatCount WHERE id = :recordId")
    suspend fun updateStatus(recordId: Long, status: String, actionAtMillis: Long?, repeatCount: Int)

    @Query("SELECT * FROM reminder_records WHERE id = :recordId LIMIT 1")
    suspend fun getById(recordId: Long): ReminderRecordEntity?
}
