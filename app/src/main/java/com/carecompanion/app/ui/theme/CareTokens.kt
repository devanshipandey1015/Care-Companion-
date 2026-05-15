package com.carecompanion.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Care Companion unified design tokens (light-first; pair with [CareCompanionTheme] dark scheme).
 *
 * **Dark mode proposal:** enable `CareCompanionTheme(darkTheme = true)` — surfaces shift to charcoal
 * (#121512 / #1C221E), text to soft white (#E8EBE9), primary mint-green accents unchanged for brand,
 * SOS/error reds slightly desaturated for night viewing.
 */
object CareRadius {
    val xs = 10.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 28.dp
}

object CareElevation {
    val flat = 0.dp
    val hairline = 1.dp
    val raised = 2.dp
    val card = 4.dp
    val sticky = 6.dp
    val overlay = 8.dp
    val modal = 14.dp
    val sheet = 18.dp
}

object CareSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val gutterScreen = 18.dp
}

/**
 * Semantic palette shared by guardian + elder flows. Prefer over one-off literals.
 */
object CarePalette {
    val Navy = Color(0xFF14213D)
    val SoftBlue = Color(0xFF4EA8DE)
    val Mint = Color(0xFF7BD389)
    val PageBgLight = Color(0xFFF5F7FB)
    /** Alias for surfaces that sit on page bg */
    val SurfaceMuted = Color(0xFFF4F6F4)
    val Emergency = Color(0xFFFF4D4D)
    val SurfaceGlassLight = Color(0xE8FFFFFF)
    val SurfaceGlassStrong = Color(0xF2FFFFFF)
    val BorderGlass = Color(0x99FFFFFF)
    val OutlineSoft = Color(0xFFE2E8F0)
    val TextMuted = Color(0xFF64748B)
}
