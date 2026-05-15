package com.carecompanion.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

private val SoftBackground = Color(0xFFF5F7FB)
private val DeepNavy = Color(0xFF14213D)
private val SoftBlue = Color(0xFF4EA8DE)
private val MintGreen = Color(0xFF7BD389)
private val CardWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF14213D)
private val TextSecondary = Color(0xFF5C6578)
private val TextMuted = Color(0xFF8E96A8)
private val FieldBorder = Color(0xFFE2E8F2)
private val FieldSurface = Color(0xFFF5F7FB)
private val ErrorColor = Color(0xFFB42318)

private data class AvatarOption(val icon: ImageVector, val bg: Color)

private val avatarOptions = listOf(
    AvatarOption(Icons.Outlined.Person, Color(0xFFE8F4FD)),
    AvatarOption(Icons.Outlined.Favorite, Color(0xFFFDF2F8)),
    AvatarOption(Icons.Outlined.LocalHospital, Color(0xFFE8F5E9)),
    AvatarOption(Icons.Outlined.Call, Color(0xFFFFF8E1)),
    AvatarOption(Icons.Outlined.Phone, Color(0xFFE0F7FA)),
    AvatarOption(Icons.Outlined.Home, Color(0xFFF3E5F5)),
)

private data class EmergencySlot(
    val key: Int,
    val name: String,
    val phone: String,
)

