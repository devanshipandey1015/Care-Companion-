package com.carecompanion.app

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareGreen
import com.carecompanion.app.ui.theme.CareGradients
import com.carecompanion.app.ui.theme.CarePalette
import com.carecompanion.app.ui.theme.CareShapes
import com.carecompanion.app.ui.theme.CareSpacing

data class GuardianProfile(
    val name: String,
    val icon: ImageVector,
    val bg: Color,
    val photoUri: Uri? = null,
)

private val CardWhite = CarePalette.CardWhite
private val TextPrimary = CarePalette.Navy
private val TextSecondary = CarePalette.TextMuted

@Composable
fun GuardianHomeScreen(
    profiles: List<GuardianProfile>,
    onAddProfile: () -> Unit = {},
    onManageProfile: (GuardianProfile) -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val ctx = LocalContext.current
    var selectedProfile by remember { mutableStateOf<GuardianProfile?>(null) }

    LaunchedEffect(profiles.size) {
        if (profiles.isEmpty()) {
            selectedProfile = null
        } else {
            val sel = selectedProfile
            if (sel == null || profiles.none { it.name == sel.name }) {
                selectedProfile = profiles.firstOrNull()
            }
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CareGradients.pageSoftWash()),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 100.dp),
            ) {
                GuardianDashboardHeader(
                    onLogout = onLogout,
                    onNotifications = {
                        Toast.makeText(ctx, "No new alerts right now.", Toast.LENGTH_SHORT).show()
                    },
                )

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = CareSpacing.gutterScreen)
                            .padding(top = CareSpacing.sm),
                ) {
                    if (selectedProfile != null) {
                        ElderMonitoringStatusCard(profile = selectedProfile!!)
                        Spacer(modifier = Modifier.height(CareSpacing.xl))
                    }

                    Text(
                        text = "Profiles",
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary,
                            ),
                    )
                    Spacer(modifier = Modifier.height(CareSpacing.sm))

                    Text(
                        text = "Choose who you’re monitoring — medicines, contacts, and SOS in one place.",
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = TextSecondary,
                            ),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(CareSpacing.xl))

                    if (profiles.isEmpty()) {
                        AddProfileInvitationCard(
                            variant = AddCardVariant.EmptyState,
                            onClick = onAddProfile,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            items(items = profiles, key = { it.name }) { profile ->
                                ProfileChoiceCard(
                                    profile = profile,
                                    selected = selectedProfile?.name == profile.name,
                                    onClick = { selectedProfile = profile },
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(CareSpacing.lg))

                        AddProfileInvitationCard(
                            variant = AddCardVariant.Inline,
                            onClick = onAddProfile,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(CareSpacing.xl))

                        GradientManageButton(
                            text =
                                if (selectedProfile == null) {
                                    "Manage Profile"
                                } else {
                                    "Manage ${selectedProfile!!.name}"
                                },
                            enabled = selectedProfile != null,
                            onClick = { selectedProfile?.let(onManageProfile) },
                            semanticsLabel = "Open management for selected profile",
                        )

                        Spacer(modifier = Modifier.height(CareSpacing.xxl + CareSpacing.sm))

                        Text(
                            text = "Care overview",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TextPrimary,
                                ),
                        )
                        Spacer(modifier = Modifier.height(CareSpacing.sm))
                        Text(
                            text = "Quick access to wellness, medicines, contacts, and alerts.",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                ),
                        )
                        Spacer(modifier = Modifier.height(CareSpacing.lg))

                        val canOpen = selectedProfile != null
                        val openManage: () -> Unit = { selectedProfile?.let(onManageProfile) ?: Unit }

                        Column(verticalArrangement = Arrangement.spacedBy(CareSpacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
                            ) {
                                DashboardQuickTile(
                                    title = "Wellness",
                                    subtitle = "Status & routines",
                                    icon = Icons.Outlined.Favorite,
                                    tint = CareGreen,
                                    iconBg = CareGreen.copy(alpha = 0.14f),
                                    onClick = openManage,
                                    enabled = canOpen,
                                    modifier = Modifier.weight(1f),
                                )
                                DashboardQuickTile(
                                    title = "Medicines",
                                    subtitle = "Schedules & doses",
                                    icon = Icons.Outlined.MedicalServices,
                                    tint = CarePalette.PrimaryBlue,
                                    iconBg = CarePalette.PrimaryBlue.copy(alpha = 0.12f),
                                    onClick = openManage,
                                    enabled = canOpen,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
                            ) {
                                DashboardQuickTile(
                                    title = "Contacts",
                                    subtitle = "Trusted circle",
                                    icon = Icons.Outlined.SupportAgent,
                                    tint = Color(0xFF1565C0),
                                    iconBg = Color(0xFFE3F2FD),
                                    onClick = openManage,
                                    enabled = canOpen,
                                    modifier = Modifier.weight(1f),
                                )
                                DashboardQuickTile(
                                    title = "SOS",
                                    subtitle = "Emergency tools",
                                    icon = Icons.Outlined.Warning,
                                    tint = CarePalette.Emergency,
                                    iconBg = CarePalette.Emergency.copy(alpha = 0.12f),
                                    onClick = openManage,
                                    enabled = canOpen,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(CareSpacing.xxl))

                        Text(
                            text = "Recent alerts",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TextPrimary,
                                ),
                        )
                        Spacer(modifier = Modifier.height(CareSpacing.md))

                        RecentAlertsSection()
                    }
                }
            }

            GuardianBottomBar(
                activeTab = BottomTab.Home,
                onHome = {},
                onAlerts = {
                    Toast.makeText(ctx, "Alerts: no new items.", Toast.LENGTH_SHORT).show()
                },
                onSettings = {
                    Toast.makeText(ctx, "Settings open from profile management.", Toast.LENGTH_SHORT).show()
                },
            )
        }
    }
}

