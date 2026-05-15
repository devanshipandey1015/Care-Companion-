package com.carecompanion.app

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Emergency
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareGradients
import com.carecompanion.app.ui.theme.CareGreen

private val NavyDeep = Color(0xFF14213D)
private val EmergencyRed = Color(0xFFDC2626)
private val EmergencyDeep = Color(0xFFB91C1C)
private val SoftBg = Color(0xFFF5F7FB)
private val CallGradient = Brush.linearGradient(listOf(Color(0xFF2F855A), CareGreen))
private val MapGradient = Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF4EA8DE)))

@Composable
fun GuardianWellnessSosScreen(
    profile: GuardianProfile,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit = {},
) {
    var alertDismissed by remember { mutableStateOf(false) }

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
                activeTab = BottomTab.Alerts,
                onHome = onNavigateHome,
                onAlerts = {},
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            EmergencyResponseHero(
                profileName = profile.name,
                onBack = onBack,
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .offset(y = (-26).dp)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                WellnessDashboardMetrics()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "Safety overview",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep.copy(alpha = 0.55f),
                            letterSpacing = 0.6.sp,
                        )
                        Text(
                            text = if (!alertDismissed) "Incident open" else "All quiet",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep,
                        )
                    }
                    if (!alertDismissed) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmergencyRed,
                            shadowElevation = 4.dp,
                        ) {
                            Text(
                                text = "SOS LIVE",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                    }
                }

                if (!alertDismissed) {
                    FloatingSosAlertCard(
                        profile = profile,
                        onDismiss = { alertDismissed = true },
                    )

                    EmergencyQuickActionsRow(profileName = profile.name)

                    IncidentProtocolCard(profileName = profile.name)
                } else {
                    AllClearCard(profileName = profile.name)
                    WellnessSummaryCard()
                }
            }

            Spacer(Modifier.height(8.dp))
        }
        }
    }
}

@Composable
private fun EmergencyResponseHero(
    profileName: String,
    onBack: () -> Unit,
) {
    val infinite = rememberInfiniteTransition(label = "sosHero")
    val glowPulse by infinite.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.48f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )
    val ringExpand by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ring",
    )
    val ringFade by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringFade",
    )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors =
                            listOf(
                                Color(0xFF9B1C1C),
                                EmergencyRed,
                                NavyDeep,
                                Color(0xFF1E40AF),
                            ),
                    ),
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    SoftBg.copy(alpha = 0.92f),
                                    SoftBg,
                                ),
                        ),
                    ),
        )

        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors =
                                listOf(
                                    Color(0xFFFF6B6B).copy(alpha = glowPulse * 0.45f),
                                    Color.Transparent,
                                ),
                            center = Offset(0.35f, 0.25f),
                            radius = 520f,
                        ),
                    ),
        )

        Column(
            modifier =
                Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 52.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Wellness & SOS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 6.dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4ADE80)),
                        )
                        Text(
                            text = "Live monitoring · Connected",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.92f),
                        )
                    }
                    Text(
                        text = profileName,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.82f),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier.size(132.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size((104 * ringExpand).dp)
                        .graphicsLayer { alpha = ringFade }
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f)),
                )
                Box(
                    modifier = Modifier
                        .size((76 * ringExpand).dp)
                        .graphicsLayer { alpha = ringFade.coerceAtLeast(0.12f) }
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f)),
                )
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 12.dp,
                    modifier = Modifier.size(76.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Outlined.Emergency,
                            contentDescription = null,
                            tint = EmergencyRed,
                            modifier = Modifier.size(38.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Stay calm · verify · respond",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.92f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            Text(
                text = "We've alerted your circle and locked onto GPS.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.78f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 28.dp, end = 28.dp, top = 4.dp),
            )
        }
    }
}

