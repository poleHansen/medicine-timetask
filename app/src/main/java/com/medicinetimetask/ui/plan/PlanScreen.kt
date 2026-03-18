package com.medicinetimetask.ui.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medicinetimetask.data.model.MedicationPlan
import com.medicinetimetask.data.model.RepeatType
import com.medicinetimetask.data.repo.MedicineRepository
import com.medicinetimetask.reminder.ReminderScheduler
import com.medicinetimetask.ui.components.GradientHeader
import com.medicinetimetask.ui.components.SectionCard
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(repository: MedicineRepository) {
    val plans by repository.observePlans().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("1 粒") }
    var note by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("10:00") }
    var intervalText by remember { mutableStateOf("2") }
    var snoozeText by remember { mutableStateOf("15") }
    var maxRepeatText by remember { mutableStateOf("3") }
    var expanded by remember { mutableStateOf(false) }
    var repeatType by remember { mutableStateOf(RepeatType.EVERY_N_DAYS) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            GradientHeader(
                title = "新建提醒计划",
                subtitle = "先做好阶段 1：周期提醒、重复提醒、今日与历史记录。"
            )
        }

        item {
            SectionCard(title = "快速添加") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = medicineName,
                        onValueChange = { medicineName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("药名") },
                    )
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("剂量") },
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("备注") },
                    )
                    OutlinedTextField(
                        value = timeText,
                        onValueChange = { input ->
                            timeText = input.filter { char -> char.isDigit() || char == ':' }.take(5)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("提醒时间，例如 10:00") },
                    )

                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = if (repeatType == RepeatType.DAILY) "每天" else "每 N 天",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            label = { Text("周期") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text("每天") }, onClick = {
                                repeatType = RepeatType.DAILY
                                expanded = false
                            })
                            DropdownMenuItem(text = { Text("每 N 天") }, onClick = {
                                repeatType = RepeatType.EVERY_N_DAYS
                                expanded = false
                            })
                        }
                    }

                    if (repeatType == RepeatType.EVERY_N_DAYS) {
                        OutlinedTextField(
                            value = intervalText,
                            onValueChange = { intervalText = it.filter(Char::isDigit) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("间隔天数") },
                        )
                    }

                    OutlinedTextField(
                        value = snoozeText,
                        onValueChange = { snoozeText = it.filter(Char::isDigit) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("未处理时再次提醒间隔（分钟）") },
                    )
                    OutlinedTextField(
                        value = maxRepeatText,
                        onValueChange = { maxRepeatText = it.filter(Char::isDigit) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("最大重复次数") },
                    )
                    Button(
                        onClick = {
                            val parsedTime = runCatching { LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm")) }.getOrDefault(LocalTime.of(10, 0))
                            val plan = MedicationPlan(
                                medicineName = medicineName.ifBlank { "未命名药品" },
                                dosage = dosage.ifBlank { "1 次" },
                                note = note,
                                startDate = LocalDate.now(),
                                reminderTime = parsedTime,
                                repeatType = repeatType,
                                intervalDays = intervalText.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                                snoozeMinutes = snoozeText.toIntOrNull()?.coerceAtLeast(1) ?: 15,
                                maxRepeats = maxRepeatText.toIntOrNull()?.coerceAtLeast(1) ?: 3,
                            )
                            scope.launch {
                                val id = repository.addPlan(plan)
                                ReminderScheduler(context).schedulePlan(plan.copy(id = id))
                                medicineName = ""
                                dosage = "1 粒"
                                note = ""
                                timeText = "10:00"
                                intervalText = "2"
                                snoozeText = "15"
                                maxRepeatText = "3"
                                repeatType = RepeatType.EVERY_N_DAYS
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("保存并启用提醒")
                    }
                }
            }
        }

        item {
            SectionCard(title = "计划列表") {
                if (plans.isEmpty()) {
                    Text("还没有计划，先添加一条。", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        items(plans) { plan ->
            SectionCard(title = plan.medicineName) {
                Text("${plan.dosage} · ${plan.reminderTime}")
                Text(
                    if (plan.repeatType == RepeatType.DAILY) {
                        "每天提醒"
                    } else {
                        "每 ${plan.intervalDays} 天提醒一次"
                    }
                )
                Text("未处理后每 ${plan.snoozeMinutes} 分钟再次提醒，最多 ${plan.maxRepeats} 次")
            }
        }
    }
}
