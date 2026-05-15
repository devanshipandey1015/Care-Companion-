package com.carecompanion.app

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.carecompanion.app.ui.theme.CareCompanionTheme
import com.carecompanion.app.ui.components.CareGlassLanguageDropdown
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

private val ElderPageBg = Color(0xFFF5F7FB)
private val ElderNavy = Color(0xFF14213D)
private val ElderSoftBlue = Color(0xFF4EA8DE)
private val ElderMint = Color(0xFF7BD389)
private val ElderEmergency = Color(0xFFFF4D4D)
private val ElderCard = Color.White

private fun tr(lang: ElderLanguage, key: String): String {
    return when (lang) {
        ElderLanguage.ENGLISH -> when (key) {
            "menu" -> "Menu"
            "home" -> "Home"
            "medicines" -> "Medicines"
            "vitals" -> "Vitals"
            "contacts" -> "Contacts"
            "tap_to_call" -> "Tap to call"
            "entertainment" -> "Entertainment"
            "status_at_home" -> "Status: At home"
            "sos_hint" -> "Press and hold for emergency"
            "ott" -> "OTT"
            "tap_open" -> "Tap to open"
            "ott_home_title" -> "Watch & listen"
            "ott_home_sub" -> "Simple picks · Big taps"
            "voice_assistant" -> "Voice assistant"
            "voice_assistant_hint" -> "Say what you want — bhajan, news, or a movie"
            "cat_all" -> "All"
            "cat_movies" -> "Movies"
            "cat_music" -> "Music"
            "cat_spiritual" -> "Spiritual"
            "cat_news" -> "News"
            "cat_kids" -> "Kids"
            "recent_played" -> "Recently played"
            "recommended" -> "Recommended for you"
            "browse_apps" -> "Your apps"
            "bigtap_footer" -> "Large buttons · High contrast text"
            "plat_youtube" -> "YouTube"
            "plat_music" -> "Music"
            "plat_movies" -> "Movies"
            "plat_tv" -> "TV shows"
            "plat_news" -> "News"
            "plat_podcasts" -> "Podcasts"
            "plat_spiritual" -> "Spiritual & calm"
            "plat_kids" -> "Kids corner"
            "coming_soon" -> "Coming soon"
            "language" -> "Language"
            "take_medicine_title" -> "Take medicine"
            "todays_medicines" -> "Today's medicines"
            "start_taking" -> "Start taking"
            "medicine_of_fmt" -> "Medicine %1\$d of %2\$d"
            "did_you_take" -> "Did you take this medicine?"
            "not_taken" -> "Not taken"
            "taken" -> "Taken"
            "next_btn" -> "Next"
            "finish_btn" -> "Finish"
            "great_job" -> "Great job!"
            "completed_medicines" -> "You completed all medicines."
            "stats_taken_fmt" -> "Taken: %1\$d   Not taken: %2\$d"
            "watch_entertainment_btn" -> "Watch your favourite show or movie"
            "review_again" -> "Review again"
            "qty_fmt" -> "Qty %d"
            "elder_back" -> "Back"
            "sos_main" -> "SOS"
            "elder_app_title" -> "Care Companion"
            "weather_demo" -> "72°F · Pleasant skies"
            "daily_health_demo" -> "You're steady today · Sip water often"
            "health_chip" -> "Doing well"
            "med_preview_title" -> "Next medicine"
            "med_preview_demo" -> "Lorazepam · After lunch"
            "sos_reassurance" -> "Stay calm — help is arranged only after you confirm."
            "settings" -> "Settings"
            "drawer_section_health" -> "Health"
            "drawer_section_safety" -> "Safety"
            "drawer_section_entertainment" -> "Entertainment"
            "drawer_section_settings" -> "Settings"
            "drawer_you" -> "Signed in"
            "drawer_quick_sos" -> "Quick SOS"
            "drawer_quick_sos_sub" -> "Emergency · Opens alert flow"
            else -> key
        }
        ElderLanguage.HINDI -> when (key) {
            "menu" -> "मेन्यू"
            "home" -> "होम"
            "medicines" -> "दवाइयाँ"
            "vitals" -> "वाइटल्स"
            "contacts" -> "संपर्क"
            "tap_to_call" -> "कॉल करने के लिए टैप करें"
            "entertainment" -> "मनोरंजन"
            "status_at_home" -> "स्थिति: घर पर"
            "sos_hint" -> "आपातकाल के लिए दबाकर रखें"
            "ott" -> "ओटीटी"
            "tap_open" -> "खोलने के लिए टैप करें"
            "ott_home_title" -> "देखें और सुनें"
            "ott_home_sub" -> "आसान चुनाव · बड़े बटन"
            "voice_assistant" -> "आवाज़ सहायक"
            "voice_assistant_hint" -> "बोलें क्या चाहिए — भजन, समाचार या फ़िल्म"
            "cat_all" -> "सभी"
            "cat_movies" -> "फ़िल्में"
            "cat_music" -> "संगीत"
            "cat_spiritual" -> "आध्यात्मिक"
            "cat_news" -> "समाचार"
            "cat_kids" -> "बच्चे"
            "recent_played" -> "हाल में चला"
            "recommended" -> "आपके लिए"
            "browse_apps" -> "आपके ऐप"
            "bigtap_footer" -> "बड़े बटन · साफ़ टेक्स्ट"
            "plat_youtube" -> "YouTube"
            "plat_music" -> "संगीत"
            "plat_movies" -> "फ़िल्में"
            "plat_tv" -> "टीवी शो"
            "plat_news" -> "समाचार"
            "plat_podcasts" -> "पॉडकास्ट"
            "plat_spiritual" -> "भक्ति व शांति"
            "plat_kids" -> "बच्चों का कोना"
            "coming_soon" -> "जल्द आ रहा है"
            "language" -> "भाषा"
            "take_medicine_title" -> "दवा लें"
            "todays_medicines" -> "आज की दवाइयाँ"
            "start_taking" -> "लेना शुरू करें"
            "medicine_of_fmt" -> "दवा %1\$d में से %2\$d"
            "did_you_take" -> "क्या आपने यह दवा ली?"
            "not_taken" -> "नहीं ली"
            "taken" -> "ली"
            "next_btn" -> "आगे"
            "finish_btn" -> "समाप्त"
            "great_job" -> "बहुत अच्छा!"
            "completed_medicines" -> "आपने सभी दवाइयाँ पूरी कीं।"
            "stats_taken_fmt" -> "ली: %1\$d   नहीं ली: %2\$d"
            "watch_entertainment_btn" -> "अपना पसंदीदा शो या फ़िल्म देखें"
            "review_again" -> "फिर से देखें"
            "qty_fmt" -> "मात्रा %d"
            "elder_back" -> "पीछे"
            "sos_main" -> "SOS"
            "elder_app_title" -> "केयर कम्पैनियन"
            "weather_demo" -> "२२°C · हल्की धूप"
            "daily_health_demo" -> "आज स्थिर हैं · पानी पीते रहें"
            "health_chip" -> "अच्छा है"
            "med_preview_title" -> "अगली दवा"
            "med_preview_demo" -> "लोराज़ेपाम · दोपहर के बाद"
            "sos_reassurance" -> "शांत रहें — पुष्टि के बाद ही मदद भेजी जाती है।"
            "settings" -> "सेटिंग्स"
            "drawer_section_health" -> "स्वास्थ्य"
            "drawer_section_safety" -> "सुरक्षा"
            "drawer_section_entertainment" -> "मनोरंजन"
            "drawer_section_settings" -> "सेटिंग्स"
            "drawer_you" -> "लॉग इन"
            "drawer_quick_sos" -> "त्वरित SOS"
            "drawer_quick_sos_sub" -> "आपातकाल · अलर्ट फ्लो"
            else -> key
        }
        ElderLanguage.MARATHI -> when (key) {
            "menu" -> "मेन्यू"
            "home" -> "होम"
            "medicines" -> "औषधे"
            "vitals" -> "वाइटल्स"
            "contacts" -> "संपर्क"
            "tap_to_call" -> "कॉल करण्यासाठी टॅप करा"
            "entertainment" -> "मनोरंजन"
            "status_at_home" -> "स्थिती: घरी"
            "sos_hint" -> "आपत्कालीनसाठी दाबून ठेवा"
            "ott" -> "ओटीटी"
            "tap_open" -> "उघडण्यासाठी टॅप करा"
            "ott_home_title" -> "पहा आणि ऐका"
            "ott_home_sub" -> "सोपी निवड · मोठी बटणे"
            "voice_assistant" -> "आवाजातील मदत"
            "voice_assistant_hint" -> "बोला काय हवं आहे — भजन, बातम्या किंवा चित्रपट"
            "cat_all" -> "सर्व"
            "cat_movies" -> "चित्रपट"
            "cat_music" -> "संगीत"
            "cat_spiritual" -> "आध्यात्मिक"
            "cat_news" -> "बातम्या"
            "cat_kids" -> "मुले"
            "recent_played" -> "अलीकडे प्ले केले"
            "recommended" -> "तुमच्यासाठी"
            "browse_apps" -> "तुमचे अ‍ॅप्स"
            "bigtap_footer" -> "मोठी बटणे · स्पष्ट मजकूर"
            "plat_youtube" -> "YouTube"
            "plat_music" -> "संगीत"
            "plat_movies" -> "चित्रपट"
            "plat_tv" -> "टीव्ही शो"
            "plat_news" -> "बातम्या"
            "plat_podcasts" -> "पॉडकास्ट"
            "plat_spiritual" -> "भक्ती आणि शांती"
            "plat_kids" -> "मुलांचा कोना"
            "coming_soon" -> "लवकरच येत आहे"
            "language" -> "भाषा"
            "take_medicine_title" -> "औषध घ्या"
            "todays_medicines" -> "आजची औषधे"
            "start_taking" -> "सुरू करा"
            "medicine_of_fmt" -> "औषध %1\$d पैकी %2\$d"
            "did_you_take" -> "तुम्ही हे औषध घेतले?"
            "not_taken" -> "नाही घेतले"
            "taken" -> "घेतले"
            "next_btn" -> "पुढे"
            "finish_btn" -> "संपले"
            "great_job" -> "छान!"
            "completed_medicines" -> "सर्व औषधे पूर्ण झाली."
            "stats_taken_fmt" -> "घेतले: %1\$d   नाही: %2\$d"
            "watch_entertainment_btn" -> "आवडता शो किंवा चित्रपट पहा"
            "review_again" -> "पुन्हा पहा"
            "qty_fmt" -> "प्रमाण %d"
            "elder_back" -> "मागे"
            "sos_main" -> "SOS"
            "elder_app_title" -> "केअर कम्पॅनियन"
            "weather_demo" -> "२२°C · हलके ढगाळ"
            "daily_health_demo" -> "आज स्थिर आहात · पाणी घ्या"
            "health_chip" -> "चांगले आहे"
            "med_preview_title" -> "पुढील औषध"
            "med_preview_demo" -> "लोराज़ेपाम · दुपारीनंतर"
            "sos_reassurance" -> "शांत राहा — तुम्ही खात्री केल्यावरच मदत येते."
            "settings" -> "सेटिंग्ज"
            "drawer_section_health" -> "आरोग्य"
            "drawer_section_safety" -> "सुरक्षा"
            "drawer_section_entertainment" -> "मनोरंजन"
            "drawer_section_settings" -> "सेटिंग्ज"
            "drawer_you" -> "साइन इन"
            "drawer_quick_sos" -> "झटपट SOS"
            "drawer_quick_sos_sub" -> "आपत्काल · सूचना"
            else -> key
        }
        ElderLanguage.GUJARATI -> when (key) {
            "menu" -> "મેનુ"
            "home" -> "હોમ"
            "medicines" -> "દવાઓ"
            "vitals" -> "વાઇટલ્સ"
            "contacts" -> "સંપર્કો"
            "tap_to_call" -> "કૉલ માટે ટેપ કરો"
            "entertainment" -> "મનોરંજન"
            "status_at_home" -> "સ્થિતિ: ઘરે"
            "sos_hint" -> "આપત્કાલ માટે દબાવી રાખો"
            "ott" -> "ઓટીટી"
            "tap_open" -> "ખોલવા માટે ટેપ કરો"
            "ott_home_title" -> "જુઓ અને સાંભળો"
            "ott_home_sub" -> "સરળ પસંદ · મોટા બટનો"
            "voice_assistant" -> "વૉઇસ સહાયક"
            "voice_assistant_hint" -> "કહો શું જોઈએ — ભજન, સમાચાર અથવા ફિલ્મ"
            "cat_all" -> "બધું"
            "cat_movies" -> "ફિલ્મો"
            "cat_music" -> "સંગીત"
            "cat_spiritual" -> "આધ્યાત્મિક"
            "cat_news" -> "સમાચાર"
            "cat_kids" -> "બાળકો"
            "recent_played" -> "હમણાં જોયું"
            "recommended" -> "તમારા માટે"
            "browse_apps" -> "તમારી એપ્સ"
            "bigtap_footer" -> "મોટા બટનો · સ્પષ્ટ લખાણ"
            "plat_youtube" -> "YouTube"
            "plat_music" -> "સંગીત"
            "plat_movies" -> "ફિલ્મો"
            "plat_tv" -> "ટીવી શો"
            "plat_news" -> "સમાચાર"
            "plat_podcasts" -> "પોડકાસ્ટ"
            "plat_spiritual" -> "ભક્તિ અને શાંતિ"
            "plat_kids" -> "બાળકો માટે"
            "coming_soon" -> "જલ્દી આવશે"
            "language" -> "ભાષા"
            "take_medicine_title" -> "દવા લો"
            "todays_medicines" -> "આજની દવાઓ"
            "start_taking" -> "શરૂ કરો"
            "medicine_of_fmt" -> "દવા %1\$d માંથી %2\$d"
            "did_you_take" -> "શું તમે આ દવા લીધી?"
            "not_taken" -> "નથી લીધી"
            "taken" -> "લીધી"
            "next_btn" -> "આગળ"
            "finish_btn" -> "પૂર્ણ"
            "great_job" -> "શાબાશ!"
            "completed_medicines" -> "તમે બધી દવાઓ પૂરી કરી."
            "stats_taken_fmt" -> "લીધી: %1\$d   નહીં: %2\$d"
            "watch_entertainment_btn" -> "મનપસંદ શો અથવા ફિલ્મ જુઓ"
            "review_again" -> "ફરી જુઓ"
            "qty_fmt" -> "જથ્થો %d"
            "elder_back" -> "પાછા"
            "sos_main" -> "SOS"
            "elder_app_title" -> "કેઅર કમ્પેનિયન"
            "weather_demo" -> "२२°C · હલકું વાદળી"
            "daily_health_demo" -> "આજે સ્થિતિસ્થાપક · પાણી પીજો"
            "health_chip" -> "સારું છે"
            "med_preview_title" -> "આગલી દવા"
            "med_preview_demo" -> "લોરાઝેપામ · બપોર પછી"
            "sos_reassurance" -> "શાંત રહો — તમારી પુષ્ટિ પછી જ મદદ મોકલાશે."
            "settings" -> "સેટિંગ્સ"
            "drawer_section_health" -> "આરોગ્ય"
            "drawer_section_safety" -> "સલામતી"
            "drawer_section_entertainment" -> "મનોરંજન"
            "drawer_section_settings" -> "સેટિંગ્સ"
            "drawer_you" -> "સાઇન ઇન"
            "drawer_quick_sos" -> "ઝડપી SOS"
            "drawer_quick_sos_sub" -> "કટોકટી · ચેતવણી"
            else -> key
        }
    }
}

