package com.carecompanion.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareElevation
import com.carecompanion.app.ui.theme.CareGreen
import com.carecompanion.app.ui.theme.CarePalette
import com.carecompanion.app.ui.theme.CareShapes
import com.carecompanion.app.ui.theme.CareSpacing
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class GenericReminder(
    val label: String,
    val subtitle: String,
    val time: String,
    val icon: ImageVector,
    val enabled: Boolean = true,
)

private data class ActivityReminder(
    val label: String,
    val time: String,
    val icon: ImageVector,
    val enabled: Boolean = true,
)

private enum class PlannerDayPart {
    Morning,
    Afternoon,
    Evening,
}

private data class TimelineEntry(
    val sortMinutes: Int,
    val dayPart: PlannerDayPart,
    val timeLabel: String,
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val chipLabel: String,
    val chipTone: StatusChipTone,
    val switchChecked: Boolean,
    val onToggle: () -> Unit,
)

@Composable
fun GuardianDailyScheduleScreen(
    profile: GuardianProfile,
    medicines: List<Medicine>,
    onBack: () -> Unit,
    onSaveMedicines: (List<Medicine>) -> Unit,
    onAddSchedule: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateSos: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(0) }
    val localMeds = remember(medicines) { mutableStateListOf<Medicine>().apply { addAll(medicines) } }

    val genericReminders =
        remember {
            mutableStateListOf(
                GenericReminder("Vitals Check", "Blood pressure · Pulse", "9:00 AM", Icons.Outlined.MonitorHeart, true),
                GenericReminder("Blood Sugar", "Fasting check", "7:00 AM", Icons.Outlined.Favorite, false),
            )
        }
    val activityReminders =
        remember {
            mutableStateListOf(
                ActivityReminder("Morning Walk", "6:30 AM", Icons.Outlined.DirectionsWalk, true),
                ActivityReminder("Hydration Reminder", "Every 2 hrs", Icons.Outlined.LocalDrink, true),
                ActivityReminder("Evening Exercise", "5:00 PM", Icons.Outlined.FitnessCenter, false),
                ActivityReminder("Meditation", "7:00 PM", Icons.Outlined.Spa, false),
            )
        }

    val medicineTimelineEntries =
        buildMedicineTimelineEntries(
            generics = genericReminders.toList(),
            meds = localMeds.toList(),
            onGenericToggle = { idx ->
                val gr = genericReminders[idx]
                genericReminders[idx] = gr.copy(enabled = !gr.enabled)
            },
            onMedScheduleToggle = { medIdx, schedIdx ->
                val med = localMeds[medIdx]
                val updated = med.schedules.toMutableList()
                updated[schedIdx] = updated[schedIdx].copy(enabled = !updated[schedIdx].enabled)
                localMeds[medIdx] = med.copy(schedules = updated)
                onSaveMedicines(localMeds.toList())
            },
        )

    val activityTimelineEntries =
        buildActivityTimelineEntries(
            activities = activityReminders.toList(),
            onToggle = { idx ->
                val act = activityReminders[idx]
                activityReminders[idx] = act.copy(enabled = !act.enabled)
            },
        )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GuardianBg,
        bottomBar = {
            GuardianBottomBar(
                activeTab = BottomTab.Home,
                onHome = onNavigateHome,
                onAlerts = onNavigateSos,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSchedule,
                containerColor = CareGreen,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 10.dp),
                modifier = Modifier.shadow(12.dp, CircleShape, spotColor = CareGreen.copy(alpha = 0.35f)),
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add schedule")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            item {
                DailyScheduleHeader(profileName = profile.name, onBack = onBack)
            }

            item {
                Spacer(modifier = Modifier.height(CareSpacing.md))
                TodayDateCard(
                    modifier =
                        Modifier.padding(horizontal = CareSpacing.gutterScreen),
                )
            }

            item {
                Spacer(modifier = Modifier.height(CareSpacing.md))
                GradientButton(
                    text = "+ Add to schedule",
                    onClick = onAddSchedule,
                    gradient = ScheduleGrad,
                    modifier =
                        Modifier
                            .padding(horizontal = CareSpacing.gutterScreen)
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                )
            }

            item {
                Spacer(modifier = Modifier.height(CareSpacing.lg))
                TabToggleRow(
                    selectedTab = selectedTab,
                    onSelect = { selectedTab = it },
                    modifier =
                        Modifier.padding(horizontal = CareSpacing.gutterScreen),
                )
                Spacer(modifier = Modifier.height(CareSpacing.md))
            }

            if (selectedTab == 0) {
                if (medicineTimelineEntries.isEmpty()) {
                    item {
                        ScheduleEmptyState(
                            title = "No entries yet",
                            message = "Add vitals checks or schedule medicines to populate today’s planner.",
                            modifier =
                                Modifier.padding(horizontal = CareSpacing.gutterScreen),
                            actionLabel = "Add schedule",
                            onAction = onAddSchedule,
                        )
                    }
                } else {
                    daySectionItems(entries = medicineTimelineEntries)
                }
            } else {
                if (activityTimelineEntries.isEmpty()) {
                    item {
                        ScheduleEmptyState(
                            title = "No activities",
                            message = "Route walks, hydration nudges, and wellness moments here.",
                            modifier =
                                Modifier.padding(horizontal = CareSpacing.gutterScreen),
                            actionLabel = "Add schedule",
                            onAction = onAddSchedule,
                        )
                    }
                } else {
                    daySectionItems(entries = activityTimelineEntries)
                }
            }
        }
    }
}

