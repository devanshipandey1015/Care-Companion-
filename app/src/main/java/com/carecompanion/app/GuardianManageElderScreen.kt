package com.carecompanion.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SwitchAccount
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareGreen

private val Navy = Color(0xFF14213D)
private val SoftBlue = Color(0xFF4EA8DE)
private val Mint = Color(0xFF7BD389)
private val DashboardBg = Color(0xFFF5F7FB)
private val Emergency = Color(0xFFFF4D4D)
private val TextPrimary = Color(0xFF14213D)
private val TextSub = Color(0xFF5C6578)
private val LineMuted = Color(0xFFE2E8F2)

private data class DashboardAlert(
    val title: String,
    val time: String,
    val accent: Color,
)

@Composable
fun GuardianManageElderScreen(
    profile: GuardianProfile,
    onBack: () -> Unit,
    onSwitchProfiles: () -> Unit,
    onLogout: () -> Unit,
    onOpenContacts: () -> Unit = {},
    onOpenMedicines: () -> Unit = {},
    onOpenDailySchedule: () -> Unit = {},
    onOpenWellnessSos: () -> Unit = {},
) {
    val alerts = remember(profile.name) {
        listOf(
            DashboardAlert(
                title = "${profile.name} missed 8:00 AM medicine",
                time = "about 1 hr ago",
                accent = Color(0xFFF97316),
            ),
            DashboardAlert(
                title = "SOS triggered · Mira Road, Mumbai",
                time = "about 2 hr ago",
                accent = Emergency,
            ),
            DashboardAlert(
                title = "Morning vitals summary ready",
                time = "Today · 8:02 AM",
                accent = SoftBlue,
            ),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DashboardBg,
        bottomBar = {
            GuardianBottomBar(
                activeTab = BottomTab.Home,
                onHome = onBack,
                onAlerts = onOpenWellnessSos,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            PremiumDashboardHeader(
                profile = profile,
                onBack = onBack,
                onSwitchProfiles = onSwitchProfiles,
                onLogout = onLogout,
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 8 },
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    QuickSosShortcut(onClick = onOpenWellnessSos)

                    Text(
                        text = "Quick actions",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSub,
                        letterSpacing = 0.6.sp,
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            PremiumActionTile(
                                title = "Contacts",
                                subtitle = "Emergency list & calls",
                                icon = Icons.Outlined.Call,
                                gradient = Brush.linearGradient(listOf(SoftBlue, Navy.copy(blue = 0.72f))),
                                modifier = Modifier.weight(1f),
                                onClick = onOpenContacts,
                            )
                            PremiumActionTile(
                                title = "Medicines",
                                subtitle = "Doses & instructions",
                                icon = Icons.Outlined.Medication,
                                gradient = Brush.linearGradient(listOf(Mint, Color(0xFF2E7D32))),
                                modifier = Modifier.weight(1f),
                                onClick = onOpenMedicines,
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            PremiumActionTile(
                                title = "Schedule",
                                subtitle = "Reminders & routines",
                                icon = Icons.Outlined.Schedule,
                                gradient = Brush.linearGradient(listOf(Color(0xFF5C6BC0), Color(0xFF3949AB))),
                                modifier = Modifier.weight(1f),
                                onClick = onOpenDailySchedule,
                            )
                            PremiumActionTile(
                                title = "Wellness",
                                subtitle = "SOS & tracking",
                                icon = Icons.Outlined.FavoriteBorder,
                                gradient = Brush.linearGradient(listOf(Color(0xFFFF5252), Color(0xFFB71C1C))),
                                modifier = Modifier.weight(1f),
                                onClick = onOpenWellnessSos,
                            )
                        }
                    }

                    HealthSummaryStrip()

                    AlertsTimelineSection(
                        profileName = profile.name,
                        alerts = alerts,
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun PremiumDashboardHeader(
    profile: GuardianProfile,
    onBack: () -> Unit,
    onSwitchProfiles: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Navy, Color(0xFF1C3A63), SoftBlue.copy(alpha = 0.95f)),
                    ),
                )
                .statusBarsPadding()
                .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 52.dp),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = SoftBlue.copy(alpha = 0.12f), radius = size.width * 0.35f, center = Offset(size.width * 0.9f, size.height * 0.1f))
                drawCircle(color = Mint.copy(alpha = 0.1f), radius = size.width * 0.28f, center = Offset(size.width * 0.08f, size.height * 0.85f))
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f))
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Care dashboard",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.2).sp,
                        )
                        Text(
                            text = "Overview · vitals · safety at a glance",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.82f),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    TextButton(onClick = onSwitchProfiles) {
                        Icon(
                            Icons.Outlined.SwitchAccount,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Switch", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = Color(0xFFFFB4B4), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    InsightChip(text = "Status: Stable", tint = Mint)
                    InsightChip(text = "2 alerts", tint = Color(0xFFFFB74D))
                    InsightChip(text = "Adherence 87%", tint = SoftBlue)
                }
            }
        }

        PatientOverviewCard(
            profile = profile,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-40).dp),
        )
    }
}