@Composable
private fun GuardianDashboardHeader(
    onLogout: () -> Unit,
    onNotifications: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(CareShapes.headerBottom)
                .background(CareGradients.heroNavyBlueMint()),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(color = CarePalette.Mint.copy(alpha = 0.14f), radius = w * 0.36f, center = Offset(w * 0.88f, h * 0.15f))
            drawCircle(color = Color.White.copy(alpha = 0.06f), radius = w * 0.26f, center = Offset(w * 0.1f, h * 0.75f))
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CareSpacing.gutterScreen, vertical = CareSpacing.lg),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LogoutPill(onClick = onLogout)
                IconButton(
                    onClick = onNotifications,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(CareSpacing.md))
            Text(
                text = "Guardian Dashboard",
                style =
                    MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        letterSpacing = (-0.35).sp,
                    ),
            )
            Spacer(modifier = Modifier.height(CareSpacing.sm))
            Text(
                text = "Live monitoring · All clear right now",
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                    ),
            )
        }
    }
}

@Composable
private fun ElderMonitoringStatusCard(profile: GuardianProfile) {
    val shape = CareShapes.card
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 14.dp,
                    shape = shape,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.08f),
                    spotColor = CarePalette.PrimaryBlue.copy(alpha = 0.18f),
                )
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        colors =
                            listOf(
                                CarePalette.Mint.copy(alpha = 0.45f),
                                CarePalette.PrimaryBlue.copy(alpha = 0.35f),
                                CardWhite.copy(alpha = 0.97f),
                            ),
                    ),
                )
                .border(1.dp, Color.White.copy(alpha = 0.75f), shape)
                .padding(CareSpacing.xl),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CareSpacing.lg),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(88.dp)
                        .shadow(8.dp, CircleShape, spotColor = CarePalette.Navy.copy(alpha = 0.15f))
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors =
                                    listOf(
                                        Color.White.copy(alpha = 0.5f),
                                        profile.bg.copy(alpha = 0.95f),
                                        profile.bg.copy(alpha = 0.65f),
                                    ),
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
                        imageVector = profile.icon,
                        contentDescription = null,
                        tint = TextPrimary.copy(alpha = 0.88f),
                        modifier = Modifier.size(44.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(6.dp))
                StatusChip(text = "SAFE", tone = StatusChipTone.Positive)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Last updated · Just now",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun DashboardQuickTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    iconBg: Color,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = CareShapes.card
    Column(
        modifier =
            modifier
                .height(120.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = shape,
                    ambientColor = CarePalette.Navy.copy(alpha = 0.06f),
                    spotColor = tint.copy(alpha = 0.12f),
                )
                .clip(shape)
                .background(CardWhite)
                .border(1.dp, CarePalette.OutlineSoft.copy(alpha = 0.85f), shape)
                .clickable(enabled = enabled, onClick = onClick)
                .alpha(if (enabled) 1f else 0.45f)
                .padding(CareSpacing.lg),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp,
            )
        }
    }
}

