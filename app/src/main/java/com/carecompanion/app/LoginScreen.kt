package com.carecompanion.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Elderly
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareCompanionTheme
import kotlinx.coroutines.delay

// Brand palette — Care Companion premium healthcare
private val DeepNavy = Color(0xFF14213D)
private val SoftBlue = Color(0xFF4EA8DE)
private val MintGreen = Color(0xFF7BD389)
private val EmergencyRed = Color(0xFFFF4D4D)
private val SoftBackground = Color(0xFFF5F7FB)
private val CardWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF14213D)
private val TextSecondary = Color(0xFF5C6578)
private val TextMuted = Color(0xFF8E96A8)
private val FieldBorder = Color(0xFFE2E8F2)
private val FieldSurface = Color(0xFFF5F7FB)
private val ChipBg = SoftBlue.copy(alpha = 0.12f)

private data class DialOption(val region: String, val code: String) {
    val menuLabel: String get() = "$region  $code"
}

private val dialOptions = listOf(
    DialOption("United States", "+1"),
    DialOption("India", "+91"),
    DialOption("United Kingdom", "+44"),
    DialOption("Australia", "+61"),
    DialOption("Canada", "+1"),
)

data class UserRole(val label: String, val icon: ImageVector, val contentDescription: String)

private val roles = listOf(
    UserRole("Elder User", Icons.Outlined.Elderly, "Elder user account"),
    UserRole("Guardian User", Icons.Outlined.HealthAndSafety, "Guardian user account"),
)

/**
 * Premium login — OTP flow, roles, and [onLoginClicked] contract are unchanged.
 *
 * **Motion ideas (optional):** drive hero circles with slow `rememberInfiniteTransition`;
 * wrap the login card in `AnimatedVisibility` + `fadeIn`/`slideInVertically` (already used);
 * add haptic feedback on role select. **Loading:** when a real OTP API exists, hoist
 * `isLoading` and show a small `CircularProgressIndicator` over the gradient CTA during the call.
 */
@Composable
fun LoginScreen(onLoginClicked: (role: String, phone: String) -> Unit = { _, _ -> }) {
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var otpRequested by remember { mutableStateOf(false) }
    var selectedDial by remember { mutableStateOf(dialOptions[0]) }
    var countryMenuExpanded by remember { mutableStateOf(false) }
    var cardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(40)
        cardVisible = true
    }

    fun digitsOnly(raw: String): String = raw.filter { it.isDigit() }

    fun fullPhoneNumber(): String {
        val cleanedDial = selectedDial.code.trim().removePrefix("+")
        val national = digitsOnly(phone)
        return "+$cleanedDial$national"
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
                .verticalScroll(rememberScrollState())
                .padding(bottom = 28.dp),
        ) {
            LoginHero(modifier = Modifier.fillMaxWidth())

            AnimatedVisibility(
                visible = cardVisible,
                enter = fadeIn(tween(420)) + slideInVertically(
                    tween(420),
                    initialOffsetY = { it / 5 },
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = DeepNavy.copy(alpha = 0.08f),
                                spotColor = SoftBlue.copy(alpha = 0.18f),
                            ),
                        shape = RoundedCornerShape(28.dp),
                        color = CardWhite,
                        tonalElevation = 0.dp,
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Welcome back",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 26.sp,
                                            letterSpacing = (-0.35).sp,
                                            lineHeight = 30.sp,
                                            color = TextPrimary,
                                        ),
                                    )
                                    Text(
                                        text = "Sign in with your mobile number",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 15.sp,
                                            lineHeight = 22.sp,
                                            color = TextSecondary,
                                        ),
                                    )
                                }
                                LoginTrustBadge()
                                HorizontalDivider(
                                    modifier = Modifier.padding(top = 2.dp),
                                    color = FieldBorder.copy(alpha = 0.85f),
                                    thickness = 1.dp,
                                )
                            }

                            Text(
                                text = "Choose your role",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                ),
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                roles.forEach { role ->
                                    RoleSegmentCard(
                                        userRole = role,
                                        selected = selectedRole == role,
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 104.dp),
                                        onClick = {
                                            selectedRole = role
                                            otpRequested = false
                                            otp = ""
                                            phone = ""
                                        },
                                    )
                                }
                            }

                            AnimatedContent(
                                targetState = otpRequested,
                                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(180)) },
                                label = "loginStep",
                            ) { showOtp ->
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    if (showOtp) {
                                        OtpFloatingField(
                                            value = otp,
                                            onValueChange = { if (it.length <= 6) otp = digitsOnly(it) },
                                        )
                                        GradientCta(
                                            text = "Verify & continue",
                                            enabled = selectedRole != null && otp.length >= 4,
                                            onClick = {
                                                onLoginClicked(selectedRole?.label.orEmpty(), fullPhoneNumber())
                                            },
                                            contentDescription = "Verify one-time password and continue",
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                        ) {
                                            Text(
                                                text = "Resend OTP",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = SoftBlue,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 15.sp,
                                                ),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null,
                                                        role = Role.Button,
                                                    ) {
                                                        otp = ""
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                                                    .semantics { contentDescription = "Resend one-time password" },
                                            )
                                        }
                                    } else {
                                        PhoneFloatingField(
                                            phone = phone,
                                            onPhoneChange = { if (it.length <= 15) phone = digitsOnly(it) },
                                            selectedDial = selectedDial,
                                            countryMenuExpanded = countryMenuExpanded,
                                            onCountryMenuExpandedChange = { countryMenuExpanded = it },
                                            onDialSelected = {
                                                selectedDial = it
                                                countryMenuExpanded = false
                                            },
                                        )
                                        GradientCta(
                                            text = "Get OTP",
                                            enabled = selectedRole != null && digitsOnly(phone).length >= 10,
                                            onClick = { otpRequested = true },
                                            contentDescription = "Send one-time password to phone",
                                        )
                                    }
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lock,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "256-bit encrypted · We never sell your data",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextMuted,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    LoginFooter()
                }
            }
        }
    }
}