@Composable
private fun FloatingSosAlertCard(
    profile: GuardianProfile,
    onDismiss: () -> Unit,
) {
    val trackPulse by rememberInfiniteTransition(label = "track").animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "trackDot",
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = Color.White,
        shadowElevation = 18.dp,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, EmergencyRed.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = EmergencyRed.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, EmergencyRed.copy(alpha = 0.25f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = EmergencyDeep,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Critical severity",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyDeep,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Triggered",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B),
                    )
                    Text(
                        text = "10:31 AM",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep,
                    )
                    Text(
                        text = "Sat · Today · IST",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFE8F4FD), Color(0xFFEAF6EC)),
                            ),
                        ),
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
                            tint = NavyDeep.copy(alpha = 0.65f),
                            modifier = Modifier.size(34.dp),
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep,
                        lineHeight = 28.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Emergency triggered · awaiting contact",
                        fontSize = 13.sp,
                        color = EmergencyRed,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "Guardian push · Delivered",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, CareGreen.copy(alpha = 0.35f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = CareGreen,
                        modifier = Modifier.size(22.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Emergency contacts notified",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NavyDeep,
                        )
                        Text(
                            text = "SMS delivered to primary & secondary contacts.",
                            fontSize = 12.sp,
                            color = Color(0xFF475569),
                        )
                    }
                }
            }

            LiveTrackingStrip(pulseScale = trackPulse)

            HorizontalDivider(color = Color(0xFFE2E8F0))

            Text(
                text = "Live location",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 0.4.sp,
            )

            MapPreviewCard(profileName = profile.name, pulseScale = trackPulse)

            Text(
                text = "Location updates in near real time while SOS is active.",
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 2.dp),
            )

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = NavyDeep.copy(alpha = 0.85f),
                ),
            ) {
                Icon(Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Dismiss alert", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun LiveTrackingStrip(pulseScale: Float) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFEFF6FF),
        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size((14 * pulseScale).dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB).copy(alpha = 0.25f)),
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB)),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Live tracking",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep,
                )
                Text(
                    text = "GPS lock · High accuracy · Updating",
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.85f),
            ) {
                Text(
                    text = "LIVE",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB),
                )
            }
        }
    }
}

@Composable
private fun MapPreviewCard(
    profileName: String,
    pulseScale: Float,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E3A5F),
                        Color(0xFF2D4A6F),
                        Color(0xFF3D5A80),
                    ),
                ),
            ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            repeat(5) { i ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .offset(y = (28 + i * 26).dp)
                        .background(Color.White.copy(alpha = 0.07f)),
                )
            }
            repeat(4) { j ->
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxSize()
                        .offset(x = (40 + j * 56).dp)
                        .background(Color.White.copy(alpha = 0.05f)),
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.GpsFixed,
                contentDescription = null,
                tint = Color(0xFF93C5FD),
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "Map preview",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.85f),
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size((52 * pulseScale).dp)
                        .clip(CircleShape)
                        .background(EmergencyRed.copy(alpha = 0.22f)),
                )
                Box(
                    modifier = Modifier
                        .size((32 * pulseScale).dp)
                        .clip(CircleShape)
                        .background(EmergencyRed.copy(alpha = 0.35f)),
                )
                Surface(
                    shape = CircleShape,
                    color = EmergencyRed,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(46.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 4.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = EmergencyRed,
                    modifier = Modifier.size(18.dp),
                )
                Column {
                    Text(
                        text = profileName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep,
                    )
                    Text(
                        text = "Mira Road · Mumbai Suburban",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                    )
                }
            }
        }
    }
}

@Composable
private fun GradientCtaButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(gradient)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun IncidentProtocolCard(profileName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.History,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(22.dp),
                    )
                }
                Column {
                    Text(
                        text = "Incident protocol",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep,
                    )
                    Text(
                        text = "Follow these steps while responders coordinate.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                    )
                }
            }

            ProtocolStepRow(
                stepLabel = "Step 1",
                icon = Icons.Outlined.Call,
                iconBg = Color(0xFFEFF6FF),
                iconTint = Color(0xFF2563EB),
                title = "Call",
                body = "Reach $profileName immediately and confirm they’re responsive.",
            )
            ProtocolStepRow(
                stepLabel = "Step 2",
                icon = Icons.Outlined.LocationOn,
                iconBg = Color(0xFFFFF7ED),
                iconTint = Color(0xFFEA580C),
                title = "Check location",
                body = "Open live tracking or maps to verify where they are relative to home or care stops.",
            )
            ProtocolStepRow(
                stepLabel = "Step 3",
                icon = Icons.Outlined.Sms,
                iconBg = Color(0xFFFFF1F2),
                iconTint = EmergencyRed,
                title = "Notify emergency contact",
                body = "Loop in your designated responder so someone trusted can assist or attend on-site.",
            )
        }
    }
}

