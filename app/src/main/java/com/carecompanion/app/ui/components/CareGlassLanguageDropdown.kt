package com.carecompanion.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.carecompanion.app.ElderLanguage
import com.carecompanion.app.ui.theme.CareElevation
import com.carecompanion.app.ui.theme.CareGreen
import com.carecompanion.app.ui.theme.CarePalette
import com.carecompanion.app.ui.theme.CareRadius
import com.carecompanion.app.ui.theme.CareSpacing

private data class LangVisual(
    val id: ElderLanguage,
    val flag: String,
    val primary: String,
    val secondaryEn: String,
)

@Composable
fun CareGlassLanguageDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    selected: ElderLanguage,
    onSelect: (ElderLanguage) -> Unit,
    menuTitle: String,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset((-6).dp, 6.dp),
) {
    val rows =
        remember {
            listOf(
                LangVisual(ElderLanguage.ENGLISH, "🌐", "English", "English"),
                LangVisual(ElderLanguage.HINDI, "🇮🇳", "हिन्दी", "Hindi"),
                LangVisual(ElderLanguage.MARATHI, "🇮🇳", "मराठी", "Marathi"),
                LangVisual(ElderLanguage.GUJARATI, "🇮🇳", "ગુજરાતી", "Gujarati"),
            )
        }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier =
            modifier.widthIn(min = 288.dp),
        offset = offset,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(min = 268.dp)
                    .shadow(CareElevation.sheet, RoundedCornerShape(CareRadius.xl))
                    .clip(RoundedCornerShape(CareRadius.xl))
                    .border(
                        BorderStroke(1.dp, CarePalette.BorderGlass),
                        RoundedCornerShape(CareRadius.xl),
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                CarePalette.SurfaceGlassStrong,
                                CarePalette.SurfaceGlassLight,
                            ),
                        ),
                    )
                    .padding(CareSpacing.sm),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CareRadius.lg))
                        .background(Color.White.copy(alpha = 0.35f))
                        .padding(horizontal = CareSpacing.md, vertical = CareSpacing.md),
            ) {
                Text(
                    text = menuTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CarePalette.Navy,
                )
            }

            HorizontalDivider(
                modifier =
                    Modifier
                        .padding(vertical = CareSpacing.sm),
                color = CarePalette.OutlineSoft.copy(alpha = 0.85f),
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                rows.forEach { row ->
                    GlassLanguageRow(
                        flag = row.flag,
                        primary = row.primary,
                        secondary = row.secondaryEn,
                        selected = row.id == selected,
                        onClick = {
                            onSelect(row.id)
                            onDismissRequest()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassLanguageRow(
    flag: String,
    primary: String,
    secondary: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(CareRadius.md)

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(
                    if (selected) {
                        Brush.horizontalGradient(
                            listOf(
                                CarePalette.SoftBlue.copy(alpha = 0.18f),
                                CarePalette.Mint.copy(alpha = 0.14f),
                                Color.White.copy(alpha = 0.6f),
                            ),
                        )
                    } else {
                        Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.42f), Color.White.copy(alpha = 0.22f)))
                    },
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .semantics {
                    role = Role.Button
                    contentDescription = "$primary. $secondary"
                }
                .padding(horizontal = CareSpacing.md, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(CareSpacing.md),
    ) {
        Text(text = flag, fontSize = 26.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = primary,
                fontSize = 17.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                color = CarePalette.Navy,
            )
            Text(
                text = secondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = CarePalette.TextMuted,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = CareGreen,
                modifier = Modifier.padding(end = 4.dp),
            )
        }
    }
}