@Composable
fun GuardianAddProfileScreen(
    onBack: () -> Unit,
    onSaveNext: (GuardianProfile) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var avatarIndex by remember { mutableIntStateOf(0) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    val emergencySlots = remember { mutableStateListOf(EmergencySlot(key = 0, name = "", phone = "")) }
    var nextEmergencyKey by remember { mutableIntStateOf(1) }

    var primaryPhoneError by remember { mutableStateOf<String?>(null) }
    var emergencyErrors by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }

    val emergencyFingerprint =
        emergencySlots.joinToString("\u0000") { "${it.key}|${it.name}|${it.phone}" }
    val formProgress = remember(name, phone, emergencyFingerprint) {
        var done = 0f
        if (name.isNotBlank()) done += 1f
        if (digitsOnly(phone).length >= 10) done += 1f
        if (emergencySlots.any { it.name.isNotBlank() && digitsOnly(it.phone).length >= 10 }) done += 1f
        (done / 3f).coerceIn(0f, 1f)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = formProgress,
        animationSpec = spring(dampingRatio = 0.88f, stiffness = 380f),
        label = "formProgress",
    )

    fun updateEmergencyName(key: Int, value: String) {
        val idx = emergencySlots.indexOfFirst { it.key == key }
        if (idx >= 0) {
            emergencySlots[idx] = emergencySlots[idx].copy(name = value)
            emergencyErrors = emergencyErrors - key
        }
    }

    fun updateEmergencyPhone(key: Int, value: String) {
        val idx = emergencySlots.indexOfFirst { it.key == key }
        if (idx >= 0) {
            emergencySlots[idx] = emergencySlots[idx].copy(phone = value)
            emergencyErrors = emergencyErrors - key
        }
    }

    fun addEmergencySlot() {
        emergencySlots.add(EmergencySlot(key = nextEmergencyKey++, name = "", phone = ""))
    }

    fun removeEmergencySlot(key: Int) {
        if (emergencySlots.size <= 1) return
        val idx = emergencySlots.indexOfFirst { it.key == key }
        if (idx >= 0) {
            emergencySlots.removeAt(idx)
            emergencyErrors = emergencyErrors - key
        }
    }

    fun validateForm(): Boolean {
        var ok = true
        primaryPhoneError =
            if (phone.isNotBlank() && digitsOnly(phone).length < 10) {
                ok = false
                context.getString(R.string.guardian_error_phone_short)
            } else {
                null
            }

        val eMap = mutableMapOf<Int, String>()
        val incomplete = context.getString(R.string.guardian_error_emergency_incomplete)
        val shortPh = context.getString(R.string.guardian_error_emergency_phone_short)
        for (slot in emergencySlots) {
            val d = digitsOnly(slot.phone)
            val hasName = slot.name.isNotBlank()
            val hasPhone = d.isNotEmpty()
            when {
                hasName xor hasPhone -> {
                    eMap[slot.key] = incomplete
                    ok = false
                }
                hasPhone && d.length < 10 -> {
                    eMap[slot.key] = shortPh
                    ok = false
                }
            }
        }
        emergencyErrors = eMap
        return ok && primaryPhoneError == null
    }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        photoUri = uri
        if (uri != null) {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.guardian_photo_added))
            }
        }
    }

    val pickContact = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val (n, p) = ContactImport.readNameAndPhone(context, uri)
        if (emergencySlots.isNotEmpty()) {
            val first = emergencySlots[0]
            emergencySlots[0] = first.copy(
                name = if (!n.isNullOrBlank()) n else first.name,
                phone = if (!p.isNullOrBlank()) p else first.phone,
            )
        }
        if (!p.isNullOrBlank()) {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.guardian_contact_imported))
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.guardian_contact_no_phone))
            }
        }
    }

    val requestContactsPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            pickContact.launch(null)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.guardian_contacts_permission))
            }
        }
    }

    fun openContactPicker() {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED -> pickContact.launch(null)
            else -> requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    fun saveProfile() {
        if (!validateForm()) return
        val pick = avatarOptions[avatarIndex.coerceIn(0, avatarOptions.lastIndex)]
        onSaveNext(
            GuardianProfile(
                name = name.trim().ifBlank {
                    context.getString(R.string.guardian_unnamed_profile)
                },
                icon = pick.icon,
                bg = pick.bg,
                photoUri = photoUri,
            ),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SoftBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AddProfileStickyHeader(
                progress = animatedProgress,
                title = stringResource(R.string.guardian_add_profile_title),
                backDescription = stringResource(R.string.guardian_back),
                hint = stringResource(R.string.guardian_progress_hint),
                onBack = onBack,
            )
        },
        bottomBar = {
            AddProfileBottomBar(
                onSave = { saveProfile() },
                onCancel = onBack,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                SectionCard(
                    title = stringResource(R.string.guardian_personal_info),
                    subtitle = stringResource(R.string.guardian_section_personal_hint),
                    sectionIcon = Icons.Outlined.Person,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            ProfileAvatarUpload(
                                photoUri = photoUri,
                                avatarIcon = avatarOptions[avatarIndex].icon,
                                avatarBg = avatarOptions[avatarIndex].bg,
                                onPickPhoto = {
                                    pickImage.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly,
                                        ),
                                    )
                                },
                                modifier = Modifier.padding(top = 4.dp),
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                SoftSecondaryButton(
                                    onClick = {
                                        pickImage.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly,
                                            ),
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Outlined.UploadFile,
                                    label = stringResource(R.string.guardian_upload_photo),
                                    contentDescription = stringResource(R.string.guardian_upload_photo),
                                )
                                SoftSecondaryButton(
                                    onClick = { showAvatarDialog = true },
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Outlined.Person,
                                    label = stringResource(R.string.guardian_choose_avatar),
                                    contentDescription = stringResource(R.string.guardian_choose_avatar),
                                    outlined = true,
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = {
                                    name = it
                                    primaryPhoneError = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp),
                                singleLine = true,
                                label = { Text(stringResource(R.string.guardian_full_name)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(22.dp),
                                    )
                                },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 17.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium,
                                ),
                                shape = RoundedCornerShape(16.dp),
                                colors = outlinedFieldColors(),
                            )
                            if (photoUri != null) {
                                TextButton(
                                    onClick = { photoUri = null },
                                    modifier = Modifier.align(Alignment.Start),
                                ) {
                                    Text(
                                        stringResource(R.string.guardian_remove_photo),
                                        fontSize = 14.sp,
                                        color = TextMuted,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                SectionCard(
                    title = stringResource(R.string.guardian_contact_info),
                    subtitle = stringResource(R.string.guardian_section_contact_hint),
                    sectionIcon = Icons.Outlined.Call,
                ) {
                    Text(
                        text = stringResource(R.string.guardian_primary_phone_label),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        ),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                primaryPhoneError = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 56.dp),
                            singleLine = true,
                            isError = primaryPhoneError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Phone,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp),
                                )
                            },
                            label = { Text(stringResource(R.string.guardian_primary_phone)) },
                            supportingText = {
                                primaryPhoneError?.let {
                                    Text(it, color = ErrorColor, fontSize = 13.sp)
                                }
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 17.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium,
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = outlinedFieldColors(error = primaryPhoneError != null),
                        )
                        OtpPillButton(
                            label = stringResource(R.string.guardian_get_otp),
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        context.getString(R.string.guardian_otp_demo),
                                    )
                                }
                            },
                            modifier = Modifier.heightIn(min = 56.dp, max = 56.dp),
                        )
                    }
                }
            }

            item {
                SectionCard(
                    title = stringResource(R.string.guardian_emergency_section),
                    subtitle = stringResource(R.string.guardian_section_emergency_hint),
                    sectionIcon = Icons.Outlined.LocalHospital,
                    trailingHeader = {
                        TextButton(
                            onClick = { openContactPicker() },
                        ) {
                            Icon(
                                Icons.Outlined.UploadFile,
                                contentDescription = null,
                                tint = SoftBlue,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                stringResource(R.string.guardian_import_contacts),
                                color = SoftBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                            )
                        }
                    },
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        emergencySlots.forEachIndexed { index, slot ->
                            key(slot.key) {
                                EmergencyContactCard(
                                    contactNumber = index + 1,
                                    slot = slot,
                                    errorMessage = emergencyErrors[slot.key],
                                    canRemove = emergencySlots.size > 1,
                                    onNameChange = { updateEmergencyName(slot.key, it) },
                                    onPhoneChange = { updateEmergencyPhone(slot.key, it) },
                                    onRemove = { removeEmergencySlot(slot.key) },
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { addEmergencySlot() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(
                                width = 2.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        MintGreen.copy(alpha = 0.55f),
                                        SoftBlue.copy(alpha = 0.45f),
                                    ),
                                ),
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = DeepNavy,
                            ),
                        ) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = null,
                                tint = MintGreen,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                stringResource(R.string.guardian_add_another_contact),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    stringResource(R.string.guardian_pick_avatar_title),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 18.sp,
                )
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 280.dp),
                ) {
                    itemsIndexed(avatarOptions) { index, option ->
                        val selected = index == avatarIndex
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .shadow(
                                    elevation = if (selected) 10.dp else 4.dp,
                                    shape = CircleShape,
                                    spotColor = if (selected) SoftBlue.copy(alpha = 0.35f) else Color.Transparent,
                                )
                                .clip(CircleShape)
                                .background(option.bg)
                                .border(
                                    width = if (selected) 3.dp else 1.dp,
                                    brush = if (selected) {
                                        Brush.linearGradient(listOf(SoftBlue, MintGreen))
                                    } else {
                                        Brush.linearGradient(listOf(FieldBorder, FieldBorder))
                                    },
                                    shape = CircleShape,
                                )
                                .clickable {
                                    avatarIndex = index
                                    photoUri = null
                                    showAvatarDialog = false
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                option.icon,
                                contentDescription = null,
                                modifier = Modifier.size(34.dp),
                                tint = DeepNavy.copy(alpha = 0.75f),
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text(stringResource(android.R.string.ok), color = SoftBlue, fontWeight = FontWeight.Bold)
                }
            },
        )
    }
}

