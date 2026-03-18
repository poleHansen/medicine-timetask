package com.medicinetimetask.data.repo

import com.medicinetimetask.data.db.MedicationPlanDao
import com.medicinetimetask.data.db.MedicationPlanEntity
import com.medicinetimetask.data.db.ReminderRecordDao
import com.medicinetimetask.data.db.ReminderRecordEntity
import com.medicinetimetask.data.model.MedicationPlan
import com.medicinetimetask.data.model.ReminderRecord
import com.medicinetimetask.data.model.ReminderStatus
import com.medicinetimetask.data.model.RepeatType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicineRepositoryImpl(
    private val planDao: MedicationPlanDao,
    private val recordDao: ReminderRecordDao,
) : MedicineRepository {

    override fun observePlans(): Flow<List<MedicationPlan>> =
        planDao.observePlans().map { it.map(::planFromEntity) }

    override fun observeHistory(): Flow<List<ReminderRecord>> =
        recordDao.observeRecords().map { it.map(::recordFromEntity) }

    override fun observeTodayRecords(): Flow<List<ReminderRecord>> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val from = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val to = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
        return recordDao.observeRecordsInRange(from, to).map { it.map(::recordFromEntity) }
    }

    override suspend fun addPlan(plan: MedicationPlan): Long = planDao.insertPlan(plan.toEntity())

    override suspend fun getRecord(recordId: Long): ReminderRecord? = recordDao.getById(recordId)?.let(::recordFromEntity)

    override suspend fun markRecordTaken(recordId: Long) {
        recordDao.updateStatus(recordId, ReminderStatus.TAKEN.name, Instant.now().toEpochMilli(), currentRepeat(recordId))
    }

    override suspend fun markRecordSkipped(recordId: Long) {
        recordDao.updateStatus(recordId, ReminderStatus.SKIPPED.name, Instant.now().toEpochMilli(), currentRepeat(recordId))
    }

    override suspend fun markRecordMissed(recordId: Long, repeatCount: Int) {
        recordDao.updateStatus(recordId, ReminderStatus.MISSED.name, Instant.now().toEpochMilli(), repeatCount)
    }

    override suspend fun createRecordForPlan(planId: Long, scheduledAtMillis: Long, repeatCount: Int): Long {
        return recordDao.insertRecord(
            ReminderRecordEntity(
                planId = planId,
                scheduledAtEpochMillis = scheduledAtMillis,
                status = ReminderStatus.PENDING.name,
                actionAtEpochMillis = null,
                repeatCount = repeatCount,
            )
        )
    }

    override suspend fun getPlan(planId: Long): MedicationPlan? = planDao.getPlanById(planId)?.let(::planFromEntity)

    override suspend fun getEnabledPlans(): List<MedicationPlan> = planDao.getEnabledPlans().map(::planFromEntity)

    private suspend fun currentRepeat(recordId: Long): Int = recordDao.getById(recordId)?.repeatCount ?: 0

    private fun MedicationPlan.toEntity() = MedicationPlanEntity(
        id = id,
        medicineName = medicineName,
        dosage = dosage,
        note = note,
        startDateEpochDay = startDate.toEpochDay(),
        reminderHour = reminderTime.hour,
        reminderMinute = reminderTime.minute,
        repeatType = repeatType.name,
        intervalDays = intervalDays,
        snoozeMinutes = snoozeMinutes,
        maxRepeats = maxRepeats,
        enabled = enabled,
    )

    private fun planFromEntity(entity: MedicationPlanEntity) = MedicationPlan(
        id = entity.id,
        medicineName = entity.medicineName,
        dosage = entity.dosage,
        note = entity.note,
        startDate = LocalDate.ofEpochDay(entity.startDateEpochDay),
        reminderTime = LocalTime.of(entity.reminderHour, entity.reminderMinute),
        repeatType = RepeatType.valueOf(entity.repeatType),
        intervalDays = entity.intervalDays,
        snoozeMinutes = entity.snoozeMinutes,
        maxRepeats = entity.maxRepeats,
        enabled = entity.enabled,
    )

    private fun recordFromEntity(entity: ReminderRecordEntity) = ReminderRecord(
        id = entity.id,
        planId = entity.planId,
        scheduledAt = Instant.ofEpochMilli(entity.scheduledAtEpochMillis),
        status = ReminderStatus.valueOf(entity.status),
        actionAt = entity.actionAtEpochMillis?.let(Instant::ofEpochMilli),
        repeatCount = entity.repeatCount,
    )
}
