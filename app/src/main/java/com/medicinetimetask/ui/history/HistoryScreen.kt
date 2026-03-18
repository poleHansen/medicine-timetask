package com.medicinetimetask.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medicinetimetask.data.repo.MedicineRepository
import com.medicinetimetask.ui.components.GradientHeader
import com.medicinetimetask.ui.components.SectionCard
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(repository: MedicineRepository) {
    val records by repository.observeHistory().collectAsState(initial = emptyList())
    val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            GradientHeader(
                title = "提醒记录",
                subtitle = "查看已服药、跳过、漏服等处理结果。"
            )
        }

        if (records.isEmpty()) {
            item {
                SectionCard(title = "还没有记录") {
                    Text("提醒触发后，历史会显示在这里。", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        items(records) { record ->
            SectionCard(title = record.status.name) {
                Text(record.scheduledAt.atZone(ZoneId.systemDefault()).format(formatter))
                Text("重复提醒次数：${record.repeatCount}")
            }
        }
    }
}
