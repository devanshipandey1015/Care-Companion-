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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.carecompanion.app.ui.theme.CareDim
import com.carecompanion.app.ui.theme.CareGradients
import com.carecompanion.app.ui.theme.CarePalette
import com.carecompanion.app.ui.theme.CareShapes
import com.carecompanion.app.ui.theme.CareSpacing
import kotlinx.coroutines.delay

private data class DialOption(val region: String, val code: String) {
    val menuLabel: String get() = "$region  $code"
}

private val dialOptions =
    listOf(
        DialOption("United States", "+1"),
        DialOption("India", "+91"),
        DialOption("United Kingdom", "+44"),
        DialOption("Australia", "+61"),
        DialOption("Canada", "+1"),
    )

private data class UserRole(val label: String, val icon: ImageVector, val contentDescription: String)

private val roles =
    listOf(
        UserRole("Elder User", Icons.Outlined.Elderly, "Elder user account"),
        UserRole("Guardian User", Icons.Outlined.HealthAndSafety, "Guardian user account"),
    )

/**
 * Premium healthcare login — OTP flow, validation, and [onLoginClicked] contract unchanged.
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

    PremiumScreenBackground {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = CareSpacing.xxl),
        ) {
            LoginHeroHeader()

            AnimatedVisibility(
                visible = cardVisible,
                enter = fadeIn(tween(420)) + slideInVertically(tween(420), initialOffsetY = { it / 6 }),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = CareSpacing.gutterScreen)
                        .offset(y = (-32).dp),
            ) {
                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(CareSpacing.xl),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(CareSpacing.sm)) {
                            Text(
                                text = "Welcome back",
                                style =
                                    MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 26.sp,
                                        letterSpacing = (-0.35).sp,
                                        lineHeight = 30.sp,
                                        color = CarePalette.Navy,
                                    ),
                            )
                            Text(
                                text = "Sign in with your mobile number",
                                style =
                                    MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp,
                                        lineHeight = 22.sp,
                                        color = CarePalette.TextMuted,
                                    ),
                            )
                        }

                        Text(
                            text = "Choose your role",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = CarePalette.Navy,
                                    fontSize = 17.sp,
                                ),
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
                        ) {
                            roles.forEach { role ->
                                RoleSegmentCard(
                                    userRole = role,
                                    selected = selectedRole == role,
                                    modifier =
                                        Modifier
                                            .weight(1f)
                                            .heightIn(min = 118.dp),
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
                            Column(verticalArrangement = Arrangement.spacedBy(CareSpacing.lg)) {
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
                                    SecureOtpTrustLine()
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                    ) {
                                        Text(
                                            text = "Resend OTP",
                                            style =
                                                MaterialTheme.typography.bodyMedium.copy(
                                                    color = CarePalette.PrimaryBlue,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 15.sp,
                                                ),
                                            modifier =
                                                Modifier
                                                    .clip(RoundedCornerShape(CareSpacing.sm))
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null,
                                                        role = Role.Button,
                                                    ) {
                                                        otp = ""
                                                    }
                                                    .padding(horizontal = CareSpacing.sm, vertical = CareSpacing.sm)
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
                                    SecureOtpTrustLine()
                                }
                            }
                        }

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = CareSpacing.sm)
                                    .padding(horizontal = CareSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = CarePalette.TextMuted,
                                modifier = Modifier.size(15.dp),
                            )
                            Spacer(modifier = Modifier.width(CareSpacing.sm))
                            Text(
                                text = "256-bit encrypted · We never sell your data",
                                modifier = Modifier.weight(1f),
                                style =
                                    MaterialTheme.typography.bodySmall.copy(
                                        color = CarePalette.TextMuted,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
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

            Spacer(modifier = Modifier.height(CareSpacing.xxl))
            LoginFooter()
        }
    }
}

@Composable
private fun LoginHeroHeader() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(248.dp)
                .clip(CareShapes.headerBottom)
                .background(CareGradients.heroNavyBlueMint()),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(color = CarePalette.PrimaryBlue.copy(alpha = 0.18f), radius = w * 0.42f, center = Offset(w * 0.82f, h * 0.18f))
            drawCircle(color = CarePalette.Mint.copy(alpha = 0.16f), radius = w * 0.36f, center = Offset(w * 0.1f, h * 0.78f))
            drawCircle(color = Color.White.copy(alpha = 0.07f), radius = w * 0.22f, center = Offset(w * 0.52f, h * 0.52f))
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = CareSpacing.xxl,
                        end = CareSpacing.xxl,
                        top = CareSpacing.xxl + CareSpacing.sm,
                        bottom = CareSpacing.xxl,
                    ),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(CareSpacing.md + CareSpacing.xs),
            ) {
                Surface(
                    shape = RoundedCornerShape(CareSpacing.lg),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(CareSpacing.lg), spotColor = Color.Black.copy(alpha = 0.12f)),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(CareSpacing.md + CareSpacing.xs).size(30.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(CareSpacing.sm)) {
                    Text(
                        text = "Care Companion",
                        style =
                            MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                letterSpacing = (-0.4).sp,
                                lineHeight = 32.sp,
                            ),
                    )
                    Text(
                        text = "Smart safety and wellness for your loved ones",
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                            ),
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm)) {
                Box(
                    modifier =
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CarePalette.Mint.copy(alpha = 0.95f)),
                )
                Text(
                    text = "Trusted by families · Clear, calm, and accessible",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.86f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                )
            }
        }
    }
}

@Composable
private fun SecureOtpTrustLine(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            tint = CarePalette.PrimaryBlue,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(CareSpacing.sm))
        Text(
            text = "Secure OTP based login",
            style =
                MaterialTheme.typography.titleSmall.copy(
                    color = CarePalette.Navy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RoleSegmentCard(
    userRole: UserRole,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(CareSpacing.lg + CareSpacing.sm)
    val borderBrush =
        if (selected) {
            Brush.linearGradient(listOf(CarePalette.PrimaryBlue, CarePalette.Mint))
        } else {
            Brush.linearGradient(listOf(CarePalette.OutlineSoft, CarePalette.OutlineSoft))
        }
    val elevation = if (selected) CareSpacing.sm + CareSpacing.sm else 4.dp

    Surface(
        modifier =
            modifier
                .shadow(
                    elevation = elevation,
                    shape = shape,
                    ambientColor = if (selected) CarePalette.Mint.copy(alpha = 0.22f) else CarePalette.Navy.copy(alpha = 0.06f),
                    spotColor = if (selected) CarePalette.PrimaryBlue.copy(alpha = 0.32f) else CarePalette.Navy.copy(alpha = 0.06f),
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
        color = if (selected) CarePalette.PrimaryBlue.copy(alpha = 0.08f) else CarePalette.PageBgLight,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CareSpacing.md, vertical = CareSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(CareSpacing.md),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) {
                                Brush.linearGradient(
                                    listOf(
                                        CarePalette.PrimaryBlue.copy(alpha = 0.35f),
                                        CarePalette.Mint.copy(alpha = 0.28f),
                                    ),
                                )
                            } else {
                                Brush.linearGradient(listOf(CarePalette.CardWhite, CarePalette.CardWhite))
                            },
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = userRole.icon,
                    contentDescription = null,
                    tint = if (selected) CarePalette.Navy else CarePalette.TextMuted,
                    modifier = Modifier.size(30.dp),
                )
            }
            Text(
                text = userRole.label,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        color = if (selected) CarePalette.Navy else CarePalette.TextMuted,
                    ),
                textAlign = TextAlign.Center,
            )
            if (selected) {
                Box(
                    modifier =
                        Modifier
                            .height(5.dp)
                            .width(40.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Brush.horizontalGradient(listOf(CarePalette.PrimaryBlue, CarePalette.Mint))),
                )
            } else {
                Spacer(modifier = Modifier.height(5.dp))
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
    val fieldMin = CareDim.textFieldMinHeight + 12.dp
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(112.dp)) {
            OutlinedTextField(
                value = selectedDial.code,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        "Country code",
                        style = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { onCountryMenuExpandedChange(!countryMenuExpanded) },
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Open country code menu",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = fieldMin),
                shape = RoundedCornerShape(CareSpacing.md + CareSpacing.xs),
                colors = outlinedFieldColors(),
                textStyle =
                    LocalTextStyle.current.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CarePalette.Navy,
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
                    "Mobile number",
                    style = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            placeholder = {
                Text(
                    "Enter mobile number",
                    color = CarePalette.TextMuted,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = CarePalette.PrimaryBlue,
                    modifier = Modifier.size(22.dp),
                )
            },
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .heightIn(min = fieldMin),
            shape = RoundedCornerShape(CareSpacing.md + CareSpacing.xs),
            colors = outlinedFieldColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            textStyle =
                LocalTextStyle.current.copy(
                    fontSize = 19.sp,
                    color = CarePalette.Navy,
                    fontWeight = FontWeight.Medium,
                ),
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
        label = { Text("One-time password", fontSize = 14.sp) },
        placeholder = { Text("Enter 4–6 digit code", color = CarePalette.TextMuted, fontSize = 16.sp) },
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = CareDim.textFieldMinHeight),
        shape = RoundedCornerShape(CareSpacing.md + CareSpacing.xs),
        colors = outlinedFieldColors(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        textStyle =
            LocalTextStyle.current.copy(
                fontSize = 24.sp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.SemiBold,
                color = CarePalette.Navy,
            ),
        singleLine = true,
    )
}

@Composable
private fun outlinedFieldColors() =
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

    val gradient = CareGradients.primaryCta(enabled)

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = CareDim.buttonMinHeight)
                .scale(scale)
                .shadow(
                    elevation = if (enabled) CareSpacing.sm + CareSpacing.sm else CareSpacing.sm,
                    shape = CareShapes.button,
                    spotColor = if (enabled) CarePalette.PrimaryBlue.copy(alpha = 0.4f) else CarePalette.PrimaryBlue.copy(alpha = 0.1f),
                    ambientColor = CarePalette.Navy.copy(alpha = 0.08f),
                )
                .clip(CareShapes.button)
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
                )
                .padding(horizontal = CareSpacing.xl, vertical = CareSpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                MaterialTheme.typography.titleMedium.copy(
                    color = Color.White.copy(alpha = if (enabled) 1f else 0.88f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
        )
    }
}

@Composable
private fun LoginFooter() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = CareSpacing.gutterScreen),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(CareSpacing.sm),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm)) {
            Box(
                modifier =
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(CarePalette.Mint),
            )
            Text(
                text = "Care Companion",
                style =
                    MaterialTheme.typography.labelLarge.copy(
                        color = CarePalette.TextMuted,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                    ),
            )
            Text(
                text = "·",
                color = CarePalette.TextMuted,
                fontSize = 13.sp,
            )
            Text(
                text = "Health & safety",
                style =
                    MaterialTheme.typography.labelLarge.copy(
                        color = CarePalette.TextMuted,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                    ),
            )
        }
        Text(
            text = "Premium support for aging in place — v1.0",
            style = MaterialTheme.typography.bodySmall.copy(color = CarePalette.TextMuted.copy(alpha = 0.88f), fontSize = 12.sp),
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