@Composable
private fun LoginHero(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(190.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DeepNavy,
                        Color(0xFF1C3A63),
                        SoftBlue.copy(alpha = 0.92f),
                    ),
                ),
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(color = SoftBlue.copy(alpha = 0.18f), radius = w * 0.42f, center = Offset(w * 0.82f, h * 0.18f))
            drawCircle(color = MintGreen.copy(alpha = 0.14f), radius = w * 0.35f, center = Offset(w * 0.08f, h * 0.72f))
            drawCircle(color = Color.White.copy(alpha = 0.06f), radius = w * 0.22f, center = Offset(w * 0.55f, h * 0.55f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 26.dp, end = 26.dp, top = 24.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.14f),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(18.dp), spotColor = Color.Black.copy(alpha = 0.15f)),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(14.dp).size(28.dp),
                    )
                }
                Column {
                    Text(
                        text = "Care Companion",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            letterSpacing = (-0.3).sp,
                        ),
                    )
                    Text(
                        text = "Calm support for elders & families",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.88f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                        ),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Safety & care, within reach",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                        ),
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmergencyRed.copy(alpha = 0.9f)),
                    )
                }
                Text(
                    text = "Designed for clarity, larger touch targets, and peace of mind.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun LoginTrustBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = ChipBg,
        border = BorderStroke(1.dp, SoftBlue.copy(alpha = 0.28f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = SoftBlue,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "OTP-protected sign-in",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = DeepNavy,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    letterSpacing = 0.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun RoleSegmentCard(
    userRole: UserRole,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    val borderBrush = if (selected) {
        Brush.linearGradient(listOf(SoftBlue, MintGreen))
    } else {
        Brush.linearGradient(listOf(FieldBorder, FieldBorder))
    }
    val glowElevation = if (selected) 14.dp else 4.dp

    Surface(
        modifier = modifier
            .shadow(
                elevation = glowElevation,
                shape = shape,
                ambientColor = if (selected) MintGreen.copy(alpha = 0.25f) else DeepNavy.copy(alpha = 0.06f),
                spotColor = if (selected) SoftBlue.copy(alpha = 0.35f) else DeepNavy.copy(alpha = 0.08f),
            )
            .clip(shape)
            .border(width = if (selected) 2.dp else 1.dp, brush = borderBrush, shape = shape)
            .semantics {
                role = Role.RadioButton
                contentDescription = userRole.contentDescription
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        shape = shape,
        color = if (selected) SoftBlue.copy(alpha = 0.08f) else FieldSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) Brush.linearGradient(listOf(SoftBlue.copy(alpha = 0.35f), MintGreen.copy(alpha = 0.28f)))
                        else Brush.linearGradient(listOf(Color.White, Color.White)),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = userRole.icon,
                    contentDescription = null,
                    tint = if (selected) DeepNavy else TextSecondary,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = userRole.label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = if (selected) DeepNavy else TextSecondary,
                ),
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(36.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Brush.horizontalGradient(listOf(SoftBlue, MintGreen))),
                )
            } else {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun PhoneFloatingField(
    phone: String,
    onPhoneChange: (String) -> Unit,
    selectedDial: DialOption,
    countryMenuExpanded: Boolean,
    onCountryMenuExpandedChange: (Boolean) -> Unit,
    onDialSelected: (DialOption) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(modifier = Modifier.width(92.dp)) {
            OutlinedTextField(
                value = selectedDial.code,
                onValueChange = {},
                readOnly = true,
                label = { Text("Code", style = LocalTextStyle.current.copy(fontSize = 11.sp)) },
                trailingIcon = {
                    IconButton(
                        onClick = { onCountryMenuExpandedChange(!countryMenuExpanded) },
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Open country code menu",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = outlinedFieldColors(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                ),
                singleLine = true,
            )
            DropdownMenu(
                expanded = countryMenuExpanded,
                onDismissRequest = { onCountryMenuExpandedChange(false) },
            ) {
                dialOptions.distinctBy { it.code to it.region }.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.menuLabel) },
                        onClick = {
                            onDialSelected(option)
                            onCountryMenuExpandedChange(false)
                        },
                    )
                }
            }
        }

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = {
                Text(
                    "Mobile",
                    style = LocalTextStyle.current.copy(fontSize = 13.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            placeholder = { Text("Enter your number", color = TextMuted) },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = outlinedFieldColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, color = TextPrimary, fontWeight = FontWeight.Medium),
            singleLine = true,
        )
    }
}

@Composable
private fun OtpFloatingField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("One-time password") },
        placeholder = { Text("Enter 4–6 digit code", color = TextMuted) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = outlinedFieldColors(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 22.sp,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        ),
        singleLine = true,
    )
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SoftBlue,
    unfocusedBorderColor = FieldBorder,
    cursorColor = SoftBlue,
    focusedLabelColor = SoftBlue,
    unfocusedLabelColor = TextMuted,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedContainerColor = FieldSurface,
    unfocusedContainerColor = FieldSurface,
)