// Screens reachable from the drawer / action cards
sealed class ElderDestination {
    object Home        : ElderDestination()
    object Medicines   : ElderDestination()
    object Vitals      : ElderDestination()
    object Contacts    : ElderDestination()
    object Entertainment : ElderDestination()
    object Settings    : ElderDestination()
}

private fun contactInitials(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase(Locale.getDefault())
        else ->
            "${parts[0].first()}${parts[1].first()}".uppercase(Locale.getDefault())
    }
}

private fun dialString(raw: String): String =
    raw.trim().filter { it.isDigit() || it == '+' }

private fun formatContactPhone(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return "—"
    val digits = trimmed.filter { it.isDigit() }
    return when {
        digits.length == 10 ->
            "(${digits.take(3)}) ${digits.drop(3).take(3)}-${digits.drop(6)}"
        digits.length == 11 && digits.startsWith("1") -> {
            val d = digits.drop(1)
            "+1 (${d.take(3)}) ${d.drop(3).take(3)}-${d.drop(6)}"
        }
        else -> trimmed
    }
}

private data class ContactPerson(
    val name: String,
    val phone: String,
    val avatarBg: Color,
)

private enum class OttCategory {
    MOVIES,
    MUSIC,
    SPIRITUAL,
    NEWS,
    KIDS;