private fun digitsOnly(s: String): String = s.filter { it.isDigit() }

@Composable
private fun AddProfileStickyHeader(
    progress: Float,
    title: String,
    backDescription: String,
    hint: String,
    onBack: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        color = CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(bottom = 14.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(52.dp)
                        .semantics { contentDescription = backDescription },
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = DeepNavy,
                        modifier = Modifier.size(26.dp),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 52.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = TextPrimary,
                            letterSpacing = (-0.25).sp,
                        ),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = SoftBlue,
                        trackColor = MintGreen.copy(alpha = 0.22f),
                    )
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp,
                        ),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    sectionIcon: ImageVector,
    modifier: Modifier = Modifier,
    trailingHeader: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = DeepNavy.copy(alpha = 0.06f),
                spotColor = SoftBlue.copy(alpha = 0.12f),
            ),
        shape = RoundedCornerShape(26.dp),
        color = CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SoftBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        sectionIcon,
                        contentDescription = null,
                        tint = SoftBlue,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
            trailingHeader?.let { trail ->
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    trail()
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            content()
        }
    }
}

@Composable
private fun ProfileAvatarUpload(
    photoUri: android.net.Uri?,
    avatarIcon: ImageVector,
    avatarBg: Color,
    onPickPhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val avatarCd = stringResource(R.string.guardian_upload_photo)
    Box(modifier = modifier.size(118.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(if (photoUri == null) avatarBg else FieldSurface)
                .drawBehind {
                    val stroke = 2.5.dp.toPx()
                    drawCircle(
                        color = MintGreen.copy(alpha = 0.65f),
                        radius = size.minDimension / 2f - stroke / 2f,
                        center = Offset(size.width / 2f, size.height / 2f),
                        style = Stroke(
                            width = stroke,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f),
                        ),
                    )
                }
                .clickable(onClick = onPickPhoto)
                .semantics { contentDescription = avatarCd },
            contentAlignment = Alignment.Center,
        ) {
            when {
                photoUri != null -> {
                    UriBitmapImage(
                        uri = photoUri,
                        contentDescription = stringResource(R.string.guardian_photo_preview),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                else -> {
                    Icon(
                        avatarIcon,
                        contentDescription = null,
                        tint = DeepNavy.copy(alpha = 0.65f),
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = (-4).dp)
                .size(40.dp)
                .shadow(6.dp, CircleShape, spotColor = SoftBlue.copy(alpha = 0.35f))
                .clip(CircleShape)
                .background(SoftBlue)
                .clickable(onClick = onPickPhoto),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.PhotoCamera,
                contentDescription = stringResource(R.string.guardian_upload_photo),
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun SoftSecondaryButton(
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 520f),
        label = "secondaryBtn",
    )

    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .scale(scale)
                .heightIn(min = 48.dp)
                .semantics { this.contentDescription = contentDescription },
            interactionSource = interactionSource,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, FieldBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepNavy),
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = SoftBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
        }
    } else {
        Surface(
            modifier = modifier
                .scale(scale)
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .semantics { this.contentDescription = contentDescription },
            shape = RoundedCornerShape(14.dp),
            color = MintGreen.copy(alpha = 0.14f),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = DeepNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepNavy, maxLines = 1)
            }
        }
    }
}