@Composable
private fun RecentAlertsSection() {
    val empty = remember { true }
    if (empty) {
        EmptyStateCard(
            title = "You’re all caught up",
            message = "No recent alerts for this profile. We’ll surface medicine reminders, SOS, and check-ins here.",
            icon = Icons.Outlined.CalendarMonth,
            modifier = Modifier.fillMaxWidth(),
        )
    } else {
        // Timeline reserved for future alert list
    }
}

@Composable
private fun LogoutPill(onClick: () -> Unit) {
    val logoutLabel = stringResource(R.string.common_logout)
    Surface(
        modifier =
            Modifier
                .clip(RoundedCornerShape(999.dp))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)), RoundedCornerShape(999.dp))
                .clickable(onClick = onClick)
                .semantics {
                    role = Role.Button
                    contentDescription = logoutLabel
                },
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.1f),
    ) {
        Text(
            text = logoutLabel,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style =
                MaterialTheme.typography.labelLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                ),
        )
    }
}

private enum class AddCardVariant { EmptyState, Inline }

@Composable
private fun AddProfileInvitationCard(
    variant: AddCardVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 520f),
        label = "addCardScale",
    )

    val verticalPad = if (variant == AddCardVariant.EmptyState) 36.dp else 22.dp
    val dashColor = CarePalette.Mint.copy(alpha = 0.65f)

    Column(
        modifier =
            modifier
                .scale(scale)
                .shadow(
                    elevation = 10.dp,
                    shape = CareShapes.card,
                    ambientColor = CarePalette.Mint.copy(alpha = 0.12f),
                    spotColor = CarePalette.Mint.copy(alpha = 0.18f),
                )
                .clip(CareShapes.card)
                .background(CardWhite)
                .dashedRoundedBorderOverlay(color = dashColor, cornerRadius = 24.dp, strokeWidth = 2.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 22.dp, vertical = verticalPad)
                .semantics {
                    role = Role.Button
                    contentDescription = "Add profile"
                },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(72.dp)
                    .shadow(8.dp, CircleShape, spotColor = CarePalette.Mint.copy(alpha = 0.35f))
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(CarePalette.Mint.copy(alpha = 0.9f), CareGreen.copy(alpha = 0.85f)),
                        ),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp),
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Add a loved one",
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = TextPrimary,
                ),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text =
                if (variant == AddCardVariant.EmptyState) {
                    "Create your first profile to start coordinating care together."
                } else {
                    "Tap to add another person you support."
                },
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ProfileChoiceCard(
    profile: GuardianProfile,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 560f),
        label = "profileCardScale",
    )

    val shape = RoundedCornerShape(24.dp)
    val borderBrush =
        if (selected) {
            Brush.linearGradient(listOf(CarePalette.PrimaryBlue, CarePalette.Mint))
        } else {
            Brush.linearGradient(listOf(CarePalette.OutlineSoft, CarePalette.OutlineSoft))
        }

    Column(
        modifier =
            Modifier
                .width(156.dp)
                .scale(scale)
                .shadow(
                    elevation = if (selected) 16.dp else 8.dp,
                    shape = shape,
                    ambientColor = if (selected) CarePalette.Mint.copy(alpha = 0.22f) else CarePalette.Navy.copy(alpha = 0.06f),
                    spotColor = if (selected) CarePalette.PrimaryBlue.copy(alpha = 0.28f) else CarePalette.Navy.copy(alpha = 0.08f),
                )
                .clip(shape)
                .border(width = if (selected) 2.dp else 1.dp, brush = borderBrush, shape = shape)
                .background(if (selected) CarePalette.PrimaryBlue.copy(alpha = 0.07f) else CardWhite)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 14.dp, vertical = 18.dp)
                .semantics {
                    role = Role.RadioButton
                    contentDescription = profile.name
                },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(92.dp)
                    .shadow(6.dp, CircleShape, spotColor = profile.bg.copy(alpha = if (selected) 0.55f else 0.35f))
                    .clip(CircleShape)
                    .border(
                        width = if (selected) 2.5.dp else 1.dp,
                        brush =
                            if (selected) {
                                Brush.linearGradient(
                                    colors =
                                        listOf(
                                            Color.White.copy(alpha = 0.95f),
                                            CarePalette.PrimaryBlue.copy(alpha = 0.88f),
                                            CarePalette.Mint.copy(alpha = 0.75f),
                                        ),
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.25f)),
                                )
                            },
                        shape = CircleShape,
                    )
                    .background(
                        brush =
                            Brush.radialGradient(
                                colors =
                                    listOf(
                                        Color.White.copy(alpha = 0.48f),
                                        profile.bg.copy(alpha = 0.94f),
                                        profile.bg.copy(alpha = 0.62f),
                                    ),
                            ),
                        shape = CircleShape,
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
                    imageVector = profile.icon,
                    contentDescription = null,
                    tint = TextPrimary.copy(alpha = 0.85f),
                    modifier = Modifier.size(42.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = profile.name,
            style =
                MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier =
                Modifier
                    .height(5.dp)
                    .width(if (selected) 44.dp else 28.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (selected) {
                            Brush.horizontalGradient(listOf(CarePalette.PrimaryBlue, CarePalette.Mint))
                        } else {
                            Brush.horizontalGradient(
                                listOf(TextSecondary.copy(alpha = 0.35f), TextSecondary.copy(alpha = 0.35f)),
                            )
                        },
                    ),
        )
    }
}

