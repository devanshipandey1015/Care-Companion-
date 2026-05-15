package com.carecompanion.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareDim
import com.carecompanion.app.ui.theme.CareElevation
import com.carecompanion.app.ui.theme.CareGradients
import com.carecompanion.app.ui.theme.CareGreen
import com.carecompanion.app.ui.theme.CarePalette
import com.carecompanion.app.ui.theme.CareShapes
import com.carecompanion.app.ui.theme.CareSpacing

// ── Legacy aliases (tokens) ────────────────────────────────────────────────────
val GuardianBg = CarePalette.PageBgLight
val GuardianPrimary = CareGreen
val GuardianTextPrimary = CarePalette.Navy
val GuardianTextSub = CarePalette.TextMuted

val ContactsGrad = Brush.linearGradient(listOf(Color(0xFF4B8B62), CareGreen))
val MedicinesGrad = Brush.linearGradient(listOf(Color(0xFF558F6A), Color(0xFF3F7E58)))
val ScheduleGrad = Brush.linearGradient(listOf(Color(0xFF5A9670), Color(0xFF4B8B62)))
val SosGrad = Brush.linearGradient(listOf(Color(0xFFF24141), Color(0xFFD62323)))

enum class BottomTab { Home, Alerts, Settings }

data class FloatingBottomNavItem(
    val icon: ImageVector,
    val label: String,
    val contentDescription: String = label,
)

// ── Premium layout primitives ──────────────────────────────────────────────────

/** Soft page canvas — spec background #F5F7FB */
@Composable
fun PremiumScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(CareGradients.pageSoftWash()),
        content = content,
    )
}

/** White card, 24.dp corners, soft shadow, interior padding 18.dp */
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .shadow(
                    elevation = CareElevation.modal,
                    shape = CareShapes.card,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.07f),
                    spotColor = CarePalette.PrimaryBlue.copy(alpha = 0.16f),
                )
                .clip(CareShapes.card)
                .background(CarePalette.CardWhite)
                .padding(CareSpacing.cardPadding),
        content = content,
    )
}

/**
 * Soft gradient header bar — navy / blue / mint wash.
 * Optional [leading] for back affordance; [trailing] for actions.
 */
@Composable
fun GradientHeader(
    title: String,
    subtitle: String = "",
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues =
        PaddingValues(
            horizontal = CareSpacing.gutterScreen,
            vertical = CareSpacing.xl,
        ),
    leading: (@Composable () -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(CareShapes.headerBottom)
                .background(CareGradients.heroNavyBlueMint())
                .statusBarsPadding()
                .padding(contentPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(CareSpacing.md))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        subtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.88f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                content = trailing,
            )
        }
    }
}

enum class StatusChipTone {
    Positive,
    Neutral,
    Warning,
    Danger,
}

@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier,
    tone: StatusChipTone = StatusChipTone.Neutral,
) {
    val (bg, fg) =
        when (tone) {
            StatusChipTone.Positive ->
                CareGreen.copy(alpha = 0.14f) to CareGreen
            StatusChipTone.Warning ->
                Color(0xFFFFF8E6) to Color(0xFFB45309)
            StatusChipTone.Danger ->
                CarePalette.Emergency.copy(alpha = 0.12f) to CarePalette.Emergency
            StatusChipTone.Neutral ->
                CarePalette.OutlineSoft.copy(alpha = 0.65f) to CarePalette.TextMuted
        }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(CareSpacing.xxl),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.22f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = CareSpacing.md, vertical = CareSpacing.xs + CareSpacing.xs),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Emergency / SOS-style filled control — full gradient, ≥52.dp tap height */
@Composable
fun EmergencyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = CareDim.buttonMinHeight)
                .clip(CareShapes.button)
                .background(if (enabled) CareGradients.emergency() else Brush.linearGradient(listOf(Color(0xFFDDDDDD), Color(0xFFCCCCCC))))
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = CareSpacing.xl, vertical = CareSpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) Color.White else CarePalette.TextMuted,
        )
    }
}

