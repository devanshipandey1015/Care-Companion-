package com.carecompanion.app

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carecompanion.app.ui.theme.CareGradients
import com.carecompanion.app.ui.theme.CareGreen

private val NavyDeep = Color(0xFF14213D)
private val SoftLine = Color(0xFFE2E8F0)
private val IceRed = Color(0xFFDC2626)

@Composable
fun GuardianManageContactsScreen(
    profile: GuardianProfile,
    initialContacts: List<ManagedContact>,
    onBack: () -> Unit,
    onSaveContacts: (List<ManagedContact>) -> Unit,
    onAddContact: () -> Unit = {},
    onOpenAlerts: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val contacts = remember(initialContacts) {
        mutableStateListOf<ManagedContact>().apply { addAll(initialContacts) }
    }
    val recentPhones = remember {
        mutableStateListOf<String>()
    }
    var confirmDeleteIndex by remember { mutableStateOf<Int?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val ctx = LocalContext.current

    val filtered = remember(contacts.size, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) contacts.toList()
        else contacts.filter {
            it.name.lowercase().contains(q) || it.phone.filter { ch -> ch.isDigit() }.contains(q.filter { ch -> ch.isDigit() }) ||
                it.phone.lowercase().contains(q)
        }
    }

    val favorites = remember(filtered) {
        filtered.filter { it.isFavorite }.distinctBy { it.phone }
    }
    val favPhoneSet = remember(favorites) { favorites.map { it.phone }.toSet() }

    val recentSnapshot = recentPhones.toList()
    val recentContacts = remember(filtered, favPhoneSet, recentSnapshot) {
        recentSnapshot.mapNotNull { phone -> filtered.find { it.phone == phone } }
            .filter { it.phone !in favPhoneSet }
            .distinctBy { it.phone }
    }
    val recentPhoneSet = remember(recentContacts) { recentContacts.map { it.phone }.toSet() }

    val everyoneElse = remember(filtered, favPhoneSet, recentPhoneSet) {
        filtered.filter { it.phone !in favPhoneSet && it.phone !in recentPhoneSet }
    }

    fun toggleFavorite(contact: ManagedContact) {
        val idx = contacts.indexOfFirst { it.phone == contact.phone }
        if (idx >= 0) {
            contacts[idx] = contacts[idx].copy(isFavorite = !contacts[idx].isFavorite)
            onSaveContacts(contacts.toList())
        }
    }

    fun recordRecentCall(phone: String) {
        recentPhones.remove(phone)
        recentPhones.add(0, phone)
        while (recentPhones.size > 8) recentPhones.removeAt(recentPhones.lastIndex)
    }

    fun dial(contact: ManagedContact) {
        val digits = contact.phone.filter { it.isDigit() || it == '+' }
        runCatching {
            ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(digits)}")))
            recordRecentCall(contact.phone)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CareGradients.pageSoftWash()),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddContact,
                containerColor = CareGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                icon = {
                    Icon(Icons.Outlined.PersonAdd, contentDescription = null, modifier = Modifier.size(22.dp))
                },
                text = {
                    Text("Add contact", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                },
            )
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        bottomBar = {
            GuardianBottomBar(
                activeTab = BottomTab.Home,
                onHome = onBack,
                onAlerts = onOpenAlerts,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            item {
                ContactsTopHero(
                    profileName = profile.name,
                    contactCount = contacts.size,
                    onBack = onBack,
                    onLogout = onLogout,
                )
            }

            item {
                ContactsSearchCard(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier
                        .offset(y = (-18).dp)
                        .padding(horizontal = 16.dp),
                )
            }

            item { Spacer(Modifier.height(6.dp)) }

            when {
                contacts.isEmpty() -> item {
                    ContactsEmptyState(onAddContact = onAddContact)
                }

                filtered.isEmpty() -> item {
                    NoSearchResultsCard(query = searchQuery)
                }

                else -> {
                    if (favorites.isNotEmpty()) {
                        item {
                            SectionHeading(
                                title = "Favorites",
                                subtitle = "${favorites.size} pinned",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            )
                        }
                        items(favorites, key = { it.phone }) { contact ->
                            ContactCardModern(
                                contact = contact,
                                onDelete = {
                                    val idx = contacts.indexOfFirst { it.phone == contact.phone }
                                    if (idx >= 0) confirmDeleteIndex = idx
                                },
                                onToggleFavorite = { toggleFavorite(contact) },
                                onCall = { dial(contact) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            )
                        }
                    }

                    if (recentContacts.isNotEmpty()) {
                        item {
                            SectionHeading(
                                title = "Recently contacted",
                                subtitle = "Quick return dial",
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp),
                            )
                        }
                        items(recentContacts, key = { "${it.phone}_recent" }) { contact ->
                            ContactCardModern(
                                contact = contact,
                                onDelete = {
                                    val idx = contacts.indexOfFirst { it.phone == contact.phone }
                                    if (idx >= 0) confirmDeleteIndex = idx
                                },
                                onToggleFavorite = { toggleFavorite(contact) },
                                onCall = { dial(contact) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            )
                        }
                    }

                    item {
                        SectionHeading(
                            title = when {
                                favorites.isEmpty() && recentContacts.isEmpty() -> "Your circle"
                                else -> "All contacts"
                            },
                            subtitle = "${filtered.size} visible · ${contacts.size} total",
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 10.dp),
                        )
                    }

                    items(everyoneElse, key = { it.phone }) { contact ->
                        ContactCardModern(
                            contact = contact,
                            onDelete = {
                                val idx = contacts.indexOfFirst { it.phone == contact.phone }
                                if (idx >= 0) confirmDeleteIndex = idx
                            },
                            onToggleFavorite = { toggleFavorite(contact) },
                            onCall = { dial(contact) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        )
                    }

                    if (favorites.isEmpty() && contacts.isNotEmpty()) {
                        item {
                            FavoritesHintCard(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }
                }
            }

            item {
                AlertsDigestCard(
                    profileName = profile.name,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 8.dp),
                )
            }
        }
    }
    }

    confirmDeleteIndex?.let { idx ->
        AlertDialog(
            onDismissRequest = { confirmDeleteIndex = null },
            title = { Text("Remove contact?") },
            text = { Text("Remove ${contacts.getOrNull(idx)?.name} from the list?") },
            confirmButton = {
                Button(
                    onClick = {
                        contacts.removeAt(idx)
                        onSaveContacts(contacts.toList())
                        confirmDeleteIndex = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IceRed),
                ) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteIndex = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun ContactsTopHero(
    profileName: String,
    contactCount: Int,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ContactsGrad),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 36.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Contacts",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.3).sp,
                    )
                    Text(
                        text = "Emergency circle · $profileName",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.88f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.18f),
                ) {
                    Text(
                        text = "$contactCount",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp,
                    )
                }
                IconButton(onClick = onLogout) {
                    Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = Color.White.copy(alpha = 0.9f))
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = "REACH YOUR PEOPLE FAST",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.72f),
                letterSpacing = 1.2.sp,
            )
        }
    }
}

