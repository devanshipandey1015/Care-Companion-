package com.carecompanion.app

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.carecompanion.app.ui.theme.CareDim
import com.carecompanion.app.ui.theme.CareGreen
import com.carecompanion.app.ui.theme.CarePalette
import com.carecompanion.app.ui.theme.CareSpacing

private data class RelationshipOption(val label: String, val icon: ImageVector)

private val relationshipOptions =
    listOf(
        RelationshipOption("Family", Icons.Outlined.Group),
        RelationshipOption("Doctor", Icons.Outlined.LocalHospital),
        RelationshipOption("Neighbor", Icons.Outlined.Home),
        RelationshipOption("Friend", Icons.Outlined.FavoriteBorder),
    )

@Composable
fun GuardianAddContactScreen(
    profile: GuardianProfile,
    onBack: () -> Unit,
    onSave: (ManagedContact) -> Unit,
) {
    val ctx = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var relationship by remember { mutableStateOf("") }
    var isEmergencyContact by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var snackMsg by remember { mutableStateOf<String?>(null) }

    val photoPicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia(),
        ) { uri -> uri?.let { photoUri = it } }

    val contactPicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.PickContact(),
        ) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            val (n, p) = ContactImport.readNameAndPhone(ctx, uri)
            if (n != null) name = n
            if (p != null) phone = p
            snackMsg = "Contact imported"
        }

    val permLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (granted) contactPicker.launch(null)
            else snackMsg = "Contacts permission required"
        }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackMsg) {
        snackMsg?.let {
            snackbarHostState.showSnackbar(it)
            snackMsg = null
        }
    }

    val fieldShape = RoundedCornerShape(18.dp)
    val fieldColors =
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = CarePalette.PageBgLight,
        topBar = {},
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            // Sticky gradient header — stays above scrolling form
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
                        .background(ContactsGrad)
                        .statusBarsPadding()
                        .padding(horizontal = CareSpacing.gutterScreen, vertical = CareSpacing.lg),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(23.dp),
                        )
                    }
                    Spacer(Modifier.width(CareSpacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Add Emergency Contact",
                            style =
                                MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    lineHeight = 26.sp,
                                ),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Add someone trusted",
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                ),
                        )
                        Text(
                            text = "For ${profile.name}",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.78f),
                                    fontSize = 13.sp,
                                ),
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = CareSpacing.gutterScreen),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(CareSpacing.xl))

                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier =
                            Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(
                                    if (photoUri == null) {
                                        Brush.radialGradient(listOf(Color(0xFFDDF2E2), Color(0xFF4B8B62)))
                                    } else {
                                        Brush.radialGradient(listOf(Color.LightGray, Color.Gray))
                                    },
                                )
                                .clickable {
                                    photoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                    )
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (photoUri != null) {
                            UriBitmapImage(
                                uri = photoUri!!,
                                contentDescription = "Contact photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                                Text("Tap to add", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    Box(
                        modifier =
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GuardianPrimary)
                                .border(2.dp, Color.White, CircleShape)
                                .clickable {
                                    photoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                    )
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.CameraAlt, contentDescription = "Camera", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(Modifier.height(CareSpacing.sm))
                Text("Contact Photo", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GuardianTextSub)

                Spacer(Modifier.height(CareSpacing.xl))

                PremiumCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(CareSpacing.lg)) {
                        Text(
                            text = "Contact details",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = GuardianTextPrimary,
                                ),
                        )
                        Text(
                            text = "We’ll reach them quickly if something needs attention.",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = GuardianTextSub,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                ),
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full name") },
                            placeholder = { Text("Full legal name") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(22.dp), tint = CarePalette.PrimaryBlue)
                            },
                            modifier = Modifier.fillMaxWidth().heightIn(min = CareDim.textFieldMinHeight + 10.dp),
                            shape = fieldShape,
                            colors = fieldColors,
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Mobile number") },
                            placeholder = { Text("Include country code if needed") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Phone, contentDescription = null, modifier = Modifier.size(22.dp), tint = CarePalette.PrimaryBlue)
                            },
                            modifier = Modifier.fillMaxWidth().heightIn(min = CareDim.textFieldMinHeight + 10.dp),
                            shape = fieldShape,
                            colors = fieldColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                        )

                        Text(
                            "Relationship",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GuardianTextPrimary,
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                        ) {
                            items(relationshipOptions, key = { it.label }) { opt ->
                                val selected = relationship == opt.label
                                FilterChip(
                                    selected = selected,
                                    onClick = { relationship = opt.label },
                                    label = {
                                        Text(opt.label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    },
                                    leadingIcon = {
                                        Icon(opt.icon, contentDescription = null, modifier = Modifier.size(18.dp))
                                    },
                                    border = BorderStroke(1.dp, if (selected) CarePalette.PrimaryBlue else CarePalette.OutlineSoft),
                                    colors =
                                        FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CarePalette.PrimaryBlue.copy(alpha = 0.15f),
                                            selectedLabelColor = CarePalette.Navy,
                                            selectedLeadingIconColor = CarePalette.PrimaryBlue,
                                        ),
                                )
                            }
                        }

                        HorizontalDivider(color = CarePalette.OutlineSoft.copy(alpha = 0.75f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Mark as primary emergency contact",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = GuardianTextPrimary,
                                )
                                Text(
                                    "Shown first in SOS & alert flows",
                                    fontSize = 12.sp,
                                    color = GuardianTextSub,
                                )
                            }
                            Switch(
                                checked = isEmergencyContact,
                                onCheckedChange = { isEmergencyContact = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CareGreen),
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Favorite", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = GuardianTextPrimary)
                                Text("Pinned at the top of your list", fontSize = 12.sp, color = GuardianTextSub)
                            }
                            Switch(
                                checked = isFavorite,
                                onCheckedChange = { isFavorite = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = CareGreen),
                            )
                        }

                        HorizontalDivider(color = CarePalette.OutlineSoft.copy(alpha = 0.55f))

                        OutlinedButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                                    contactPicker.launch(null)
                                } else {
                                    permLauncher.launch(Manifest.permission.READ_CONTACTS)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GuardianPrimary),
                            border = BorderStroke(1.5.dp, CareGreen.copy(alpha = 0.65f)),
                            contentPadding = PaddingValues(vertical = 14.dp),
                        ) {
                            Icon(Icons.Outlined.Contacts, contentDescription = null, modifier = Modifier.size(21.dp))
                            Spacer(Modifier.width(CareSpacing.sm))
                            Text("Import from phone contacts", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }

                Spacer(Modifier.height(96.dp))
            }

            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 12.dp,
                color = CarePalette.CardWhite,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = CareSpacing.gutterScreen, vertical = CareSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(CareSpacing.sm),
                ) {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.Start),
                    ) {
                        Text("Cancel / Back", fontWeight = FontWeight.SemiBold, color = CarePalette.TextMuted, fontSize = 15.sp)
                    }
                    GradientButton(
                        text = "Save Contact",
                        onClick = {
                            if (name.isNotBlank() && phone.isNotBlank()) {
                                onSave(
                                    ManagedContact(
                                        name = name.trim(),
                                        phone = phone.trim(),
                                        photoUri = photoUri,
                                        relationship = relationship.trim(),
                                        isEmergencyContact = isEmergencyContact,
                                        isFavorite = isFavorite,
                                    ),
                                )
                            } else {
                                snackMsg = "Please enter name and phone"
                            }
                        },
                        gradient = ContactsGrad,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = 54.dp),
                        enabled = name.isNotBlank() && phone.isNotBlank(),
                    )
                }
            }
        }
    }
}