/** Compact tappable tile for home shortcuts */
@Composable
fun QuickActionCard(
    icon: ImageVector,
    label: String,
    tint: Color,
    background: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String = "",
) {
    Column(
        modifier =
            modifier
                .shadow(
                    elevation = CareElevation.sheet,
                    shape = CareShapes.card,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.05f),
                    spotColor = tint.copy(alpha = 0.12f),
                )
                .clip(CareShapes.card)
                .background(CarePalette.CardWhite)
                .clickable(onClick = onClick)
                .padding(CareSpacing.cardPadding),
    ) {
        Box(
            modifier =
                Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(CareSpacing.md))
                    .background(background),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(CareSpacing.md))
        Text(
            label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = CarePalette.Navy,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle.isNotBlank()) {
            Text(
                subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = CarePalette.TextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Info,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = CareElevation.modal,
                    shape = CareShapes.card,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.05f),
                )
                .clip(CareShapes.card)
                .background(CarePalette.CardWhite)
                .border(
                    BorderStroke(1.dp, CarePalette.OutlineSoft.copy(alpha = 0.85f)),
                    CareShapes.card,
                )
                .padding(CareSpacing.xl + CareSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(CarePalette.PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = CarePalette.PrimaryBlue, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(CareSpacing.md))
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = CarePalette.Navy,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(CareSpacing.sm))
        Text(
            message,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = CarePalette.TextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(CareSpacing.xl))
            GradientButton(text = actionLabel, onClick = onAction, modifier = Modifier.fillMaxWidth())
        }
    }
}

// ── Floating pill bottom navigation ─────────────────────────────────────────────

