package com.carecompanion.app

import androidx.compose.runtime.compositionLocalOf

enum class ElderLanguage {
    ENGLISH,
    HINDI,
    MARATHI,
    GUJARATI,
}

val LocalElderLanguage = compositionLocalOf { ElderLanguage.ENGLISH }
