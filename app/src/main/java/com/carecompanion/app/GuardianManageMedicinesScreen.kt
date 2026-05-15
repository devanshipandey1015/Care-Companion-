package com.carecompanion.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareElevation
import com.carecompanion.app.ui.theme.CareGradients
import com.carecompanion.app.ui.theme.CareGreen
import com.carecompanion.app.ui.theme.CarePalette
import com.carecompanion.app.ui.theme.CareShapes
import com.carecompanion.app.ui.theme.CareSpacing
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private enum class MedicineReminderUiStatus {
    Taken,
    Pending,
    Missed,
    NeedsSchedule,
}

@Composable
fun GuardianManageMedicinesScreen(
    profile: GuardianProfile,
    medicines: List<Medicine>,
    onBack: () -> Unit,
    onSaveMedicines: (List<Medicine>) -> Unit,
    onAddMedicine: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateSos: () -> Unit = {},
) {
    val localMeds = remember(medicines) { mutableStateListOf<Medicine>().apply { addAll(medicines) } }
    var confirmDeleteId by remember { mutableStateOf<String?>(null) }

    val nowMinutes =
        Calendar.getInstance().run {
            get(Calendar.HOUR_OF_DAY) * 60 + get(Calendar.MINUTE)
        }

    fun statusesFor(list: List<Medicine>): List<MedicineReminderUiStatus> =
        list.map { medicineReminderUiStatus(it, nowMinutes) }

    val reminderStatuses = statusesFor(localMeds)

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CareGradients.pageSoftWash()),
    ) {
        Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            GuardianBottomBar(
                activeTab = BottomTab.Home,
                onHome = onNavigateHome,
                onAlerts = onNavigateSos,
            )
        },
        floatingActionButton = {
            Box(
                modifier =
                    Modifier
                        .size(58.dp)
                        .shadow(
                            elevation = 14.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFF2E5C44).copy(alpha = 0.28f),
                            spotColor = Color(0xFF3F7E58).copy(alpha = 0.42f),
                        )
                        .clip(CircleShape)
                        .background(MedicinesGrad)
                        .clickable(onClick = onAddMedicine)
                        .semantics { contentDescription = "Add medicine" },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            item {
                MedicinesGradientHeader(
                    profileName = profile.name,
                    onBack = onBack,
                )
            }

            item {
                Spacer(modifier = Modifier.height(CareSpacing.md))
                TodayMedicineSummaryCard(
                    totalMedicines = localMeds.size,
                    takenCount = reminderStatuses.count { it == MedicineReminderUiStatus.Taken },
                    pendingCount =
                        reminderStatuses.count {
                            it == MedicineReminderUiStatus.Pending ||
                                it == MedicineReminderUiStatus.NeedsSchedule
                        },
                    modifier =
                        Modifier
                            .padding(horizontal = CareSpacing.gutterScreen),
                )
                Spacer(modifier = Modifier.height(CareSpacing.lg))
            }

            if (localMeds.isEmpty()) {
                item {
                    MedicinesEmptyState(
                        modifier =
                            Modifier.padding(horizontal = CareSpacing.gutterScreen),
                        onAddMedicine = onAddMedicine,
                    )
                }
            } else {
                itemsIndexed(localMeds, key = { _, m -> m.id }) { index, med ->
                    MedicineCard(
                        medicine = med,
                        nowMinutes = nowMinutes,
                        onToggleActive = {
                            localMeds[index] = med.copy(isActive = !med.isActive)
                            onSaveMedicines(localMeds.toList())
                        },
                        onDelete = { confirmDeleteId = med.id },
                        modifier =
                            Modifier.padding(
                                horizontal = CareSpacing.gutterScreen,
                                vertical = 6.dp,
                            ),
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
        }
    }

    confirmDeleteId?.let { targetId ->
        val med = localMeds.find { it.id == targetId }
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            title = { Text("Remove medicine?") },
            text = { Text("Remove ${med?.name} from the list?") },
            confirmButton = {
                Button(
                    onClick = {
                        localMeds.removeAll { it.id == targetId }
                        onSaveMedicines(localMeds.toList())
                        confirmDeleteId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteId = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun MedicinesGradientHeader(
    profileName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = CareShapes.headerBottom,
                )
                .background(MedicinesGrad)
                .statusBarsPadding()
                .padding(
                    horizontal = CareSpacing.gutterScreen,
                    vertical = CareSpacing.lg,
                ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(CareSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Medicines",
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White,
                            lineHeight = 28.sp,
                        ),
                )
                Text(
                    text = "Track daily medication",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                        ),
                )
                Text(
                    text = profileName,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.76f),
                            fontSize = 13.sp,
                        ),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun TodayMedicineSummaryCard(
    totalMedicines: Int,
    takenCount: Int,
    pendingCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = CareElevation.modal,
                    shape = CareShapes.card,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.06f),
                    spotColor = CarePalette.PrimaryBlue.copy(alpha = 0.12f),
                ),
        shape = CareShapes.card,
        color = CarePalette.CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CareSpacing.md, vertical = CareSpacing.lg),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SummaryMetric(
                value = totalMedicines.toString(),
                label = "Total",
                accent = CarePalette.PrimaryBlue,
                modifier = Modifier.weight(1f),
            )
            VerticalDividerThin()
            SummaryMetric(
                value = takenCount.toString(),
                label = "Taken",
                accent = CareGreen,
                modifier = Modifier.weight(1f),
            )
            VerticalDividerThin()
            SummaryMetric(
                value = pendingCount.toString(),
                label = "Pending",
                accent = Color(0xFFE65100),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun RowScope.VerticalDividerThin() {
    Box(
        modifier =
            Modifier
                .width(1.dp)
                .height(46.dp)
                .background(CarePalette.OutlineSoft.copy(alpha = 0.55f)),
    )
}

@Composable
private fun SummaryMetric(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = accent,
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = GuardianTextSub,
        )
    }
}

@Composable
private fun MedicinesEmptyState(
    onAddMedicine: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = CareElevation.modal,
                    shape = CareShapes.card,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.06f),
                    spotColor = CarePalette.Mint.copy(alpha = 0.14f),
                ),
        shape = CareShapes.card,
        color = CarePalette.CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(CareSpacing.xl + CareSpacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(CareSpacing.md),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(88.dp)
                        .shadow(12.dp, CircleShape, spotColor = CareGreen.copy(alpha = 0.18f))
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)),
                            ),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Medication,
                    contentDescription = null,
                    tint = CareGreen,
                    modifier = Modifier.size(44.dp),
                )
            }
            Text(
                text = "No medicines yet",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = GuardianTextPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text =
                    "Build a simple schedule so reminders stay on track. Tap the green + button to add your first medicine.",
                fontSize = 14.sp,
                color = GuardianTextSub,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = CareSpacing.sm),
            )
            TextButton(onClick = onAddMedicine, modifier = Modifier.padding(top = CareSpacing.sm)) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = CareGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add medicine", fontWeight = FontWeight.Bold, color = CareGreen, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun MedicineCard(
    medicine: Medicine,
    nowMinutes: Int,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiStatus = medicineReminderUiStatus(medicine, nowMinutes)

    Surface(
        modifier =
            modifier
                .fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CarePalette.CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = CarePalette.Navy.copy(alpha = 0.05f),
                        spotColor = CarePalette.PrimaryBlue.copy(alpha = 0.08f),
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(CarePalette.CardWhite)
                    .padding(CareSpacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(CareSpacing.md),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (medicine.pillImageUri == null) {
                                    Brush.linearGradient(listOf(Color(0xFFEFF8F1), Color(0xFFD9EDE3)))
                                } else {
                                    Brush.linearGradient(listOf(Color.LightGray, Color.Gray))
                                },
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (medicine.pillImageUri != null) {
                        UriBitmapImage(
                            uri = medicine.pillImageUri,
                            contentDescription = "Medicine photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            Icons.Outlined.Medication,
                            contentDescription = null,
                            tint = CareGreen,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicine.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = GuardianTextPrimary,
                        maxLines = 2,
                    )
                    val dose =
                        listOfNotNull(
                            medicine.dosage.takeIf { it.isNotBlank() },
                            medicine.form.takeIf { it.isNotBlank() },
                        ).joinToString(" · ")
                    if (dose.isNotBlank()) {
                        Text(
                            text = dose,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = GuardianTextSub,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 10.dp),
                    ) {
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = CarePalette.PrimaryBlue.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = primaryScheduleLabel(medicine),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GuardianTextPrimary,
                            maxLines = 2,
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                ) {
                    MedicineStatusChip(uiStatus)
                    Switch(
                        checked = medicine.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors =
                            SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CareGreen,
                            ),
                    )
                }
            }

            if (medicine.packetFrontUri != null || medicine.packetBackUri != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    medicine.packetFrontUri?.let { uri ->
                        Box(
                            modifier =
                                Modifier
                                    .size(56.dp, 40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F8F2)),
                        ) {
                            UriBitmapImage(uri = uri, contentDescription = "Front", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                    }
                    medicine.packetBackUri?.let { uri ->
                        Box(
                            modifier =
                                Modifier
                                    .size(56.dp, 40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F8F2)),
                        ) {
                            UriBitmapImage(uri = uri, contentDescription = "Back", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                    }
                    Text(
                        text = "Packet photos",
                        fontSize = 11.sp,
                        color = GuardianTextSub,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            }

            if (medicine.schedules.isNotEmpty()) {
                HorizontalDivider(color = Color(0xFFF1F5F9))
                FlowScheduleRow(medicine)
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626)),
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Remove", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun FlowScheduleRow(medicine: Medicine) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        medicine.schedules.filter { it.enabled }.take(4).forEach { sched ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEEF7F1),
            ) {
                Text(
                    text = "${sched.label} · ${sched.time}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = CareGreen,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun MedicineStatusChip(status: MedicineReminderUiStatus) {
    when (status) {
        MedicineReminderUiStatus.Taken ->
            StatusChip(text = "Taken", tone = StatusChipTone.Positive)
        MedicineReminderUiStatus.Pending ->
            StatusChip(text = "Pending", tone = StatusChipTone.Warning)
        MedicineReminderUiStatus.Missed ->
            StatusChip(text = "Inactive", tone = StatusChipTone.Danger)
        MedicineReminderUiStatus.NeedsSchedule ->
            StatusChip(text = "Set times", tone = StatusChipTone.Warning)
    }
}

private fun parseScheduleToMinutes(raw: String): Int? {
    val time = raw.trim()
    if (time.isEmpty()) return null
    val patterns =
        arrayOf(
            "h:mm a",
            "hh:mm a",
            "K:mm a",
            "H:mm",
            "HH:mm",
            "h:mm:ss a",
            "HH:mm:ss",
        )
    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            sdf.isLenient = false
            val date = sdf.parse(time) ?: continue
            val cal =
                Calendar.getInstance().apply {
                    this.time = date
                }
            return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        } catch (_: ParseException) {
            continue
        }
    }
    return null
}

private fun medicineReminderUiStatus(
    med: Medicine,
    nowMinutes: Int,
): MedicineReminderUiStatus {
    if (!med.isActive) return MedicineReminderUiStatus.Missed

    val enabled = med.schedules.filter { it.enabled }
    if (enabled.isEmpty()) return MedicineReminderUiStatus.NeedsSchedule

    val minuteMarks = enabled.mapNotNull { parseScheduleToMinutes(it.time) }
    if (minuteMarks.isEmpty()) return MedicineReminderUiStatus.Pending

    val hasFuture = minuteMarks.any { it > nowMinutes }
    return if (hasFuture) MedicineReminderUiStatus.Pending else MedicineReminderUiStatus.Taken
}

private fun primaryScheduleLabel(medicine: Medicine): String {
    val enabled = medicine.schedules.filter { it.enabled }
    if (enabled.isEmpty()) return "No reminders yet"
    val first = enabled.first()
    return "${first.label} · ${first.time}"
}