@Composable
fun FloatingBottomNav(
    items: List<FloatingBottomNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = CareSpacing.gutterScreen + 10.dp,
                    vertical = CareSpacing.md + 4.dp,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = CareShapes.pill,
            shadowElevation = CareElevation.sheet,
            tonalElevation = 1.dp,
            color = CarePalette.SurfaceGlassStrong,
            border = BorderStroke(1.dp, CarePalette.PrimaryBlue.copy(alpha = 0.14f)),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = CareSpacing.md, vertical = CareSpacing.sm + 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, item ->
                    FloatingNavEntry(
                        icon = item.icon,
                        label = item.label,
                        contentDescription = item.contentDescription,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingNavEntry(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (selected) CareGreen else CarePalette.TextMuted
    val pillShape = RoundedCornerShape(CareSpacing.xxl)
    Column(
        modifier =
            Modifier
                .clip(pillShape)
                .background(
                    if (selected) {
                        Brush.horizontalGradient(
                            listOf(
                                CarePalette.PrimaryBlue.copy(alpha = 0.22f),
                                CareGreen.copy(alpha = 0.16f),
                            ),
                        )
                    } else {
                        Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                    },
                )
                .clickable(onClick = onClick)
                .padding(horizontal = CareSpacing.md + CareSpacing.xs, vertical = CareSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(if (selected) 28.dp else 24.dp),
        )
        Text(
            label,
            fontSize = if (selected) 12.sp else 11.sp,
            color = tint,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun GuardianBottomBar(
    activeTab: BottomTab = BottomTab.Home,
    onHome: () -> Unit = {},
    onAlerts: () -> Unit = {},
    onSettings: () -> Unit = {},
) {
    val items =
        listOf(
            FloatingBottomNavItem(Icons.Outlined.Home, "Home"),
            FloatingBottomNavItem(Icons.Outlined.Notifications, "Alerts"),
            FloatingBottomNavItem(Icons.Outlined.Settings, "Settings"),
        )
    val selectedIndex =
        when (activeTab) {
            BottomTab.Home -> 0
            BottomTab.Alerts -> 1
            BottomTab.Settings -> 2
        }
    FloatingBottomNav(
        items = items,
        selectedIndex = selectedIndex,
        onSelect = { idx ->
            when (idx) {
                0 -> onHome()
                1 -> onAlerts()
                else -> onSettings()
            }
        },
    )
}

// ── Gradient page header (legacy API — callers unchanged) ────────────────────────
@Composable
fun GradientPageHeader(
    title: String,
    subtitle: String = "",
    gradient: Brush = Brush.linearGradient(listOf(Color(0xFF4B8B62), GuardianPrimary)),
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(gradient)
                .statusBarsPadding()
                .padding(horizontal = CareSpacing.lg, vertical = CareSpacing.lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier =
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Home,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(CareSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        subtitle,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            actions()
        }
    }
}

// ── Elder status profile card ───────────────────────────────────────────────────
@Composable
fun ElderStatusCard(
    profile: GuardianProfile,
    statusText: String = "At Home | 15 mins ago",
    isSafe: Boolean = true,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CareSpacing.xl))
                .background(Brush.linearGradient(listOf(Color(0xFF5A9670), Color(0xFF4B8B62))))
                .padding(CareSpacing.cardPadding),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CareSpacing.md + CareSpacing.xs),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                if (profile.photoUri != null) {
                    UriBitmapImage(
                        uri = profile.photoUri,
                        contentDescription = profile.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        imageVector = profile.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    profile.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    statusText,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (isSafe) {
                Surface(shape = RoundedCornerShape(CareSpacing.xl), color = CareGreen, shadowElevation = 0.dp) {
                    Row(
                        modifier = Modifier.padding(horizontal = CareSpacing.md, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(6.dp)
                                    .background(Color.White, CircleShape),
                        )
                        Text("SAFE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = GuardianTextSub,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(vertical = 2.dp),
    )
}

@Composable
fun GuardianTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    singleLine: Boolean = true,
) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = RoundedCornerShape(CareSpacing.md),
        keyboardOptions = keyboardOptions,
        colors =
            androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CareGreen.copy(alpha = 0.55f),
                focusedLabelColor = GuardianPrimary,
                focusedLeadingIconColor = GuardianPrimary,
                unfocusedBorderColor = CarePalette.OutlineSoft,
                unfocusedLabelColor = GuardianTextSub,
                focusedContainerColor = CarePalette.CardWhite,
                unfocusedContainerColor = CarePalette.CardWhite,
                disabledContainerColor = CarePalette.CardWhite,
            ),
    )
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = CareGradients.primaryCta(true),
    enabled: Boolean = true,
) {
    Box(
        modifier =
            modifier
                .shadow(
                    elevation = if (enabled) 10.dp else 4.dp,
                    shape = CareShapes.button,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.06f),
                    spotColor = CarePalette.PrimaryBlue.copy(alpha = if (enabled) 0.28f else 0.08f),
                )
                .clip(CareShapes.button)
                .background(if (enabled) gradient else CareGradients.primaryCta(false))
                .heightIn(min = 56.dp)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = CareSpacing.xl, vertical = CareSpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (enabled) Color.White else CarePalette.TextMuted,
        )
    }
}

@Composable
fun ImagePickerCard(
    label: String,
    imageUri: android.net.Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Home,
) {
    Box(
        modifier =
            modifier
                .aspectRatio(0.85f)
                .clip(RoundedCornerShape(CareSpacing.md))
                .background(if (imageUri == null) Color(0xFFF1F8F2) else Color.Transparent)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUri != null) {
            UriBitmapImage(
                uri = imageUri,
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.30f)),
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(CareSpacing.sm),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .background(
                            if (imageUri == null) Color(0xFFE3EEE6) else Color.White.copy(alpha = 0.25f),
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (imageUri == null) GuardianTextSub else Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (imageUri == null) GuardianTextSub else Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = CareSpacing.xs),
            )
        }
        if (imageUri == null) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(CareSpacing.md)),
            )
        }
    }
}