private fun LazyListScope.daySectionItems(entries: List<TimelineEntry>) {
    val sorted = entries.sortedBy { it.sortMinutes }
    PlannerDayPart.entries.forEach { part ->
        val slice = sorted.filter { it.dayPart == part }
        if (slice.isEmpty()) return@forEach
        item(key = "hdr_$part") {
            DayPartSectionTitle(
                part = part,
                modifier =
                    Modifier.padding(
                        horizontal = CareSpacing.gutterScreen,
                        vertical = CareSpacing.sm,
                    ),
            )
        }
        items(
            slice,
            key = { "${it.title}_${it.timeLabel}_${it.sortMinutes}_${it.subtitle.hashCode()}" },
        ) { entry ->
            TimelineCard(
                entry = entry,
                modifier =
                    Modifier.padding(
                        horizontal = CareSpacing.gutterScreen,
                        vertical = 6.dp,
                    ),
            )
        }
    }
}

@Composable
private fun DailyScheduleHeader(
    profileName: String,
    onBack: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(10.dp, CareShapes.headerBottom)
                .background(ScheduleGrad)
                .statusBarsPadding()
                .padding(horizontal = CareSpacing.gutterScreen, vertical = CareSpacing.lg),
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
                    text = "Daily Schedule",
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 24.sp,
                        ),
                )
                Text(
                    text = "Today’s care plan",
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
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 13.sp,
                        ),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun TodayDateCard(modifier: Modifier = Modifier) {
    val fmtWeekday = remember { SimpleDateFormat("EEEE", Locale.getDefault()) }
    val fmtMain = remember { SimpleDateFormat("MMMM d · yyyy", Locale.getDefault()) }
    val now = remember { Date() }
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = CareElevation.card,
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
                    .padding(CareSpacing.cardPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = fmtWeekday.format(now),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GuardianTextPrimary,
                )
                Text(
                    text = fmtMain.format(now),
                    fontSize = 14.sp,
                    color = GuardianTextSub,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Surface(
                shape = RoundedCornerShape(CareSpacing.md),
                color = CareGreen.copy(alpha = 0.12f),
            ) {
                Text(
                    text = "Today",
                    modifier =
                        Modifier.padding(horizontal = CareSpacing.md + 4.dp, vertical = CareSpacing.sm),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CareGreen,
                )
            }
        }
    }
}

@Composable
private fun TabToggleRow(
    selectedTab: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CarePalette.CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf("Medicines", "Activities").forEachIndexed { index, label ->
                val selected = selectedTab == index
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .then(
                                if (selected) {
                                    Modifier.background(ScheduleGrad)
                                } else {
                                    Modifier.background(CarePalette.PageBgLight)
                                },
                            )
                            .clickable { onSelect(index) }
                            .padding(vertical = CareSpacing.sm + 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (selected) Color.White else GuardianTextSub,
                    )
                }
            }
        }
    }
}

@Composable
private fun DayPartSectionTitle(
    part: PlannerDayPart,
    modifier: Modifier = Modifier,
) {
    val label =
        when (part) {
            PlannerDayPart.Morning -> "Morning"
            PlannerDayPart.Afternoon -> "Afternoon"
            PlannerDayPart.Evening -> "Evening"
        }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm),
    ) {
        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(CareGreen.copy(alpha = 0.85f)),
        )
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = GuardianTextPrimary,
            letterSpacing = 0.3.sp,
        )
        HorizontalDivider(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = CareSpacing.sm),
            color = CarePalette.OutlineSoft.copy(alpha = 0.45f),
        )
    }
}

@Composable
private fun TimelineCard(
    entry: TimelineEntry,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = CarePalette.CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
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
                    .padding(CareSpacing.md + 2.dp),
            horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.width(54.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = entry.timeLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GuardianTextPrimary,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                )
                Box(
                    modifier =
                        Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.End)
                            .size(10.dp)
                            .background(if (entry.switchChecked) CareGreen else CarePalette.OutlineSoft, CircleShape),
                )
            }

            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (entry.switchChecked) CareGreen.copy(alpha = 0.12f) else CarePalette.PageBgLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    entry.icon,
                    contentDescription = null,
                    tint = if (entry.switchChecked) CareGreen else GuardianTextSub,
                    modifier = Modifier.size(26.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GuardianTextPrimary,
                    maxLines = 2,
                )
                Text(
                    text = entry.subtitle,
                    fontSize = 13.sp,
                    color = GuardianTextSub,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 3,
                )
                Spacer(modifier = Modifier.height(10.dp))
                StatusChip(text = entry.chipLabel, tone = entry.chipTone)
            }

            Switch(
                checked = entry.switchChecked,
                onCheckedChange = { entry.onToggle() },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CareGreen),
            )
        }
    }
}