@Composable
private fun GradientCta(
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
        label = "ctaScale",
    )

    val gradient = when {
        !enabled -> Brush.horizontalGradient(listOf(SoftBlue.copy(alpha = 0.38f), MintGreen.copy(alpha = 0.32f)))
        else -> Brush.horizontalGradient(listOf(SoftBlue, MintGreen))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .shadow(
                elevation = if (enabled) 12.dp else 6.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = if (enabled) SoftBlue.copy(alpha = 0.45f) else SoftBlue.copy(alpha = 0.12f),
                ambientColor = DeepNavy.copy(alpha = 0.08f),
            )
            .clip(RoundedCornerShape(18.dp))
            .background(brush = gradient)
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White.copy(alpha = if (enabled) 1f else 0.92f),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
            ),
        )
    }
}

@Composable
private fun LoginFooter() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MintGreen),
            )
            Text(
                text = "Care Companion",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                ),
            )
            Text(
                text = "·",
                color = TextMuted,
                fontSize = 13.sp,
            )
            Text(
                text = "Health & safety",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                ),
            )
        }
        Text(
            text = "Premium support for aging in place — v1.0",
            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted.copy(alpha = 0.85f), fontSize = 11.sp),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    CareCompanionTheme {
        LoginScreen()
    }
}