    fun trKey(): String =
        when (this) {
            MOVIES -> "cat_movies"
            MUSIC -> "cat_music"
            SPIRITUAL -> "cat_spiritual"
            NEWS -> "cat_news"
            KIDS -> "cat_kids"
        }
}

private data class OttPlatform(
    val id: String,
    val labelKey: String,
    val icon: ImageVector,
    val thumbBrush: Brush,
    val thumbIconTint: Color,
    val category: OttCategory,
)

private fun ottCatalog(): List<OttPlatform> =
    listOf(
        OttPlatform(
            id = "yt",
            labelKey = "plat_youtube",
            icon = Icons.Outlined.PlayCircle,
            thumbBrush =
                Brush.linearGradient(
                    listOf(Color(0xFFFF0844), Color(0xFFFFB199)),
                ),
            thumbIconTint = Color.White,
            category = OttCategory.MOVIES,
        ),
        OttPlatform(
            id = "music",
            labelKey = "plat_music",
            icon = Icons.Outlined.LibraryMusic,
            thumbBrush =
                Brush.linearGradient(
                    listOf(Color(0xFF1DB954), Color(0xFF169C46)),
                ),
            thumbIconTint = Color.White,
            category = OttCategory.MUSIC,
        ),
        OttPlatform(
            id = "movies",
            labelKey = "plat_movies",
            icon = Icons.Outlined.Movie,
            thumbBrush =
                Brush.linearGradient(
                    listOf(Color(0xFF0F0F0F), Color(0xFFE50914)),
                ),
            thumbIconTint = Color.White,
            category = OttCategory.MOVIES,
        ),
        OttPlatform(
            id = "tv",
            labelKey = "plat_tv",
            icon = Icons.Outlined.LiveTv,
            thumbBrush =
                Brush.linearGradient(
                    listOf(Color(0xFF452B7A), Color(0xFFAB47BC)),
                ),
            thumbIconTint = Color.White,
            category = OttCategory.MOVIES,
        ),
        OttPlatform(
            id = "news",
            labelKey = "plat_news",
            icon = Icons.Outlined.Newspaper,
            thumbBrush =
                Brush.linearGradient(
                    listOf(Color(0xFF0D47A1), Color(0xFF42A5F5)),
                ),
            thumbIconTint = Color.White,
            category = OttCategory.NEWS,
        ),
        OttPlatform(
            id = "podcasts",
            labelKey = "plat_podcasts",
            icon = Icons.Outlined.Podcasts,
            thumbBrush =
                Brush.linearGradient(
                    listOf(Color(0xFF673AB7), Color(0xFFB39DDB)),
                ),
            thumbIconTint = Color.White,
            category = OttCategory.MUSIC,
        ),
        OttPlatform(
            id = "spiritual",
            labelKey = "plat_spiritual",
            icon = Icons.Outlined.Spa,
            thumbBrush =
                Brush.linearGradient(
                    listOf(Color(0xFFFF8F00), Color(0xFFFFF176)),
                ),
            thumbIconTint = Color(0xFF4E342E),
            category = OttCategory.SPIRITUAL,
        ),
        OttPlatform(
            id = "kids",
            labelKey = "plat_kids",
            icon = Icons.Outlined.ChildCare,
            thumbBrush =
                Brush.linearGradient(
                    listOf(Color(0xFF0097A7), Color(0xFF4DD0E1)),
                ),
            thumbIconTint = Color.White,
            category = OttCategory.KIDS,
        ),
    )

private data class MedicineItem(
    val name: String,
    val quantity: Int,
    val timeInstruction: String,
    val withInstruction: String,
    val icon: ImageVector,
    val color: Color,
    val photoRes: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElderHomeScreen(
    elderName: String = "Sunita",
    onSosPressed: () -> Unit = {},
    onLogout: () -> Unit = {},
    elderContacts: List<ManagedContact> = emptyList()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()
    var destination by remember { mutableStateOf<ElderDestination>(ElderDestination.Home) }
    var language by remember { mutableStateOf(ElderLanguage.ENGLISH) }

    CompositionLocalProvider(LocalElderLanguage provides language) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ElderDrawer(
                    elderName = elderName,
                    drawerState = drawerState,
                    current = destination,
                    onSosQuick = {
                        onSosPressed()
                        scope.launch { drawerState.close() }
                    },
                    onSelect = { dest ->
                        destination = dest
                        scope.launch { drawerState.close() }
                    },
                )
            },
            scrimColor = Color.Black.copy(alpha = 0.3f)
        ) {
            Scaffold(
                containerColor = ElderPageBg,
                topBar = {
                    ElderTopBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onLanguageSelected = { language = it },
                        onLogout = onLogout
                    )
                }
            ) { padding ->
                when (destination) {
                    ElderDestination.Home        -> ElderHomePage(padding, elderName, onSosPressed) { destination = it }
                    ElderDestination.Medicines   -> MedicinesScreen(padding, onSosPressed) { destination = it }
                    ElderDestination.Vitals      -> VitalsScreen(padding, onSosPressed) { destination = it }
                    ElderDestination.Contacts    -> ContactsScreen(padding, onSosPressed, elderContacts) { destination = it }
                    ElderDestination.Entertainment -> EntertainmentScreen(padding, onSosPressed) { destination = it }
                    ElderDestination.Settings ->
                        StubScreen(
                            padding = padding,
                            title = tr(LocalElderLanguage.current, "settings"),
                            icon = Icons.Outlined.Settings,
                            color = ElderNavy,
                            onSosPressed = onSosPressed,
                            current = ElderDestination.Settings,
                            onNavigate = { destination = it },
                        )
                }
            }
        }
    }
}