@Composable
private fun ContactsSearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 10.dp,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, SoftLine.copy(alpha = 0.6f)),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search name or number", color = GuardianTextSub, fontSize = 15.sp)
            },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = GuardianPrimary, modifier = Modifier.size(22.dp))
            },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = GuardianPrimary,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions.Default,
        )
    }
}

@Composable
private fun SectionHeading(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = NavyDeep,
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = GuardianTextSub,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun ContactCardModern(
    contact: ManagedContact,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        border = BorderStroke(1.dp, SoftLine.copy(alpha = 0.85f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.White, Color(0xFFF7FBF8)),
                    ),
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF7BD389), Color(0xFF4B8B62))))
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(Color(0xFFDDF6E8), Color(0xFFE8F5EC))),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (contact.photoUri != null) {
                            UriBitmapImage(
                                uri = contact.photoUri,
                                contentDescription = contact.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Text(
                                contact.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                color = GuardianPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contact.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NavyDeep,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (contact.relationship.isNotBlank()) {
                        Text(
                            text = contact.relationship,
                            fontSize = 12.sp,
                            color = GuardianTextSub,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 6.dp),
                    ) {
                        Icon(
                            Icons.Outlined.Phone,
                            contentDescription = null,
                            tint = GuardianTextSub.copy(alpha = 0.85f),
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = contact.phone,
                            fontSize = 13.sp,
                            color = GuardianTextSub,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (contact.isEmergencyContact) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = IceRed.copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, IceRed.copy(alpha = 0.35f)),
                            ) {
                                Text(
                                    text = "ICE · Emergency",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IceRed,
                                )
                            }
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            imageVector = if (contact.isFavorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (contact.isFavorite) "Remove favorite" else "Add favorite",
                            tint = if (contact.isFavorite) Color(0xFFF59E0B) else GuardianTextSub,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF2F855A), CareGreen)))
                            .clickable(onClick = onCall),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.Call,
                            contentDescription = "Call ${contact.name}",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactsEmptyState(onAddContact: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onAddContact),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, SoftLine),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAF6EC)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    tint = GuardianPrimary,
                    modifier = Modifier.size(34.dp),
                )
            }
            Text(
                text = "No contacts yet",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = NavyDeep,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Build your emergency list — we'll surface ICE tags, favorites, and fast dial here.",
                fontSize = 13.sp,
                color = GuardianTextSub,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tap + Add contact",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GuardianPrimary,
            )
        }
    }
}

@Composable
private fun NoSearchResultsCard(query: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, SoftLine),
        shadowElevation = 2.dp,
    ) {
        Text(
            text = "No matches for \"$query\". Try another name or digits.",
            modifier = Modifier.padding(20.dp),
            fontSize = 14.sp,
            color = GuardianTextSub,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FavoritesHintCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFFFFBEB),
        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.StarBorder, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(22.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Tip · Favorites", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDeep)
                Text(
                    "Tap the star on any card to pin them here for quicker access.",
                    fontSize = 12.sp,
                    color = GuardianTextSub,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun AlertsDigestCard(
    profileName: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, SoftLine.copy(alpha = 0.75f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    Icons.Outlined.NotificationsActive,
                    contentDescription = null,
                    tint = IceRed,
                    modifier = Modifier.size(22.dp),
                )
                Column {
                    Text("Alerts digest", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDeep)
                    Text("Linked to $profileName", fontSize = 12.sp, color = GuardianTextSub)
                }
            }
            HorizontalDivider(color = SoftLine.copy(alpha = 0.7f))
            AlertRow(
                icon = Icons.Outlined.LocalPharmacy,
                text = "$profileName missed her 8:00 am medicine",
                iconBg = Color(0xFFFFF7ED),
                iconTint = Color(0xFFF97316),
            )
            AlertRow(
                icon = Icons.Outlined.Warning,
                text = "SOS triggered · 10:32 AM · Khar",
                iconBg = Color(0xFFFEF2F2),
                iconTint = IceRed,
            )
        }
    }
}

@Composable
private fun AlertRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    iconBg: Color,
    iconTint: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = NavyDeep,
            lineHeight = 18.sp,
        )
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = GuardianTextSub, modifier = Modifier.size(18.dp))
    }
}
