package com.carecompanion.app

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.FlipToBack
import androidx.compose.material.icons.outlined.FlipToFront
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareDim
import com.carecompanion.app.ui.theme.CareElevation
import com.carecompanion.app.ui.theme.CareGreen
import com.carecompanion.app.ui.theme.CarePalette
import com.carecompanion.app.ui.theme.CareShapes
import com.carecompanion.app.ui.theme.CareSpacing
import java.util.UUID

private val FormOptions = listOf("Tablet", "Capsule", "Syrup", "Drops", "Injection")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GuardianAddMedicineScreen(
    profile: GuardianProfile,
    onBack: () -> Unit,
    onSave: (Medicine) -> Unit,
) {
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedForm by remember { mutableStateOf("Tablet") }
    var pillUri by remember { mutableStateOf<Uri?>(null) }
    var packetFrontUri by remember { mutableStateOf<Uri?>(null) }
    var packetBackUri by remember { mutableStateOf<Uri?>(null) }
    var snackMsg by remember { mutableStateOf<String?>(null) }

    var activePickerTarget by remember { mutableStateOf("") }

    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            when (activePickerTarget) {
                "pill" -> pillUri = uri
                "packetFront" -> packetFrontUri = uri
                "packetBack" -> packetBackUri = uri
            }
        }

    fun launchPicker(target: String) {
        activePickerTarget = target
        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackMsg) {
        snackMsg?.let {
            snackbarHostState.showSnackbar(it)
            snackMsg = null
        }
    }

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
                        .background(MedicinesGrad)
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
                            text = "Add Medicine",
                            style =
                                MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 23.sp,
                                ),
                        )
                        Text(
                            text = "Log details for ${profile.name}",
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
                Spacer(Modifier.height(CareSpacing.md))

                FormSectionCard(title = "Medicine Details", subtitle = "Name as printed on the packaging.") {
                    OutlinedTextField(
                        value = medicineName,
                        onValueChange = { medicineName = it },
                        label = { Text("Medicine name") },
                        placeholder = { Text("e.g. Metformin") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Medication, contentDescription = null, tint = CarePalette.PrimaryBlue, modifier = Modifier.size(22.dp))
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = CareDim.textFieldMinHeight + 8.dp),
                        shape = fieldShape,
                        colors = fieldColors,
                        singleLine = true,
                    )
                }

                FormSectionCard(title = "Dosage", subtitle = "Strength and physical form.") {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage") },
                        placeholder = { Text("e.g. 500 mg · once daily") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Scale, contentDescription = null, tint = CarePalette.PrimaryBlue, modifier = Modifier.size(22.dp))
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = CareDim.textFieldMinHeight + 8.dp),
                        shape = fieldShape,
                        colors = fieldColors,
                        singleLine = true,
                    )
                    Spacer(Modifier.height(CareSpacing.md))
                    Text(
                        text = "Form",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = GuardianTextPrimary,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                    ) {
                        FormOptions.forEach { form ->
                            val sel = selectedForm == form
                            FilterChip(
                                selected = sel,
                                onClick = { selectedForm = form },
                                label = {
                                    Text(form, fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
                                },
                                border = BorderStroke(1.dp, if (sel) CarePalette.PrimaryBlue else CarePalette.OutlineSoft),
                                colors =
                                    FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CareGreen.copy(alpha = 0.18f),
                                        selectedLabelColor = CarePalette.Navy,
                                        selectedLeadingIconColor = CareGreen,
                                        containerColor = CarePalette.PageBgLight,
                                        labelColor = GuardianTextSub,
                                    ),
                                shape = RoundedCornerShape(14.dp),
                            )
                        }
                    }
                }

                FormSectionCard(title = "Schedule", subtitle = "Reminder slots are added after this medicine is saved.") {
                    Row(horizontalArrangement = Arrangement.spacedBy(CareSpacing.md), verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = CarePalette.PrimaryBlue,
                            modifier = Modifier.size(24.dp),
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Next step",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GuardianTextPrimary,
                            )
                            Text(
                                text = "From Manage Medicines, choose Schedule Medicine to tie doses to meals and pick confirmation times.",
                                fontSize = 13.sp,
                                color = GuardianTextSub,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                }

                FormSectionCard(title = "Reminder Settings", subtitle = "How alerts behave on devices.") {
                    Row(horizontalArrangement = Arrangement.spacedBy(CareSpacing.md), verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Outlined.NotificationsActive,
                            contentDescription = null,
                            tint = CarePalette.PrimaryBlue,
                            modifier = Modifier.size(24.dp),
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Notifications",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GuardianTextPrimary,
                            )
                            Text(
                                text = "Standard reminders respect system notification settings. Critical SOS flows use your configured escalation path.",
                                fontSize = 13.sp,
                                color = GuardianTextSub,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                }

                FormSectionCard(title = "Photos", subtitle = "Pill and packet images help caregivers recognize the right medicine.") {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm)) {
                        Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = CareGreen, modifier = Modifier.size(22.dp))
                        Text(
                            text = "Gallery uploads only · tap a tile to replace.",
                            fontSize = 12.sp,
                            color = GuardianTextSub,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                    ) {
                        ImagePickerCard(
                            label = "Pill\nImage",
                            imageUri = pillUri,
                            onClick = { launchPicker("pill") },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Medication,
                        )
                        ImagePickerCard(
                            label = "Packet\nFront",
                            imageUri = packetFrontUri,
                            onClick = { launchPicker("packetFront") },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.FlipToFront,
                        )
                        ImagePickerCard(
                            label = "Packet\nBack",
                            imageUri = packetBackUri,
                            onClick = { launchPicker("packetBack") },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.FlipToBack,
                        )
                    }
                }

                if (pillUri != null || packetFrontUri != null || packetBackUri != null) {
                    MedicineImagePreviewStrip(
                        pillUri = pillUri,
                        packetFrontUri = packetFrontUri,
                        packetBackUri = packetBackUri,
                    )
                }

                Spacer(Modifier.height(CareSpacing.md))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(CareSpacing.md),
                    color = CarePalette.PrimaryBlue.copy(alpha = 0.06f),
                    border = BorderStroke(1.dp, CarePalette.PrimaryBlue.copy(alpha = 0.15f)),
                ) {
                    Row(
                        modifier = Modifier.padding(CareSpacing.md),
                        horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = CarePalette.PrimaryBlue, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Saving adds this medicine to the list. Open Schedule Medicine anytime to attach meal-based reminders.",
                            fontSize = 12.sp,
                            color = GuardianTextSub,
                            lineHeight = 17.sp,
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
                        text = "Save Medicine",
                        onClick = {
                            if (medicineName.isNotBlank()) {
                                onSave(
                                    Medicine(
                                        id = UUID.randomUUID().toString(),
                                        name = medicineName.trim(),
                                        dosage = dosage.trim(),
                                        form = selectedForm,
                                        pillImageUri = pillUri,
                                        packetFrontUri = packetFrontUri,
                                        packetBackUri = packetBackUri,
                                    ),
                                )
                            } else {
                                snackMsg = "Please enter medicine name"
                            }
                        },
                        gradient = MedicinesGrad,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
                        enabled = medicineName.isNotBlank(),
                    )
                }
            }
        }
    }
}

@Composable
private fun FormSectionCard(
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

@Composable
private fun MedicineImagePreviewStrip(
    pillUri: Uri?,
    packetFrontUri: Uri?,
    packetBackUri: Uri?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = CareElevation.card,
                    shape = CareShapes.card,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.05f),
                    spotColor = CareGreen.copy(alpha = 0.12f),
                ),
        shape = CareShapes.card,
        color = CarePalette.CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(CareSpacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(CareSpacing.sm),
        ) {
            Text(
                text = "Image preview",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = GuardianTextPrimary,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm),
            ) {
                pillUri?.let {
                    PreviewThumb(uri = it, label = "Pill", Modifier.weight(1f))
                }
                packetFrontUri?.let {
                    PreviewThumb(uri = it, label = "Front", Modifier.weight(1f))
                }
                packetBackUri?.let {
                    PreviewThumb(uri = it, label = "Back", Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PreviewThumb(
    uri: Uri,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CarePalette.OutlineSoft.copy(alpha = 0.65f), RoundedCornerShape(14.dp)),
        ) {
            UriBitmapImage(
                uri = uri,
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GuardianTextSub)
    }
}