// ── Top bar ─────────────────────────────────────────────────────────
@Composable
private fun ElderTopBar(
    onMenuClick: () -> Unit,
    onLanguageSelected: (ElderLanguage) -> Unit,
    onLogout: () -> Unit,
) {
    val lang = LocalElderLanguage.current
    val ctx = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        color = ElderCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 6.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(52.dp),
            ) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = tr(lang, "menu"),
                    tint = ElderNavy,
                    modifier = Modifier.size(28.dp),
                )
            }

            Text(
                text = tr(lang, "elder_app_title"),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                fontSize = 16.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Bold,
                color = ElderNavy,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )

            Box {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        Icons.Outlined.Translate,
                        contentDescription = tr(lang, "language"),
                        tint = ElderSoftBlue,
                        modifier = Modifier.size(26.dp),
                    )
                }
                CareGlassLanguageDropdown(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    selected = lang,
                    onSelect = onLanguageSelected,
                    menuTitle = tr(lang, "language"),
                )
            }

            IconButton(
                onClick = {
                    Toast.makeText(
                        ctx,
                        "No new alerts right now.",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = ElderNavy.copy(alpha = 0.75f),
                    modifier = Modifier.size(26.dp),
                )
            }

            IconButton(
                onClick = onLogout,
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    Icons.Outlined.Logout,
                    contentDescription = "Logout",
                    tint = ElderEmergency,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
    }
}

// ── Side drawer ──────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ElderDrawer(
    elderName: String,
    drawerState: DrawerState,
    current: ElderDestination,
    onSosQuick: () -> Unit,
    onSelect: (ElderDestination) -> Unit,
) {
    val lang = LocalElderLanguage.current
    val drawerMotionOpen by remember {
        derivedStateOf {
            drawerState.currentValue == DrawerValue.Open ||
                drawerState.targetValue == DrawerValue.Open
        }
    }

    ModalDrawerSheet(
        modifier = Modifier.width(304.dp),
        drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
        drawerContainerColor = Color(0xFFF4F6FB),
        drawerTonalElevation = 4.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
        ) {
            DrawerProfileHeader(elderName = elderName, lang = lang)

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp),
            ) {
                AnimatedVisibility(
                    visible = drawerMotionOpen,
                    enter =
                        fadeIn(animationSpec = tween(280, delayMillis = 40)) +
                            slideInHorizontally(
                                animationSpec = tween(340, delayMillis = 40),
                                initialOffsetX = { -it / 5 },
                            ),
                    exit = fadeOut(tween(120)),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        DrawerSectionHeader(title = tr(lang, "drawer_section_health"))
                        DrawerNavRow(
                            icon = Icons.Outlined.Home,
                            label = tr(lang, "home"),
                            selected = current == ElderDestination.Home,
                            onClick = { onSelect(ElderDestination.Home) },
                        )
                        DrawerNavRow(
                            icon = Icons.Outlined.MedicalServices,
                            label = tr(lang, "medicines"),
                            selected = current == ElderDestination.Medicines,
                            onClick = { onSelect(ElderDestination.Medicines) },
                        )
                        DrawerNavRow(
                            icon = Icons.Outlined.MonitorHeart,
                            label = tr(lang, "vitals"),
                            selected = current == ElderDestination.Vitals,
                            onClick = { onSelect(ElderDestination.Vitals) },
                        )

                        Spacer(Modifier.height(10.dp))

                        DrawerSectionHeader(title = tr(lang, "drawer_section_safety"))
                        DrawerNavRow(
                            icon = Icons.Outlined.Shield,
                            label = tr(lang, "contacts"),
                            selected = current == ElderDestination.Contacts,
                            onClick = { onSelect(ElderDestination.Contacts) },
                        )

                        Spacer(Modifier.height(10.dp))

                        DrawerSectionHeader(title = tr(lang, "drawer_section_entertainment"))
                        DrawerNavRow(
                            icon = Icons.Outlined.Movie,
                            label = tr(lang, "entertainment"),
                            selected = current == ElderDestination.Entertainment,
                            onClick = { onSelect(ElderDestination.Entertainment) },
                        )

                        Spacer(Modifier.height(10.dp))

                        DrawerSectionHeader(title = tr(lang, "drawer_section_settings"))
                        DrawerNavRow(
                            icon = Icons.Outlined.Settings,
                            label = tr(lang, "settings"),
                            selected = current == ElderDestination.Settings,
                            onClick = { onSelect(ElderDestination.Settings) },
                        )

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            DrawerQuickSosFooter(
                lang = lang,
                onClick = onSosQuick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun DrawerProfileHeader(
    elderName: String,
    lang: ElderLanguage,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                ElderNavy,
                                Color(0xFF1E3A5F),
                                ElderSoftBlue.copy(alpha = 0.92f),
                            ),
                    ),
                )
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 22.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.15f),
                    shadowElevation = 9.dp,
                    modifier = Modifier.size(72.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color.White, Color(0xFFE3F2FD)),
                                    ),
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.Elderly,
                            contentDescription = null,
                            tint = ElderNavy,
                            modifier = Modifier.size(38.dp),
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tr(lang, "drawer_you"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.78f),
                    )
                    Text(
                        text = elderName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                shadowElevation = 5.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(ElderMint),
                    )
                    Icon(
                        Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(22.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tr(lang, "health_chip"),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Text(
                            text = tr(lang, "status_at_home"),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.82f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title.uppercase(Locale.getDefault()),
        modifier =
            Modifier
                .padding(start = 8.dp, top = 12.dp, bottom = 6.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = ElderNavy.copy(alpha = 0.45f),
    )
}

@Composable
private fun DrawerNavRow(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pillShape = RoundedCornerShape(16.dp)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .shadow(
                    elevation = if (selected) 8.dp else 2.dp,
                    shape = pillShape,
                    ambientColor = Color.Black.copy(alpha = if (selected) 0.14f else 0.06f),
                    spotColor = ElderSoftBlue.copy(alpha = if (selected) 0.24f else 0.1f),
                )
                .clip(pillShape)
                .background(
                    if (selected) {
                        Brush.horizontalGradient(
                            listOf(
                                ElderSoftBlue.copy(alpha = 0.22f),
                                ElderMint.copy(alpha = 0.18f),
                                Color.White.copy(alpha = 0.92f),
                            ),
                        )
                    } else {
                        Brush.horizontalGradient(listOf(Color.White, Color(0xFFFDFEFE)))
                    },
                )
                .border(
                    width = 1.dp,
                    color =
                        if (selected) ElderSoftBlue.copy(alpha = 0.38f)
                        else Color(0x14000000),
                    shape = pillShape,
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        if (selected) ElderNavy.copy(alpha = 0.12f)
                        else Color(0xFFF1F5F9),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) ElderNavy else ElderNavy.copy(alpha = 0.72f),
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (selected) ElderNavy else ElderNavy.copy(alpha = 0.88f),
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Box(
                modifier =
                    Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(ElderMint),
            )
        }
    }
}

@Composable
private fun DrawerQuickSosFooter(
    lang: ElderLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier =
            modifier
                .shadow(
                    elevation = 12.dp,
                    shape = shape,
                    ambientColor = ElderEmergency.copy(alpha = 0.35f),
                    spotColor = ElderEmergency.copy(alpha = 0.45f),
                )
                .clip(shape)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFF5252), ElderEmergency, Color(0xFFD50000)),
                    ),
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.Emergency,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tr(lang, "drawer_quick_sos"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                text = tr(lang, "drawer_quick_sos_sub"),
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.88f),
            )
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(26.dp),
        )
    }
}

