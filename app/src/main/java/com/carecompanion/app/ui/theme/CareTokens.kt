package com.carecompanion.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Care Companion design tokens — healthcare / safety visual language (light-first).
 * Pair with [CareCompanionTheme]; prefer these over one-off literals in new UI.
 */
object CareRadius {
    val xs = 10.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 28.dp
    /** Premium cards — spec: 24.dp */
    val card = 24.dp
    val button = 20.dp
    val pill = 28.dp
    val headerBottom = 32.dp
}

object CareShapes {
    val card = RoundedCornerShape(CareRadius.card)
    val button = RoundedCornerShape(CareRadius.button)
    val pill = RoundedCornerShape(CareRadius.pill)
    val headerBottom = RoundedCornerShape(bottomStart = CareRadius.headerBottom, bottomEnd = CareRadius.headerBottom)
}

object CareElevation {
    val flat = 0.dp
    val hairline = 1.dp
    val raised = 2.dp
    val card = 4.dp
    val sticky = 6.dp
    val overlay = 8.dp
    val modal = 12.dp
    val sheet = 16.dp
    val floatingNav = 14.dp
}

object CareSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val gutterScreen = 18.dp
    /** Card interior padding — between 16–20.dp */
    val cardPadding = 18.dp
}

object CareDim {
    val buttonMinHeight = 52.dp
    val textFieldMinHeight = 58.dp
}

/**
 * Semantic palette: background #F5F7FB, navy #14213D, primary blue #4EA8DE,
 * care green #2E8540, mint #7BD389, emergency #FF4D4D, muted #64748B.
 */
object CarePalette {
    val Navy = Color(0xFF14213D)
    val SoftBlue = Color(0xFF4EA8DE)
    /** Alias — primary interactive blue */
    val PrimaryBlue = SoftBlue
    val Mint = Color(0xFF7BD389)
    val PageBgLight = Color(0xFFF5F7FB)
    val CardWhite = Color(0xFFFFFFFF)
    /** Alias for surfaces that sit on page bg */
    val SurfaceMuted = Color(0xFFF4F6F4)
    val Emergency = Color(0xFFFF4D4D)
    val SurfaceGlassLight = Color(0xE8FFFFFF)
    val SurfaceGlassStrong = Color(0xF2FFFFFF)
    val BorderGlass = Color(0x99FFFFFF)
    val OutlineSoft = Color(0xFFE2E8F0)
    val TextMuted = Color(0xFF64748B)
    /** Secondary body — slightly softer than navy */
    val TextSecondary = Color(0xFF475569)
}

object CareGradients {
    fun heroNavyBlueMint(): Brush =
        Brush.verticalGradient(
            colors =
                listOf(
                    CarePalette.Navy,
                    Color(0xFF1C3A63),
                    CarePalette.PrimaryBlue.copy(alpha = 0.92f),
                    CarePalette.Mint.copy(alpha = 0.42f),
                ),
        )

    fun primaryCta(enabled: Boolean): Brush =
        if (enabled) {
            Brush.horizontalGradient(
                colors =
                    listOf(
                        CarePalette.PrimaryBlue,
                        CarePalette.Mint,
                    ),
            )
        } else {
            Brush.horizontalGradient(
                colors =
                    listOf(
                        CarePalette.OutlineSoft,
                        CarePalette.SurfaceMuted,
                    ),
            )
        }

    fun emergency(): Brush =
        Brush.horizontalGradient(
            colors =
                listOf(
                    CarePalette.Emergency,
                    Color(0xFFD62323),
                ),
        )

    fun subtleHeaderWash(): Brush =
        Brush.verticalGradient(
            colors =
                listOf(
                    CarePalette.PrimaryBlue.copy(alpha = 0.08f),
                    Color.Transparent,
                ),
        )

    /** Soft vertical wash behind scrollable screens — replaces flat page gray */
    fun pageSoftWash(): Brush =
        Brush.verticalGradient(
            colors =
                listOf(
                    CarePalette.PrimaryBlue.copy(alpha = 0.07f),
                    CarePalette.PageBgLight,
                    CarePalette.Mint.copy(alpha = 0.09f),
                ),
        )
}