@Composable
private fun OtpPillButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 560f),
        label = "otpPill",
    )
    Surface(
        modifier = modifier
            .scale(scale)
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(999.dp))
            .border(BorderStroke(1.5.dp, SoftBlue.copy(alpha = 0.45f)), RoundedCornerShape(999.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(999.dp),
        color = SoftBlue.copy(alpha = 0.1f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Outlined.Phone,
                contentDescription = null,
                tint = SoftBlue,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                label,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = DeepNavy,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun EmergencyContactCard(
    contactNumber: Int,
    slot: EmergencySlot,
    errorMessage: String?,
    canRemove: Boolean,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = FieldSurface.copy(alpha = 0.65f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, MintGreen.copy(alpha = 0.28f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.guardian_emergency_section) + " · $contactNumber",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy,
                        fontSize = 13.sp,
                    ),
                )
                if (canRemove) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(44.dp),
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.guardian_remove_contact),
                            tint = TextMuted,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }

            OutlinedTextField(
                value = slot.name,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                singleLine = true,
                isError = errorMessage != null,
                label = { Text(stringResource(R.string.guardian_field_name)) },
                placeholder = { Text(stringResource(R.string.guardian_emergency_name), color = TextMuted.copy(alpha = 0.65f)) },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp),
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                ),
                shape = RoundedCornerShape(16.dp),
                colors = outlinedFieldColors(error = errorMessage != null),
            )

            OutlinedTextField(
                value = slot.phone,
                onValueChange = onPhoneChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                singleLine = true,
                isError = errorMessage != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                label = { Text(stringResource(R.string.guardian_field_phone)) },
                placeholder = { Text(stringResource(R.string.guardian_emergency_phone), color = TextMuted.copy(alpha = 0.65f)) },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Phone,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp),
                    )
                },
                supportingText = {
                    errorMessage?.let {
                        Text(it, color = ErrorColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                ),
                shape = RoundedCornerShape(16.dp),
                colors = outlinedFieldColors(error = errorMessage != null),
            )
        }
    }
}

@Composable
private fun outlinedFieldColors(error: Boolean = false) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = if (error) ErrorColor else SoftBlue,
    unfocusedBorderColor = if (error) ErrorColor.copy(alpha = 0.65f) else FieldBorder,
    errorBorderColor = ErrorColor,
    cursorColor = SoftBlue,
    focusedLabelColor = if (error) ErrorColor else SoftBlue,
    unfocusedLabelColor = TextMuted,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedContainerColor = FieldSurface,
    unfocusedContainerColor = FieldSurface,
    errorCursorColor = ErrorColor,
)

@Composable
private fun AddProfileBottomBar(
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        color = CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, FieldBorder),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            GradientSaveButton(
                text = stringResource(R.string.guardian_save_next),
                onClick = onSave,
                contentDescription = stringResource(R.string.guardian_save_next),
            )
            TextButton(onClick = onCancel, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(
                    stringResource(R.string.guardian_cancel),
                    fontSize = 16.sp,
                    color = SoftBlue,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun GradientSaveButton(
    text: String,
    onClick: () -> Unit,
    contentDescription: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 520f),
        label = "saveScale",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .scale(scale)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = SoftBlue.copy(alpha = 0.42f),
                ambientColor = DeepNavy.copy(alpha = 0.08f),
            )
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(SoftBlue, MintGreen)))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
            ),
        )
    }
}