// ── Home page body ───────────────────────────────────────────────────
@Composable
private fun ElderHomePage(
    padding: PaddingValues,
    elderName: String,
    onSosPressed: () -> Unit,
    navigate: (ElderDestination) -> Unit,
) {
    val lang = LocalElderLanguage.current
    val dateStr = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            WeatherDateBanner(
                dateLabel = dateStr,
                weather = tr(lang, "weather_demo"),
            )
        }
        item {
            ProfileSummaryCard(
                name = elderName,
                healthChip = tr(lang, "health_chip"),
                locationLine = tr(lang, "status_at_home"),
            )
        }
        item {
            DailyHealthSummaryCard(text = tr(lang, "daily_health_demo"))
        }
        item {
            MedicationReminderPreviewCard(
                title = tr(lang, "med_preview_title"),
                detail = tr(lang, "med_preview_demo"),
                onOpen = { navigate(ElderDestination.Medicines) },
            )
        }
        item {
            SosButton(onSosPressed)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ActionCard(
                    icon = Icons.Outlined.Groups,
                    label = tr(lang, "contacts"),
                    tint = Color(0xFF0D47A1),
                    bg = Color(0xFFE3F2FD),
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.82f),
                    onClick = { navigate(ElderDestination.Contacts) },
                )
                ActionCard(
                    icon = Icons.Outlined.Movie,
                    label = tr(lang, "entertainment"),
                    tint = Color(0xFF4A148C),
                    bg = Color(0xFFF3E5F5),
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.82f),
                    onClick = { navigate(ElderDestination.Entertainment) },
                )
            }
        }
    }
}

// ── Home widgets ─────────────────────────────────────────────────────
@Composable
private fun WeatherDateBanner(
    dateLabel: String,
    weather: String,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = ElderSoftBlue.copy(alpha = 0.14f)),
        shape = RoundedCornerShape(24.dp),
        color = ElderCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.85f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElderSoftBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = ElderSoftBlue,
                    modifier = Modifier.size(28.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateLabel,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElderNavy,
                    maxLines = 2,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = weather,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = ElderNavy.copy(alpha = 0.65f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                Icons.Outlined.WbSunny,
                contentDescription = null,
                tint = Color(0xFFFFA726),
                modifier = Modifier.size(38.dp),
            )
        }
    }
}