@Composable
private fun WellnessDashboardMetrics() {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Wellness summary",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyDeep,
                )
                Text(
                    text = "Safe · Active · Medication · Last checked",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                WellnessMetricTile(
                    label = "Safe",
                    value = "Stable vitals",
                    accent = Color(0xFF10B981),
                    modifier = Modifier.weight(1f),
                )
                WellnessMetricTile(
                    label = "Active",
                    value = "Mobile today",
                    accent = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                WellnessMetricTile(
                    label = "Medication",
                    value = "2 pending",
                    accent = Color(0xFFF97316),
                    modifier = Modifier.weight(1f),
                )
                WellnessMetricTile(
                    label = "Last checked",
                    value = "Just now",
                    accent = NavyDeep.copy(alpha = 0.65f),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun WellnessMetricTile(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = accent,
                letterSpacing = 0.8.sp,
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EmergencyQuickActionsRow(profileName: String) {
    val ctx = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Respond now",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = NavyDeep.copy(alpha = 0.65f),
            letterSpacing = 0.5.sp,
        )
        GradientCtaButton(
            text = "Call Elder",
            icon = Icons.Outlined.Call,
            gradient = CallGradient,
            onClick = {
                runCatching {
                    ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:")))
                }
            },
        )
        GradientCtaButton(
            text = "View Location",
            icon = Icons.Outlined.Map,
            gradient = MapGradient,
            onClick = {
                val query = Uri.encode("$profileName — care location")
                val geoUri = Uri.parse("geo:0,0?q=$query")
                runCatching {
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
                }.onFailure {
                    runCatching {
                        ctx.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps")),
                        )
                    }
                }
            },
        )
        OutlinedButton(
            onClick = {
                val send =
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Care alert — $profileName")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Quick update regarding $profileName:\n\n",
                        )
                    }
                ctx.startActivity(Intent.createChooser(send, "Notify contact"))
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(2.dp, EmergencyRed.copy(alpha = 0.85f)),
            colors =
                androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = EmergencyRed,
                ),
        ) {
            Icon(Icons.Outlined.Sms, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text("Notify Contact", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun ProtocolStepRow(
    stepLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    body: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(58.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NavyDeep.copy(alpha = 0.08f),
            ) {
                Text(
                    text = stepLabel,
                    modifier =
                        Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp,
                        ),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep,
            )
            Text(
                text = body,
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun AllClearCard(profileName: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color(0xFFDCFCE7), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = CareGreen,
                    modifier = Modifier.size(40.dp),
                )
            }
            Text(
                text = "All clear",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep,
            )
            Text(
                text = "No active SOS alerts for $profileName.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun WellnessSummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Wellness summary",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NavyDeep,
            )
            WellnessStat("Last seen", "At home · 15 mins ago", Icons.Outlined.Home, Color(0xFF10B981))
            WellnessStat("Medicine adherence", "Good · 4/5 today", Icons.Outlined.Medication, Color(0xFF3B82F6))
            WellnessStat("Activity today", "Morning walk done", Icons.Outlined.DirectionsWalk, Color(0xFFF97316))
            WellnessStat("Vitals check", "9:00 am · Normal", Icons.Outlined.MonitorHeart, CareGreen)
        }
    }
}

@Composable
private fun WellnessStat(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, color = Color(0xFF64748B))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = NavyDeep)
        }
    }
}
