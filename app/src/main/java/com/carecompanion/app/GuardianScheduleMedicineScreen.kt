package com.carecompanion.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareDim
import com.carecompanion.app.ui.theme.CareElevation
import com.carecompanion.app.ui.theme.CareGreen
import com.carecompanion.app.ui.theme.CarePalette
import com.carecompanion.app.ui.theme.CareShapes
import com.carecompanion.app.ui.theme.CareSpacing

private val MealOptions = listOf("Breakfast", "Lunch", "Dinner")
private val TimePresets = listOf("7:00 AM", "8:00 AM", "12:00 PM", "2:00 PM", "7:00 PM", "9:00 PM")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GuardianScheduleMedicineScreen(
    medicines: List<Medicine>,
    onBack: () -> Unit,
    onSave: (List<Medicine>) -> Unit,
) {
    var selectedMedicineId by remember { mutableStateOf<String?>(null) }
    var selectedMeals by remember { mutableStateOf(setOf<String>()) }
    var mealTiming by remember { mutableStateOf(MealTiming.Before) }
    var withWater by remember { mutableStateOf(false) }
    var customTime by remember { mutableStateOf("8:00 AM") }
    var snackMsg by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackMsg) {
        snackMsg?.let {
            snackbarHostState.showSnackbar(it)
            snackMsg = null
        }
    }

    val selectedMedicine = medicines.firstOrNull { it.id == selectedMedicineId }

    val fieldShape = RoundedCornerShape(18.dp)
    val fieldColors =
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CarePalette.PrimaryBlue,
            unfocusedBorderColor = CarePalette.OutlineSoft,
            cursorColor = CarePalette.PrimaryBlue,
            focusedLabelColor = CarePalette.PrimaryBlue,
            unfocusedLabelColor = CarePalette.TextMuted,
            focusedTextColor = CarePalette.Navy,
            unfocusedTextColor = CarePalette.Navy,
            focusedContainerColor = CarePalette.PageBgLight,
            unfocusedContainerColor = CarePalette.PageBgLight,
        )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = GuardianBg,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
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
                    Spacer(Modifier.width(CareSpacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Schedule Medicine",
                            style =
                                MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 23.sp,
                                ),
                        )
                        Text(
                            text = "Align doses with meals and confirmations",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.88f),
                                    fontSize = 13.sp,
                                ),
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = CareSpacing.gutterScreen),
                verticalArrangement = Arrangement.spacedBy(CareSpacing.lg),
            ) {
                Spacer(Modifier.height(CareSpacing.sm))

                ScheduleSectionCard(
                    title = "Medicine Details",
                    subtitle = "Pick what this reminder applies to.",
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Medication, contentDescription = null, tint = CareGreen, modifier = Modifier.size(22.dp))
                        Text(
                            text = "Available medicines",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GuardianTextPrimary,
                        )
                    }
                    if (medicines.isEmpty()) {
                        Text(
                            text = "No medicines found. Add a medicine first.",
                            fontSize = 13.sp,
                            color = GuardianTextSub,
                            lineHeight = 18.sp,
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(CareSpacing.sm)) {
                            medicines.forEach { med ->
                                val isSelected = selectedMedicineId == med.id
                                Surface(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(18.dp))
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color =
                                                    if (isSelected) CareGreen else CarePalette.OutlineSoft.copy(alpha = 0.85f),
                                                shape = RoundedCornerShape(18.dp),
                                            )
                                            .clickable { selectedMedicineId = med.id },
                                    shape = RoundedCornerShape(18.dp),
                                    color =
                                        if (isSelected) CareGreen.copy(alpha = 0.08f) else CarePalette.CardWhite,
                                    tonalElevation = 0.dp,
                                    shadowElevation = 0.dp,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(CareSpacing.md),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
                                    ) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .size(44.dp)
                                                    .background(
                                                        if (isSelected) CareGreen else CarePalette.PageBgLight,
                                                        CircleShape,
                                                    ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                Icons.Outlined.Medication,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else GuardianTextSub,
                                                modifier = Modifier.size(22.dp),
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                med.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = if (isSelected) CareGreen else GuardianTextPrimary,
                                            )
                                            val detail =
                                                listOfNotNull(
                                                    med.dosage.takeIf { it.isNotBlank() },
                                                    med.form.takeIf { it.isNotBlank() },
                                                ).joinToString(" · ")
                                            if (detail.isNotBlank()) {
                                                Text(detail, fontSize = 13.sp, color = GuardianTextSub)
                                            }
                                        }
                                        if (isSelected) {
                                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = CareGreen, modifier = Modifier.size(24.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                ScheduleSectionCard(
                    title = "Dosage context",
                    subtitle = "Shown for the medicine you selected.",
                ) {
                    if (selectedMedicine == null) {
                        Text(
                            text = "Select a medicine above to review dose details.",
                            fontSize = 13.sp,
                            color = GuardianTextSub,
                            lineHeight = 18.sp,
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(CarePalette.PageBgLight)
                                        .padding(horizontal = CareSpacing.md, vertical = CareSpacing.sm + 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Strength", fontWeight = FontWeight.Medium, color = GuardianTextSub, fontSize = 13.sp)
                                Text(
                                    selectedMedicine.dosage.ifBlank { "—" },
                                    fontWeight = FontWeight.Bold,
                                    color = GuardianTextPrimary,
                                    fontSize = 14.sp,
                                )
                            }
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(CarePalette.PageBgLight)
                                        .padding(horizontal = CareSpacing.md, vertical = CareSpacing.sm + 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Form", fontWeight = FontWeight.Medium, color = GuardianTextSub, fontSize = 13.sp)
                                Text(
                                    selectedMedicine.form,
                                    fontWeight = FontWeight.Bold,
                                    color = GuardianTextPrimary,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }

                ScheduleSectionCard(
                    title = "Schedule",
                    subtitle = "Choose meals and the reminder window.",
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = CareGreen, modifier = Modifier.size(22.dp))
                        Text(
                            text = "Meals",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GuardianTextPrimary,
                        )
                    }
                    Text(
                        text = "Select one or more anchor meals:",
                        fontSize = 12.sp,
                        color = GuardianTextSub,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                    ) {
                        MealOptions.forEach { meal ->
                            val selected = meal in selectedMeals
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    selectedMeals = if (selected) selectedMeals - meal else selectedMeals + meal
                                },
                                label = {
                                    Text(meal, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
                                },
                                border = BorderStroke(1.dp, if (selected) CareGreen else CarePalette.OutlineSoft),
                                colors =
                                    FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CareGreen.copy(alpha = 0.2f),
                                        selectedLabelColor = CarePalette.Navy,
                                        containerColor = CarePalette.PageBgLight,
                                        labelColor = GuardianTextSub,
                                    ),
                                shape = RoundedCornerShape(14.dp),
                            )
                        }
                    }
                    HorizontalDivider(color = CarePalette.OutlineSoft.copy(alpha = 0.55f))
                    Text(
                        text = "Timing vs meal",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = GuardianTextPrimary,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm)) {
                        listOf(MealTiming.Before to "Before meal", MealTiming.After to "After meal").forEach { (timing, label) ->
                            val selected = mealTiming == timing
                            Surface(
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { mealTiming = timing },
                                shape = RoundedCornerShape(16.dp),
                                color = if (selected) CareGreen else CarePalette.PageBgLight,
                                border = BorderStroke(1.dp, if (selected) CareGreen.copy(alpha = 0.55f) else CarePalette.OutlineSoft.copy(alpha = 0.65f)),
                            ) {
                                Text(
                                    label,
                                    modifier = Modifier.padding(horizontal = CareSpacing.lg, vertical = CareSpacing.sm + 4.dp),
                                    fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Color.White else GuardianTextSub,
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = CarePalette.OutlineSoft.copy(alpha = 0.55f))
                    Row(horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Schedule, contentDescription = null, tint = CareGreen, modifier = Modifier.size(22.dp))
                        Text(
                            text = "Reminder time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GuardianTextPrimary,
                        )
                    }
                    Text(
                        text = "Quick presets",
                        fontSize = 12.sp,
                        color = GuardianTextSub,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                    ) {
                        TimePresets.forEach { preset ->
                            val selected = customTime == preset
                            Surface(
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { customTime = preset },
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) CareGreen else CarePalette.PageBgLight,
                                border = BorderStroke(1.dp, if (selected) CareGreen.copy(alpha = 0.6f) else CarePalette.OutlineSoft.copy(alpha = 0.65f)),
                            ) {
                                Text(
                                    preset,
                                    modifier = Modifier.padding(horizontal = CareSpacing.md + 2.dp, vertical = CareSpacing.sm),
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Color.White else GuardianTextSub,
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = customTime,
                        onValueChange = { customTime = it },
                        label = { Text("Custom time") },
                        placeholder = { Text("e.g. 6:30 AM") },
                        leadingIcon = {
                            Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = CarePalette.PrimaryBlue, modifier = Modifier.size(22.dp))
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = CareDim.textFieldMinHeight + 8.dp),
                        shape = fieldShape,
                        colors = fieldColors,
                        singleLine = true,
                    )
                }

                ScheduleSectionCard(
                    title = "Reminder Settings",
                    subtitle = "Fine tune how we phrase this reminder.",
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(48.dp)
                                    .background(if (withWater) CareGreen.copy(alpha = 0.12f) else CarePalette.PageBgLight, CircleShape)
                                    .border(
                                        1.dp,
                                        if (withWater) CareGreen.copy(alpha = 0.35f) else CarePalette.OutlineSoft.copy(alpha = 0.5f),
                                        CircleShape,
                                    ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Outlined.LocalDrink,
                                contentDescription = null,
                                tint = if (withWater) CareGreen else GuardianTextSub,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Take with water",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = GuardianTextPrimary,
                            )
                            Text(
                                text = if (withWater) "Include hydration cue on alerts." else "Skip hydration wording.",
                                fontSize = 13.sp,
                                color = GuardianTextSub,
                            )
                        }
                        Switch(
                            checked = withWater,
                            onCheckedChange = { withWater = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CareGreen),
                        )
                    }
                }

                Spacer(Modifier.height(88.dp))
            }

            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 12.dp,
                color = CarePalette.CardWhite,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = CareSpacing.gutterScreen, vertical = CareSpacing.md),
                ) {
                    GradientButton(
                        text = "Add to Schedule",
                        onClick = {
                            val medId = selectedMedicineId
                            if (medId == null) {
                                snackMsg = "Please select a medicine"
                                return@GradientButton
                            }
                            if (selectedMeals.isEmpty()) {
                                snackMsg = "Please select at least one meal"
                                return@GradientButton
                            }
                            val updatedMedicines =
                                medicines.map { med ->
                                    if (med.id != medId) med
                                    else {
                                        val newSlots =
                                            selectedMeals.map { meal ->
                                                MedicineSchedule(
                                                    label = meal,
                                                    time = customTime,
                                                    enabled = true,
                                                    withWater = withWater,
                                                    mealTiming = mealTiming,
                                                )
                                            }
                                        val existingLabels = med.schedules.map { it.label }.toSet()
                                        val merged = med.schedules.toMutableList()
                                        newSlots.forEach { slot ->
                                            if (slot.label !in existingLabels) merged.add(slot)
                                            else {
                                                val idx = merged.indexOfFirst { it.label == slot.label }
                                                if (idx >= 0) merged[idx] = slot
                                            }
                                        }
                                        med.copy(schedules = merged)
                                    }
                                }
                            onSave(updatedMedicines)
                        },
                        gradient = ScheduleGrad,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleSectionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = CareElevation.card,
                    shape = CareShapes.card,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.06f),
                    spotColor = CarePalette.PrimaryBlue.copy(alpha = 0.1f),
                ),
        shape = CareShapes.card,
        color = CarePalette.CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(CareSpacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(CareSpacing.md),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = GuardianTextPrimary,
                        ),
                )
                Text(
                    text = subtitle,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = GuardianTextSub,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                        ),
                )
            }
            content()
        }
    }
}