@Composable
private fun DailyHealthSummaryCard(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = ElderMint.copy(alpha = 0.14f)),
        shape = RoundedCornerShape(22.dp),
        color = ElderCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, ElderMint.copy(alpha = 0.22f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ElderMint.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.MonitorHeart,
                    contentDescription = null,
                    tint = ElderMint,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = ElderNavy.copy(alpha = 0.88f),
                lineHeight = 22.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MedicationReminderPreviewCard(
    title: String,
    detail: String,
    onOpen: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = ElderCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onOpen)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF8E1)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Medication,
                    contentDescription = null,
                    tint = Color(0xFF795548),
                    modifier = Modifier.size(30.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElderNavy.copy(alpha = 0.55f),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = detail,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElderNavy,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                Icons.Outlined.NavigateNext,
                contentDescription = null,
                tint = ElderSoftBlue,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
private fun ProfileSummaryCard(
    name: String,
    healthChip: String,
    locationLine: String,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(28.dp), spotColor = ElderSoftBlue.copy(alpha = 0.16f)),
        shape = RoundedCornerShape(28.dp),
        color = ElderCard,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            ElderSoftBlue.copy(alpha = 0.16f),
                            ElderMint.copy(alpha = 0.1f),
                            Color.White,
                        ),
                    ),
                )
                .padding(horizontal = 18.dp, vertical = 20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .shadow(10.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(ElderSoftBlue, ElderNavy),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Elderly,
                        contentDescription = name,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElderNavy,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ElderMint.copy(alpha = 0.22f),
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(ElderMint),
                                )
                                Text(
                                    text = healthChip,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElderNavy,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false),
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ElderSoftBlue.copy(alpha = 0.14f),
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp,
                        ) {
                            Text(
                                text = locationLine,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElderNavy.copy(alpha = 0.78f),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── SOS button ───────────────────────────────────────────────────────
@Composable
private fun SosButton(onClick: () -> Unit) {
    val lang = LocalElderLanguage.current
    val pulse = rememberInfiniteTransition(label = "sosGlow")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.045f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 950),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sosPulseScale",
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(188.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                        alpha = 0.35f
                    }
                    .clip(RoundedCornerShape(28.dp))
                    .background(ElderEmergency.copy(alpha = 0.55f)),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(172.dp)
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = ElderEmergency.copy(alpha = 0.35f),
                        spotColor = ElderEmergency.copy(alpha = 0.55f),
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF5252), Color(0xFFD50000), Color(0xFFB71C1C)),
                        ),
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = { onClick() })
                    }
                    .semantics {
                        contentDescription = "${tr(lang, "sos_main")}. ${tr(lang, "sos_hint")}"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            Icons.Outlined.HealthAndSafety,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = tr(lang, "sos_main"),
                            modifier = Modifier.weight(1f),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 4.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = tr(lang, "sos_hint"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.96f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = tr(lang, "sos_reassurance"),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

// ── Action card (Contacts / Entertainment) ───────────────────────────
@Composable
private fun ActionCard(
    icon: ImageVector,
    label: String,
    tint: Color,
    bg: Color,
    modifier: Modifier = Modifier,
    labelFontSize: TextUnit = 20.sp,
    iconBoxDp: Dp = 88.dp,
    iconDp: Dp = 48.dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = ElderNavy.copy(alpha = 0.07f),
                spotColor = ElderSoftBlue.copy(alpha = 0.14f),
            )
            .clip(RoundedCornerShape(26.dp))
            .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color.White, ElderPageBg.copy(alpha = 0.45f)),
                ),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(iconBoxDp)
                        .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = tint.copy(alpha = 0.14f))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(bg.copy(alpha = 0.98f), bg.copy(alpha = 0.72f)),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        icon,
                        contentDescription = label,
                        tint = tint,
                        modifier = Modifier.size(iconDp),
                    )
                }
            }
            Text(
                text = label,
                fontSize = labelFontSize,
                fontWeight = FontWeight.Bold,
                color = ElderNavy,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ── Stub sub-screens ─────────────────────────────────────────────────
@Composable
fun MedicinesScreen(
    padding: PaddingValues,
    onSosPressed: () -> Unit,
    onNavigate: (ElderDestination) -> Unit
) {
    val medicines = remember {
        listOf(
            MedicineItem(
                name = "Buprenorphine/Naloxone Strip",
                quantity = 1,
                timeInstruction = "Before Lunch",
                withInstruction = "With Water",
                icon = Icons.Outlined.Medication,
                color = Color(0xFFFFF8E1),
                photoRes = R.drawable.med_strip_1
            ),
            MedicineItem(
                name = "Lorazepam 3mg",
                quantity = 1,
                timeInstruction = "After Lunch",
                withInstruction = "With Water",
                icon = Icons.Outlined.LocalPharmacy,
                color = Color(0xFFE3F2FD),
                photoRes = R.drawable.med_strip_2
            ),
            MedicineItem(
                name = "Alprazolam ODT",
                quantity = 2,
                timeInstruction = "After Dinner",
                withInstruction = "With Milk",
                icon = Icons.Outlined.Vaccines,
                color = Color(0xFFF3E5F5),
                photoRes = R.drawable.med_strip_3
            )
        )
    }

    var step by rememberSaveable { mutableStateOf(0) } // 0=list, 1=details, 2=done
    var index by rememberSaveable { mutableStateOf(0) }
    var tookCurrent by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var takenCount by rememberSaveable { mutableStateOf(0) }
    var skippedCount by rememberSaveable { mutableStateOf(0) }
    val lang = LocalElderLanguage.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onNavigate(ElderDestination.Home) },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = tr(lang, "elder_back"),
                    tint = Color(0xFF404040)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = tr(lang, "take_medicine_title"),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1C)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(6.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(0.06f))
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .padding(12.dp)
        ) {
            when (step) {
                0 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = tr(lang, "todays_medicines"),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2A2A2A)
                        )
                        medicines.forEachIndexed { i, med ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, ElderSoftBlue.copy(alpha = 0.16f)),
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        med.color.copy(alpha = 0.95f),
                                                        med.color.copy(alpha = 0.65f),
                                                    ),
                                                ),
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = med.icon,
                                            contentDescription = med.name,
                                            tint = ElderNavy.copy(alpha = 0.82f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${i + 1}. ${med.name}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF2E2E2E),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 22.sp,
                                        )
                                        Text(
                                            text = "${med.timeInstruction} · ${med.withInstruction}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF707990),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                    Text(
                                        text = String.format(Locale.getDefault(), tr(lang, "qty_fmt"), med.quantity),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ElderSoftBlue,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                index = 0
                                tookCurrent = null
                                takenCount = 0
                                skippedCount = 0
                                step = 1
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElderSoftBlue,
                                contentColor = Color.White,
                            )
                        ) {
                            Text(tr(lang, "start_taking"), fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                1 -> {
                    AnimatedContent(
                        targetState = index,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(260)) togetherWith
                                fadeOut(animationSpec = tween(220))
                        },
                        label = "medicine_slide"
                    ) { animatedIndex ->
                        val med = medicines[animatedIndex]
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = String.format(
                                    Locale.getDefault(),
                                    tr(lang, "medicine_of_fmt"),
                                    animatedIndex + 1,
                                    medicines.size
                                ),
                                fontSize = 20.sp,
                                color = Color(0xFF666666)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .shadow(2.dp, RoundedCornerShape(16.dp))
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(med.color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (med.photoRes != null) {
                                        Image(
                                            painter = painterResource(id = med.photoRes),
                                            contentDescription = med.name,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = med.icon,
                                            contentDescription = med.name,
                                            tint = Color(0xFF5B4300),
                                            modifier = Modifier.size(96.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = med.name,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1F1F),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 34.sp,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                AssistChip(
                                    onClick = { },
                                    enabled = false,
                                    label = {
                                        Text(
                                            String.format(Locale.getDefault(), tr(lang, "qty_fmt"), med.quantity),
                                            fontSize = 18.sp
                                        )
                                    }
                                )
                                AssistChip(
                                    onClick = { },
                                    enabled = false,
                                    label = {
                                        Text(
                                            med.timeInstruction,
                                            fontSize = 18.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                )
                            }
                            AssistChip(
                                onClick = { },
                                enabled = false,
                                label = {
                                    Text(
                                        med.withInstruction,
                                        fontSize = 18.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = tr(lang, "did_you_take"),
                                fontSize = 21.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2A2A2A)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { tookCurrent = false },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (tookCurrent == false) Color(0xFFD32F2F) else Color(0xFFFDECEC),
                                        contentColor = if (tookCurrent == false) Color.White else Color(0xFFD32F2F)
                                    )
                                ) {
                                    Text(tr(lang, "not_taken"), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Button(
                                    onClick = { tookCurrent = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (tookCurrent == true) Color(0xFF2E7D32) else Color(0xFFEAF6EC),
                                        contentColor = if (tookCurrent == true) Color.White else Color(0xFF2E7D32)
                                    )
                                ) {
                                    Text(tr(lang, "taken"), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    if (tookCurrent == true) takenCount += 1 else skippedCount += 1
                                    if (index < medicines.lastIndex) {
                                        index += 1
                                        tookCurrent = null
                                    } else {
                                        step = 2
                                    }
                                },
                                enabled = tookCurrent != null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElderSoftBlue,
                                    contentColor = Color.White,
                                )
                            ) {
                                Text(
                                    text = if (animatedIndex < medicines.lastIndex) {
                                        tr(lang, "next_btn")
                                    } else {
                                        tr(lang, "finish_btn")
                                    },
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                else -> {
                    val confetti = rememberInfiniteTransition(label = "confetti")
                    val bounce by confetti.animateFloat(
                        initialValue = 0f,
                        targetValue = 14f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(900),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "celebrate_bounce"
                    )
                    val particleA by confetti.animateFloat(
                        initialValue = -8f,
                        targetValue = 42f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1400),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "particle_a"
                    )
                    val particleB by confetti.animateFloat(
                        initialValue = -20f,
                        targetValue = 48f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "particle_b"
                    )
                    val particleC by confetti.animateFloat(
                        initialValue = -14f,
                        targetValue = 50f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1600),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "particle_c"
                    )
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF3CD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .graphicsLayer {
                                        translationX = -42f
                                        translationY = particleA
                                    }
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF6F61))
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .graphicsLayer {
                                        translationX = 46f
                                        translationY = particleB
                                    }
                                    .clip(CircleShape)
                                    .background(Color(0xFF42A5F5))
                            )
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .graphicsLayer {
                                        translationX = 0f
                                        translationY = particleC
                                    }
                                    .clip(CircleShape)
                                    .background(Color(0xFF66BB6A))
                            )
                            Icon(
                                imageVector = Icons.Outlined.SentimentVerySatisfied,
                                contentDescription = "Done",
                                tint = Color(0xFFE6A800),
                                modifier = Modifier
                                    .size(76.dp)
                                    .scale(1f + (bounce / 150f))
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = tr(lang, "great_job"),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF222222)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = tr(lang, "completed_medicines"),
                            fontSize = 20.sp,
                            color = Color(0xFF555555)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = String.format(
                                Locale.getDefault(),
                                tr(lang, "stats_taken_fmt"),
                                takenCount,
                                skippedCount
                            ),
                            fontSize = 18.sp,
                            color = Color(0xFF4A4A4A)
                        )
                        Spacer(Modifier.height(26.dp))
                        Button(
                            onClick = { onNavigate(ElderDestination.Entertainment) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2))
                        ) {
                            Text(tr(lang, "watch_entertainment_btn"), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                step = 0
                                index = 0
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(tr(lang, "review_again"), fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VitalsScreen(
    padding: PaddingValues,
    onSosPressed: () -> Unit,
    onNavigate: (ElderDestination) -> Unit
) = StubScreen(
    padding = padding,
    title = tr(LocalElderLanguage.current, "vitals"),
    icon = Icons.Outlined.MonitorHeart,
    color = Color(0xFFC62828),
    onSosPressed = onSosPressed,
    current = ElderDestination.Vitals,
    onNavigate = onNavigate
)

@Composable
fun ContactsScreen(
    padding: PaddingValues,
    onSosPressed: () -> Unit,
    elderContacts: List<ManagedContact> = emptyList(),
    onNavigate: (ElderDestination) -> Unit
) = ContactListScreen(padding, onSosPressed, elderContacts, onNavigate)

@Composable
fun EntertainmentScreen(
    padding: PaddingValues,
    onSosPressed: () -> Unit,
    onNavigate: (ElderDestination) -> Unit
) = EntertainmentListScreen(
    padding = padding,
    onNavigate = onNavigate
)

@Composable
private fun ContactListScreen(
    padding: PaddingValues,
    onSosPressed: () -> Unit,
    elderContacts: List<ManagedContact>,
    onNavigate: (ElderDestination) -> Unit
) {
    val lang = LocalElderLanguage.current
    val ctx = LocalContext.current
    val contacts = remember(elderContacts) {
        if (elderContacts.isNotEmpty()) {
            elderContacts.mapIndexed { i, c ->
                val colors = listOf(
                    Color(0xFFE3F2FD), Color(0xFFFCE4EC), Color(0xFFE8F5E9),
                    Color(0xFFF3E5F5), Color(0xFFFFF3E0), Color(0xFFE0F2F1)
                )
                ContactPerson(c.name, c.phone.ifBlank { "" }, colors[i % colors.size])
            }
        } else {
            listOf(
                ContactPerson("Aarav", "+15550101", Color(0xFFE3F2FD)),
                ContactPerson("Riya", "+15550102", Color(0xFFFCE4EC)),
                ContactPerson("Meera", "+15550103", Color(0xFFE8F5E9)),
                ContactPerson("Rahul", "+15550104", Color(0xFFF3E5F5)),
            )
        }
    }

    fun dialPerson(person: ContactPerson) {
        val n = dialString(person.phone)
        if (n.isNotEmpty()) {
            ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$n")))
        }
    }
    val contactRows = remember(contacts) { contacts.chunked(2) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onNavigate(ElderDestination.Home) },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = tr(lang, "elder_back"),
                    tint = Color(0xFF404040)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = tr(lang, "contacts"),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1C)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(6.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black.copy(0.06f))
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(contactRows) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ContactCard(
                            person = row[0],
                            modifier = Modifier.weight(1f),
                            onCallClick = { dialPerson(row[0]) },
                        )
                        if (row.size > 1) {
                            ContactCard(
                                person = row[1],
                                modifier = Modifier.weight(1f),
                                onCallClick = { dialPerson(row[1]) },
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        SharedBottomNav(
            current = ElderDestination.Contacts,
            onNavigate = onNavigate
        )
    }
}

@Composable
private fun EntertainmentListScreen(
    padding: PaddingValues,
    onNavigate: (ElderDestination) -> Unit,
) {
    val lang = LocalElderLanguage.current
    val catalog = remember { ottCatalog() }
    val platformById = remember(catalog) { catalog.associateBy { it.id } }

    var selectedCategoryName by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedCategory = selectedCategoryName?.let { OttCategory.valueOf(it) }

    var recentCsv by rememberSaveable { mutableStateOf("") }
    val recentIds = remember(recentCsv) { recentCsv.split("|").filter { it.isNotBlank() } }

    fun pushRecent(id: String) {
        val cur = recentCsv.split("|").filter { it.isNotBlank() }.toMutableList()
        cur.remove(id)
        cur.add(0, id)
        recentCsv = cur.take(6).joinToString("|")
    }

    fun openPlatform(platform: OttPlatform) {
        pushRecent(platform.id)
    }

    val filtered =
        remember(catalog, selectedCategory) {
            if (selectedCategory == null) catalog
            else catalog.filter { it.category == selectedCategory }
        }

    val filteredRows = remember(filtered) { filtered.chunked(2) }

    val recentPlatforms =
        remember(recentIds, platformById) {
            recentIds.mapNotNull { platformById[it] }
        }

    val recommended =
        remember(platformById) {
            listOf("yt", "music", "news").mapNotNull { platformById[it] }
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 10.dp, bottom = 10.dp),
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                EntertainmentTvHeader(
                    lang = lang,
                    onBack = { onNavigate(ElderDestination.Home) },
                )
            }

            item {
                VoiceAssistantShortcut(
                    lang = lang,
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp),
                    onClick = { /* Voice pipeline can plug in here */ },
                )
            }

            item {
                OttCategoryChipsRow(
                    lang = lang,
                    selectedCategory = selectedCategory,
                    onSelectAll = { selectedCategoryName = null },
                    onSelectCategory = { cat -> selectedCategoryName = cat.name },
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp),
                )
            }

            if (recentPlatforms.isNotEmpty()) {
                item {
                    OttSectionTitle(
                        text = tr(lang, "recent_played"),
                        modifier =
                            Modifier
                                .padding(horizontal = 16.dp),
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(recentPlatforms, key = { "recent_${it.id}" }) { platform ->
                            OttLaunchCard(
                                platform = platform,
                                lang = lang,
                                thumbHeight = 102.dp,
                                titleFontSize = 17.sp,
                                onOpen = { openPlatform(platform) },
                                modifier = Modifier.width(158.dp),
                            )
                        }
                    }
                }
            }

            item {
                OttSectionTitle(
                    text = tr(lang, "recommended"),
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp),
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(recommended, key = { "rec_${it.id}" }) { platform ->
                        OttLaunchCard(
                            platform = platform,
                            lang = lang,
                            thumbHeight = 102.dp,
                            titleFontSize = 17.sp,
                            onOpen = { openPlatform(platform) },
                            modifier = Modifier.width(164.dp),
                        )
                    }
                }
            }

            item {
                OttSectionTitle(
                    text = tr(lang, "browse_apps"),
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp),
                )
            }

            items(filteredRows, key = { row -> row.joinToString("-") { it.id } }) { row ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OttLaunchCard(
                        platform = row[0],
                        lang = lang,
                        thumbHeight = 118.dp,
                        titleFontSize = 18.sp,
                        onOpen = { openPlatform(row[0]) },
                        modifier = Modifier.weight(1f),
                    )
                    if (row.size > 1) {
                        OttLaunchCard(
                            platform = row[1],
                            lang = lang,
                            thumbHeight = 118.dp,
                            titleFontSize = 18.sp,
                            onOpen = { openPlatform(row[1]) },
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                Text(
                    text = tr(lang, "bigtap_footer"),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                    fontSize = 13.sp,
                    color = ElderNavy.copy(alpha = 0.55f),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }

        SharedBottomNav(
            modifier = Modifier.padding(horizontal = 16.dp),
            current = ElderDestination.Entertainment,
            onNavigate = onNavigate,
        )
    }
}

@Composable
private fun EntertainmentTvHeader(
    lang: ElderLanguage,
    onBack: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                Color(0xFF2D1B69),
                                Color(0xFF4A148C),
                                ElderSoftBlue,
                                ElderPageBg,
                            ),
                    ),
                )
                .padding(start = 10.dp, end = 16.dp, top = 4.dp, bottom = 26.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier =
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f)),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = tr(lang, "elder_back"),
                    tint = Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tr(lang, "entertainment").uppercase(Locale.getDefault()),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.82f),
                    letterSpacing = 1.1.sp,
                )
                Text(
                    text = tr(lang, "ott_home_title"),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp,
                )
                Text(
                    text = tr(lang, "ott_home_sub"),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.88f),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Icon(
                imageVector = Icons.Outlined.LiveTv,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(34.dp),
            )
        }
    }
}