@Composable
private fun InsightChip(text: String, tint: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.14f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(tint),
            )
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.95f),
            )
        }
    }
}

@Composable
private fun PatientOverviewCard(
    profile: GuardianProfile,
    modifier: Modifier = Modifier,
) {
    val pulse = rememberInfiniteTransition(label = "livePulse")
    val liveAlpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "liveDot",
    )

    Surface(
        modifier = modifier
            .shadow(18.dp, RoundedCornerShape(26.dp), spotColor = SoftBlue.copy(alpha = 0.22f)),
        shape = RoundedCornerShape(26.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .shadow(10.dp, CircleShape)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(listOf(SoftBlue, Mint)),
                            shape = CircleShape,
                        )
                        .background(profile.bg.copy(alpha = 0.35f)),
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
                            profile.icon,
                            contentDescription = null,
                            tint = Navy.copy(alpha = 0.85f),
                            modifier = Modifier.size(44.dp),
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 24.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E).copy(alpha = liveAlpha)),
                        )
                        Text(
                            text = "Live · At home",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CareGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Mint.copy(alpha = 0.14f),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    border = BorderStroke(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(listOf(SoftBlue, Mint)),
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = Navy,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "SAFE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Navy,
                            letterSpacing = 1.sp,
                            maxLines = 1,
                        )
                    }
                }
            }

            Text(
                text = "Last updated · 15 minutes ago",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSub.copy(alpha = 0.85f),
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun QuickSosShortcut(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(20.dp), spotColor = Emergency.copy(alpha = 0.35f))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFFFF5252), Emergency, Color(0xFFB71C1C))))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.WarningAmber,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Quick SOS",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                text = "Open wellness center · alerts & SOS history",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.88f),
            )
        }
        Icon(
            Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun PremiumActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .heightIn(min = 132.dp)
            .shadow(12.dp, RoundedCornerShape(22.dp), ambientColor = Navy.copy(alpha = 0.06f))
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Icon(
            Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.55f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(20.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = Color.White.copy(alpha = 0.88f),
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun HealthSummaryStrip() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.MonitorHeart,
                            contentDescription = null,
                            tint = SoftBlue,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Column {
                        Text(
                            text = "Health pulse",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSub,
                            letterSpacing = 0.4.sp,
                        )
                        Text(
                            text = "Steady rhythm · no spikes",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Medication adherence",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSub,
                    letterSpacing = 0.4.sp,
                )
                Text(
                    text = "87%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Navy,
                )
                LinearProgressIndicator(
                    progress = { 0.87f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = Mint,
                    trackColor = Mint.copy(alpha = 0.18f),
                    strokeCap = StrokeCap.Round,
                )
                Text(
                    text = "Based on last 7 days",
                    fontSize = 11.sp,
                    color = TextSub,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun AlertsTimelineSection(profileName: String, alerts: List<DashboardAlert>) {
    val urgentCount = alerts.count { it.accent == Emergency || it.accent == Color(0xFFF97316) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(10.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        Icons.Outlined.NotificationsActive,
                        contentDescription = null,
                        tint = Emergency,
                        modifier = Modifier.size(22.dp),
                    )
                    Column {
                        Text(
                            text = "Recent activity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary,
                        )
                        Text(
                            text = "Timeline for ${profileName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSub,
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Emergency.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = "$urgentCount need review",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emergency,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = LineMuted,
            )

            alerts.forEachIndexed { index, alert ->
                TimelineAlertRow(
                    alert = alert,
                    isLast = index == alerts.lastIndex,
                )
                if (index < alerts.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TimelineAlertRow(
    alert: DashboardAlert,
    isLast: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(22.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .clip(CircleShape)
                    .background(alert.accent),
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(52.dp)
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(LineMuted),
                )
            }
        }

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            color = DashboardBg.copy(alpha = 0.85f),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(1.dp, alert.accent.copy(alpha = 0.22f)),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = alert.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    lineHeight = 18.sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(alert.accent.copy(alpha = 0.14f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = when (alert.accent) {
                                Emergency -> "Critical"
                                Color(0xFFF97316) -> "Attention"
                                else -> "Info"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = alert.accent,
                            letterSpacing = 0.4.sp,
                        )
                    }
                    Text(
                        text = alert.time,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSub,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}
