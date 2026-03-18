package com.medicinetimetask.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medicinetimetask.data.model.ReminderStatus
import com.medicinetimetask.data.repo.MedicineRepository
import com.medicinetimetask.ui.components.GradientHeader
import com.medicinetimetask.ui.components.SectionCard
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(repository: MedicineRepository) {
    val plans by repository.observePlans().collectAsState(initial = emptyList())
    val records by repository.observeTodayRecords().collectAsState(initial = emptyList())
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            GradientHeader(
                title = "今天的用药安排",
                subtitle = "${plans.count { it.enabled }} 个计划正在运行，${records.count { it.status == ReminderStatus.TAKEN }} 次已完成"
            )
        }

        item {
            SectionCard(title = "最近计划") {
                if (plans.isEmpty()) {
                    Text("先去计划页添加第一条用药计划。", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    plans.take(3).forEach { plan ->
                        Text(
                            text = "${plan.medicineName} · ${plan.dosage} · ${plan.reminderTime.format(formatter)}",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        item {
            SectionCard(title = "今日记录") {
                if (records.isEmpty()) {
                    Text("今天还没有提醒记录。", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Text(
                        text = "待处理 ${records.count { it.status == ReminderStatus.PENDING }} 次，已完成 ${records.count { it.status == ReminderStatus.TAKEN }} 次",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        items(records) { record ->
            val time = record.scheduledAt.atZone(ZoneId.systemDefault()).toLocalTime().format(formatter)
            SectionCard(title = time) {
                Text("状态：${record.status.name}")
                Text("重复提醒次数：${record.repeatCount}")
            }
        }
    }
}