@Composable
private fun VoiceAssistantShortcut(
    lang: ElderLanguage,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.98f else 1f, label = "voiceTap")

    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .scale(scale)
                .semantics {
                    role = Role.Button
                    contentDescription =
                        "${tr(lang, "voice_assistant")}. ${tr(lang, "voice_assistant_hint")}"
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
        shape = RoundedCornerShape(22.dp),
        color = ElderCard,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, ElderSoftBlue.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ElderSoftBlue, ElderMint.copy(alpha = 0.95f)),
                            ),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tr(lang, "voice_assistant"),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElderNavy,
                )
                Text(
                    text = tr(lang, "voice_assistant_hint"),
                    fontSize = 14.sp,
                    color = ElderNavy.copy(alpha = 0.62f),
                    modifier = Modifier.padding(top = 4.dp),
                    lineHeight = 18.sp,
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = ElderNavy.copy(alpha = 0.45f),
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun OttCategoryChipsRow(
    lang: ElderLanguage,
    selectedCategory: OttCategory?,
    onSelectAll: () -> Unit,
    onSelectCategory: (OttCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            OttCategoryChip(
                label = tr(lang, "cat_all"),
                selected = selectedCategory == null,
                onClick = onSelectAll,
            )
        }
        items(enumValues<OttCategory>().toList(), key = { it.name }) { cat ->
            OttCategoryChip(
                label = tr(lang, cat.trKey()),
                selected = selectedCategory == cat,
                onClick = { onSelectCategory(cat) },
            )
        }
    }
}

