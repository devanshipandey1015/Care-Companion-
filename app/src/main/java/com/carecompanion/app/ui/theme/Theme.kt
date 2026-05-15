package com.carecompanion.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme =
    lightColorScheme(
        primary = CareGreen,
        onPrimary = Color.White,
        primaryContainer = CareGreen.copy(alpha = 0.12f),
        secondary = CareGreenDark,
        onSecondary = Color.White,
        tertiary = CarePalette.SoftBlue,
        background = CarePalette.PageBgLight,
        surface = Color.White,
        surfaceVariant = Color(0xFFE8ECF4),
        onPrimaryContainer = CareGreenDark,
        onSecondaryContainer = CareGreenDark,
        onBackground = CarePalette.Navy,
        onSurface = CarePalette.Navy,
        onSurfaceVariant = Color(0xFF475569),
        outline = CarePalette.OutlineSoft,
        outlineVariant = Color(0xFFF1F5F9),
        error = Color(0xFFDC2626),
        onError = Color.White,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF81C784),
        onPrimary = Color(0xFF0D1F12),
        primaryContainer = Color(0xFF264D33),
        secondary = CareGreen,
        onSecondary = Color.White,
        tertiary = CarePalette.Mint,
        background = Color(0xFF121512),
        surface = Color(0xFF1C221E),
        surfaceVariant = Color(0xFF2A322C),
        onPrimaryContainer = Color(0xFFC8E6C9),
        onBackground = Color(0xFFE8EBE9),
        onSurface = Color(0xFFE8EBE9),
        onSurfaceVariant = Color(0xFFCBD5E1),
        outline = Color(0xFF475569),
        outlineVariant = Color(0xFF334155),
        error = Color(0xFFF87171),
        onError = Color(0xFF450A0A),
    )

@Composable
fun CareCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
