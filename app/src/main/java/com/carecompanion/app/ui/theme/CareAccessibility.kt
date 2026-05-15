package com.carecompanion.app.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Elder-first accessibility rules for Care Companion.
 *
 * - **Touch targets:** interactive controls ≥ [MinTouchTarget] (WCAG 2.5.5 AAA-oriented).
 * - **Copy:** prefer ≥ [MinBodySp] sp for primary labels on elder surfaces; secondary line ≥ 13 sp.
 * - **Contrast:** pair [CarePalette.Navy] / near-black text with white or off-white surfaces (AA body).
 * - **Motion:** keep feedback subtle (scale/ripple); avoid rapid flashing reds except SOS semantics.
 */
object CareAccessibility {
    const val MIN_TOUCH_DP = 48
    val MinTouchTarget = 48.dp

    const val MIN_BODY_SP = 15

    /** Comfortable minimum row / chip tap height inside lists */
    val MinComfortRowHeight = 52.dp

    /** Minimum icon tile for primary actions (contacts, SOS satellite buttons) */
    val MinIconWellDp = 44
}