@Composable
private fun ScheduleEmptyState(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = CareElevation.modal,
                    shape = CareShapes.card,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.05f),
                    spotColor = CarePalette.Mint.copy(alpha = 0.12f),
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
                    .padding(CareSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(CareSpacing.md),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(CarePalette.PageBgLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Medication,
                    contentDescription = null,
                    tint = CarePalette.PrimaryBlue.copy(alpha = 0.45f),
                    modifier = Modifier.size(38.dp),
                )
            }
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = GuardianTextPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = GuardianTextSub,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )
            TextButton(onClick = onAction) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = CareGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(actionLabel, fontWeight = FontWeight.Bold, color = CareGreen)
            }
        }
    }
}

private fun buildMedicineTimelineEntries(
    generics: List<GenericReminder>,
    meds: List<Medicine>,
    onGenericToggle: (Int) -> Unit,
    onMedScheduleToggle: (Int, Int) -> Unit,
): List<TimelineEntry> {
    val out = mutableListOf<TimelineEntry>()
    generics.forEachIndexed { idx, gr ->
        val mins = parseScheduleToMinutes(gr.time)
        out.add(
            TimelineEntry(
                sortMinutes = mins ?: 720,
                dayPart = dayPartForMinutes(mins),
                timeLabel = gr.time,
                icon = gr.icon,
                title = gr.label,
                subtitle = gr.subtitle,
                chipLabel = if (gr.enabled) "Active" else "Paused",
                chipTone = if (gr.enabled) StatusChipTone.Positive else StatusChipTone.Neutral,
                switchChecked = gr.enabled,
                onToggle = { onGenericToggle(idx) },
            ),
        )
    }
    meds.forEachIndexed { medIdx, med ->
        med.schedules.forEachIndexed { schedIdx, sched ->
            val mins = parseScheduleToMinutes(sched.time)
            val subtitle =
                buildString {
                    append(sched.label)
                    append(" · ")
                    append("${if (sched.mealTiming == MealTiming.Before) "Before" else "After"} meal")
                    if (sched.withWater) append(" · With water")
                }
            out.add(
                TimelineEntry(
                    sortMinutes = mins ?: 840,
                    dayPart = dayPartForMinutes(mins),
                    timeLabel = sched.time,
                    icon = Icons.Outlined.Medication,
                    title = med.name,
                    subtitle = subtitle,
                    chipLabel =
                        when {
                            !med.isActive -> "Medicine paused"
                            sched.enabled -> "Scheduled"
                            else -> "Slot off"
                        },
                    chipTone =
                        when {
                            !med.isActive -> StatusChipTone.Warning
                            sched.enabled -> StatusChipTone.Positive
                            else -> StatusChipTone.Neutral
                        },
                    switchChecked = sched.enabled,
                    onToggle = { onMedScheduleToggle(medIdx, schedIdx) },
                ),
            )
        }
    }
    return out
}

private fun buildActivityTimelineEntries(
    activities: List<ActivityReminder>,
    onToggle: (Int) -> Unit,
): List<TimelineEntry> =
    activities.mapIndexed { idx, act ->
        val mins = parseScheduleToMinutes(act.time)
        TimelineEntry(
            sortMinutes = mins ?: 960,
            dayPart = dayPartForMinutes(mins),
            timeLabel = act.time,
            icon = act.icon,
            title = act.label,
            subtitle = "Daily activity reminder",
            chipLabel = if (act.enabled) "Active" else "Paused",
            chipTone = if (act.enabled) StatusChipTone.Positive else StatusChipTone.Neutral,
            switchChecked = act.enabled,
            onToggle = { onToggle(idx) },
        )
    }

private fun dayPartForMinutes(minutes: Int?): PlannerDayPart {
    if (minutes == null) return PlannerDayPart.Evening
    val h = minutes / 60
    return when (h) {
        in 5..11 -> PlannerDayPart.Morning
        in 12..16 -> PlannerDayPart.Afternoon
        else -> PlannerDayPart.Evening
    }
}

private fun parseScheduleToMinutes(raw: String): Int? {
    val time = raw.trim()
    if (time.isEmpty()) return null
    val lowered = time.lowercase(Locale.US)
    if ("every" in lowered || "hrs" in lowered || "hours" in lowered) return null
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