@Composable
private fun GradientManageButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    semanticsLabel: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 520f),
        label = "manageScale",
    )

    val gradient =
        when {
            !enabled ->
                Brush.horizontalGradient(
                    listOf(CarePalette.PrimaryBlue.copy(alpha = 0.35f), CarePalette.Mint.copy(alpha = 0.3f)),
                )
            else -> Brush.horizontalGradient(listOf(CarePalette.PrimaryBlue, CarePalette.Mint))
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .scale(scale)
                .shadow(
                    elevation = if (enabled) 14.dp else 6.dp,
                    shape = CareShapes.button,
                    spotColor = if (enabled) CarePalette.PrimaryBlue.copy(alpha = 0.4f) else CarePalette.PrimaryBlue.copy(alpha = 0.12f),
                    ambientColor = CarePalette.Navy.copy(alpha = 0.08f),
                )
                .clip(CareShapes.button)
                .background(brush = gradient)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick,
                )
                .semantics {
                    role = Role.Button
                    contentDescription = semanticsLabel
                },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 16.dp),
            style =
                MaterialTheme.typography.titleMedium.copy(
                    color = Color.White.copy(alpha = if (enabled) 1f else 0.9f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                ),
        )
    }
}

private fun Modifier.dashedRoundedBorderOverlay(
    color: Color,
    cornerRadius: Dp,
    strokeWidth: Dp,
): Modifier =
    drawWithContent {
        drawContent()
        val stroke = strokeWidth.toPx()
        val r = cornerRadius.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(size.width - stroke, size.height - stroke),
            cornerRadius = CornerRadius(r, r),
            style =
                Stroke(
                    width = stroke,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f),
                ),
        )
    }
