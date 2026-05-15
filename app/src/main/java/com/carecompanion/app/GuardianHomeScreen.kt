package com.carecompanion.app

import android.net.Uri
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
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Icon
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

data class GuardianProfile(
    val name: String,
    val icon: ImageVector,
    val bg: Color,
    val photoUri: Uri? = null,
)

// Aligned with premium Care Companion palette
private val DeepNavy = Color(0xFF14213D)
private val SoftBlue = Color(0xFF4EA8DE)
private val MintGreen = Color(0xFF7BD389)
private val SoftBackground = Color(0xFFF5F7FB)
private val CardWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF14213D)
private val TextSecondary = Color(0xFF5C6578)
private val TextMuted = Color(0xFF8E96A8)

@Composable
fun GuardianHomeScreen(
    profiles: List<GuardianProfile>,
    onAddProfile: () -> Unit = {},
    onManageProfile: (GuardianProfile) -> Unit = {},
    onLogout: () -> Unit = {},
) {
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
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState()),
        ) {
            SelectProfileHero(
                onLogout = onLogout,
                modifier = Modifier.fillMaxWidth(),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(top = 8.dp, bottom = 28.dp),
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Manage your loved ones",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        letterSpacing = (-0.3).sp,
                        color = TextPrimary,
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Choose a profile to coordinate care, medicines, and safety — all in one calm place.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = TextSecondary,
                    ),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(26.dp))

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

                    Spacer(modifier = Modifier.height(18.dp))

                    AddProfileInvitationCard(
                        variant = AddCardVariant.Inline,
                        onClick = onAddProfile,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    GradientManageButton(
                        text = if (selectedProfile == null) {
                            "Manage Profile"
                        } else {
                            "Manage ${selectedProfile!!.name}"
                        },
                        enabled = selectedProfile != null,
                        onClick = { selectedProfile?.let(onManageProfile) },
                        contentDescription = "Open management for selected profile",
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectProfileHero(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(210.dp)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DeepNavy,
                        Color(0xFF1C3A63),
                        SoftBlue.copy(alpha = 0.9f),
                    ),
                ),
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(color = MintGreen.copy(alpha = 0.12f), radius = w * 0.38f, center = Offset(w * 0.85f, h * 0.2f))
            drawCircle(color = Color.White.copy(alpha = 0.07f), radius = w * 0.28f, center = Offset(w * 0.12f, h * 0.75f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LogoutPill(onClick = onLogout)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.14f),
                    modifier = Modifier.shadow(6.dp, RoundedCornerShape(18.dp), spotColor = Color.Black.copy(alpha = 0.12f)),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(12.dp).size(26.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Care Companion",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                        ),
                    )
                    Text(
                        text = "Hello, caregiver",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Who are you caring for today?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            lineHeight = 22.sp,
                        ),
                    )
                    Text(
                        text = "Select a profile below — everything stays private and secure.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.82f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                        ),
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.VolunteerActivism,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(52.dp).padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun LogoutPill(onClick: () -> Unit) {
    val logoutLabel = stringResource(R.string.common_logout)
    Surface(
        modifier = Modifier
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
            style = MaterialTheme.typography.labelLarge.copy(
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
    val dashColor = MintGreen.copy(alpha = 0.65f)

    Column(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = MintGreen.copy(alpha = 0.12f),
                spotColor = MintGreen.copy(alpha = 0.18f),
            )
            .clip(RoundedCornerShape(26.dp))
            .background(CardWhite)
            .dashedRoundedBorderOverlay(color = dashColor, cornerRadius = 26.dp, strokeWidth = 2.dp)
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
            modifier = Modifier
                .size(72.dp)
                .shadow(8.dp, CircleShape, spotColor = MintGreen.copy(alpha = 0.35f))
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(MintGreen.copy(alpha = 0.9f), CareGreen.copy(alpha = 0.85f)))),
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
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = DeepNavy,
            ),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (variant == AddCardVariant.EmptyState) {
                "Create your first profile to start coordinating care together."
            } else {
                "Tap to add another person you support."
            },
            style = MaterialTheme.typography.bodyMedium.copy(
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

    val shape = RoundedCornerShape(26.dp)
    val borderBrush = if (selected) {
        Brush.linearGradient(listOf(SoftBlue, MintGreen))
    } else {
        Brush.linearGradient(listOf(Color(0xFFE2E8F2), Color(0xFFE2E8F2)))
    }

    Column(
        modifier = Modifier
            .width(156.dp)
            .scale(scale)
            .shadow(
                elevation = if (selected) 16.dp else 8.dp,
                shape = shape,
                ambientColor = if (selected) MintGreen.copy(alpha = 0.22f) else DeepNavy.copy(alpha = 0.06f),
                spotColor = if (selected) SoftBlue.copy(alpha = 0.28f) else DeepNavy.copy(alpha = 0.08f),
            )
            .clip(shape)
            .border(width = if (selected) 2.dp else 1.dp, brush = borderBrush, shape = shape)
            .background(if (selected) SoftBlue.copy(alpha = 0.07f) else CardWhite)
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
            modifier = Modifier
                .size(92.dp)
                .shadow(4.dp, CircleShape, spotColor = profile.bg.copy(alpha = 0.45f))
                .clip(CircleShape)
                .background(profile.bg, CircleShape),
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
                    tint = DeepNavy.copy(alpha = 0.85f),
                    modifier = Modifier.size(42.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = profile.name,
            style = MaterialTheme.typography.titleSmall.copy(
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
            modifier = Modifier
                .height(5.dp)
                .width(if (selected) 44.dp else 28.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    if (selected) Brush.horizontalGradient(listOf(SoftBlue, MintGreen))
                    else Brush.horizontalGradient(listOf(TextMuted.copy(alpha = 0.35f), TextMuted.copy(alpha = 0.35f))),
                ),
        )
    }
}

@Composable
private fun GradientManageButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 520f),
        label = "manageScale",
    )

    val gradient = when {
        !enabled -> Brush.horizontalGradient(listOf(SoftBlue.copy(alpha = 0.35f), MintGreen.copy(alpha = 0.3f)))
        else -> Brush.horizontalGradient(listOf(SoftBlue, MintGreen))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .scale(scale)
            .shadow(
                elevation = if (enabled) 14.dp else 6.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = if (enabled) SoftBlue.copy(alpha = 0.4f) else SoftBlue.copy(alpha = 0.12f),
                ambientColor = DeepNavy.copy(alpha = 0.08f),
            )
            .clip(RoundedCornerShape(18.dp))
            .background(brush = gradient)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.titleMedium.copy(
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
): Modifier = drawWithContent {
    drawContent()
    val stroke = strokeWidth.toPx()
    val r = cornerRadius.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(stroke / 2, stroke / 2),
        size = Size(size.width - stroke, size.height - stroke),
        cornerRadius = CornerRadius(r, r),
        style = Stroke(
            width = stroke,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f),
        ),
    )
}
