package com.carecompanion.app

import android.net.Uri

data class ManagedContact(
    val name: String,
    val phone: String,
    val photoUri: Uri? = null,
    val relationship: String = "",
    val isEmergencyContact: Boolean = false,
    val isFavorite: Boolean = false,
)