@Composable
private fun OttCategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier =
            Modifier
                .semantics {
                    role = Role.Button
                    contentDescription = label
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
        shape = RoundedCornerShape(24.dp),
        color =
            if (selected) ElderSoftBlue.copy(alpha = 0.22f) else ElderCard,
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    if (selected) ElderSoftBlue.copy(alpha = 0.65f)
                    else Color(0x14000000),
            ),
        shadowElevation = if (selected) 3.dp else 1.dp,
    ) {
        Text(
            text = label,
            modifier =
                Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 14.dp,
                ),
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (selected) ElderNavy else ElderNavy.copy(alpha = 0.85f),
        )
    }
}

@Composable
private fun OttSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.padding(top = 6.dp),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ElderNavy,
    )
}

@Composable
private fun OttLaunchCard(
    platform: OttPlatform,
    lang: ElderLanguage,
    thumbHeight: androidx.compose.ui.unit.Dp,
    titleFontSize: androidx.compose.ui.unit.TextUnit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (pressed) 0.97f else 1f, label = "ottCardPress")

    val title = tr(lang, platform.labelKey)
    val categoryLine = tr(lang, platform.category.trKey())

    Card(
        modifier =
            modifier
                .scale(scale)
                .semantics {
                    role = Role.Button
                    contentDescription =
                        "$title. ${tr(lang, "tap_open")}. $categoryLine."
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onOpen,
                ),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = ElderCard),
        border = BorderStroke(1.dp, Color(0x14000000)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(thumbHeight)
                        .clip(RoundedCornerShape(18.dp))
                        .background(platform.thumbBrush),
            ) {
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            Color.Black.copy(alpha = 0f),
                                            Color.Black.copy(alpha = 0.28f),
                                        ),
                                    ),
                            ),
                )
                Icon(
                    imageVector = platform.icon,
                    contentDescription = null,
                    tint = platform.thumbIconTint,
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .size(40.dp),
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = ElderNavy,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp,
            )
            Text(
                text = categoryLine,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ElderNavy.copy(alpha = 0.55f),
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = tr(lang, "tap_open"),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ElderSoftBlue,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun ContactCard(
    person: ContactPerson,
    modifier: Modifier = Modifier,
    onCallClick: () -> Unit
) {
    val lang = LocalElderLanguage.current
    Column(
        modifier = modifier
            .shadow(
                6.dp,
                RoundedCornerShape(20.dp),
                ambientColor = ElderNavy.copy(alpha = 0.06f),
                spotColor = ElderSoftBlue.copy(alpha = 0.12f),
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(118.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            person.avatarBg.copy(alpha = 0.95f),
                            ElderSoftBlue.copy(alpha = 0.2f),
                            person.avatarBg.copy(alpha = 0.58f),
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.1f))
                    .border(3.dp, Color.White.copy(alpha = 0.92f), CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.96f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = contactInitials(person.name),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElderNavy,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = person.name,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1C),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 25.sp,
            )
            Text(
                text = formatContactPhone(person.phone),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5C6578),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 12.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, ElderSoftBlue.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
                .background(ElderSoftBlue.copy(alpha = 0.12f))
                .clickable(onClick = onCallClick)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Call,
                    contentDescription = null,
                    tint = ElderSoftBlue,
                    modifier = Modifier.size(28.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tr(lang, "tap_to_call"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElderNavy.copy(alpha = 0.62f),
                    )
                    Text(
                        text = formatContactPhone(person.phone),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElderNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = ElderSoftBlue.copy(alpha = 0.78f),
                modifier = Modifier.size(22.dp),
            )
        }
    }
}


@Composable
private fun StubScreen(
    padding: PaddingValues,
    title: String,
    icon: ImageVector,
    color: Color,
    onSosPressed: () -> Unit,
    current: ElderDestination,
    onNavigate: (ElderDestination) -> Unit
) {
    val lang = LocalElderLanguage.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
        Spacer(Modifier.height(8.dp))
        Text(
            tr(lang, "coming_soon"),
            fontSize = 14.sp,
            color = Color(0xFF999999),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.weight(1f))
        SosButton(onSosPressed)
        Spacer(Modifier.height(12.dp))
        SharedBottomNav(current = current, onNavigate = onNavigate)
    }
}

@Composable
private fun SharedBottomNav(
    modifier: Modifier = Modifier,
    current: ElderDestination,
    onNavigate: (ElderDestination) -> Unit,
) {
    val left =
        when (current) {
            ElderDestination.Contacts -> ElderDestination.Home
            ElderDestination.Settings -> ElderDestination.Home
            else -> ElderDestination.Contacts
        }
    val right =
        when (current) {
            ElderDestination.Entertainment -> ElderDestination.Home
            ElderDestination.Settings -> ElderDestination.Contacts
            else -> ElderDestination.Entertainment
        }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        FooterNavCard(
            destination = left,
            modifier = Modifier.weight(1f),
            onNavigate = onNavigate
        )
        FooterNavCard(
            destination = right,
            modifier = Modifier.weight(1f),
            onNavigate = onNavigate
        )
    }
}

@Composable
private fun FooterNavCard(
    destination: ElderDestination,
    modifier: Modifier = Modifier,
    onNavigate: (ElderDestination) -> Unit
) {
    val lang = LocalElderLanguage.current
    data class NavMeta(
        val label: String,
        val icon: ImageVector,
        val tint: Color,
        val bg: Color
    )
    val meta = when (destination) {
        ElderDestination.Home -> NavMeta(tr(lang, "home"), Icons.Outlined.Home, Color(0xFF2F2F2F), Color(0xFFEDEDED))
        ElderDestination.Contacts -> NavMeta(tr(lang, "contacts"), Icons.Outlined.Contacts, Color(0xFF1565C0), Color(0xFFE3F2FD))
        ElderDestination.Entertainment -> NavMeta(tr(lang, "entertainment"), Icons.Outlined.Movie, Color(0xFF6A1B9A), Color(0xFFF3E5F5))
        ElderDestination.Medicines -> NavMeta(tr(lang, "medicines"), Icons.Outlined.MedicalServices, Color(0xFF2E7D32), Color(0xFFE8F5E9))
        ElderDestination.Vitals -> NavMeta(tr(lang, "vitals"), Icons.Outlined.MonitorHeart, Color(0xFFC62828), Color(0xFFFFEBEE))
        ElderDestination.Settings -> NavMeta(tr(lang, "settings"), Icons.Outlined.Settings, ElderNavy, Color(0xFFE8EAF6))
    }
    ActionCard(
        icon = meta.icon,
        label = meta.label,
        tint = meta.tint,
        bg = meta.bg,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.92f),
        labelFontSize = 16.sp,
        iconBoxDp = 72.dp,
        iconDp = 40.dp,
        onClick = { onNavigate(destination) },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ElderHomePreview() {
    CareCompanionTheme { ElderHomeScreen() }
}
