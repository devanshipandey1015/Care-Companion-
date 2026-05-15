package com.carecompanion.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareAccessibility
import com.carecompanion.app.ui.theme.CareElevation
import com.carecompanion.app.ui.theme.CareGreen
import com.carecompanion.app.ui.theme.CarePalette
import com.carecompanion.app.ui.theme.CareRadius
import com.carecompanion.app.ui.theme.CareSpacing

/** Full-width SOS strip — wire [onClick] to your SOS flow. */
@Composable
fun CareSosStripCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(CareElevation.overlay, RoundedCornerShape(CareRadius.lg))
                .clip(RoundedCornerShape(CareRadius.lg))
                .clickable(onClick = onClick),
        color = Color.Transparent,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF5252), CarePalette.Emergency, Color(0xFFD50000)),
                        ),
                    )
                    .padding(CareSpacing.lg),
        ) {
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

/** Neutral elevated wellness / vitals summary. */
@Composable
fun CareHealthSummaryCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    iconTint: Color = CarePalette.SoftBlue,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CareRadius.xl),
        elevation = CardDefaults.cardElevation(defaultElevation = CareElevation.card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CarePalette.OutlineSoft.copy(alpha = 0.65f)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(CareSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier =
                        Modifier
                            .size(CareAccessibility.MinIconWellDp.dp)
                            .clip(RoundedCornerShape(CareRadius.sm))
                            .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(leadingIcon, contentDescription = null, tint = iconTint, modifier = Modifier.size(26.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CarePalette.Navy)
                Text(
                    body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CarePalette.TextMuted,
                    modifier = Modifier.padding(top = 6.dp),
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

/** Compact contact row — avatar slot optional; caller handles dial intent. */
@Composable
fun CareContactTile(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    avatarContent: (@Composable () -> Unit)? = null,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(CareElevation.raised, RoundedCornerShape(CareRadius.lg))
                .clip(RoundedCornerShape(CareRadius.lg))
                .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, CarePalette.OutlineSoft.copy(alpha = 0.55f)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(CareSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(CareAccessibility.MinIconWellDp.dp)
                        .clip(CircleShape)
                        .background(CarePalette.SoftBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                avatarContent?.invoke()
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = CarePalette.Navy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, fontSize = 14.sp, color = CarePalette.TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

/** Medication reminder tile with accent via border tint. */
@Composable
fun CareMedicationReminderCard(
    name: String,
    scheduleLine: String,
    detailLine: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = CareGreen,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        shape = RoundedCornerShape(CareRadius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = CareElevation.card),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
    ) {
        Column(modifier = Modifier.padding(CareSpacing.lg)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = CarePalette.Navy)
            Text(scheduleLine, fontSize = 14.sp, color = accent, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
            Text(detailLine, fontSize = 14.sp, color = CarePalette.TextMuted, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

/** Timeline-style alert row with colored severity rail. */
@Composable
fun CareAlertTimelineItem(
    title: String,
    timestamp: String,
    severityColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.NotificationsActive,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CareRadius.md))
                .background(MaterialTheme.colorScheme.surface)
                .padding(CareSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(severityColor),
        )
        Icon(icon, contentDescription = null, tint = severityColor, modifier = Modifier.size(22.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = CarePalette.Navy)
            Text(timestamp, fontSize = 13.sp, color = CarePalette.TextMuted, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

/** Rounded navigation pills for elder-friendly hub switching. */
@Composable
fun CareNavigationTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(CareElevation.sticky, RoundedCornerShape(CareRadius.xxl)),
        shape = RoundedCornerShape(CareRadius.xxl),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, CarePalette.OutlineSoft.copy(alpha = 0.45f)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(CareSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                Surface(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(CareRadius.xl))
                            .clickable { onSelect(index) },
                    shape = RoundedCornerShape(CareRadius.xl),
                    color =
                        if (selected) {
                            CarePalette.SoftBlue.copy(alpha = 0.22f)
                        } else {
                            Color.Transparent
                        },
                    border =
                        if (selected) {
                            BorderStroke(1.dp, CarePalette.SoftBlue.copy(alpha = 0.35f))
                        } else {
                            null
                        },
                    shadowElevation = if (selected) CareElevation.sticky else CareElevation.flat,
                ) {
                    Text(
                        text = label,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = CareSpacing.md),
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        color = CarePalette.Navy,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
