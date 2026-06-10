package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.api.TranslationEdition
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.LightSurface
import kotlinx.coroutines.delay

// --- SHARED GLASSMORPHIC CARD WITH SMOOTH HIGHLIGHTS ---
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    borderStrokeColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme() || !MaterialTheme.colorScheme.background.value.toString().contains("SoftIvory")
    
    val cardBackground = if (isDark) {
        CharcoalCard.copy(alpha = 0.85f)
    } else {
        LightSurface.copy(alpha = 0.95f)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = BorderStroke(1.dp, borderStrokeColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

// --- APP BAR HEADER ---
@Composable
fun MainHeader(
    viewModel: IslamQuranViewModel,
    title: String,
    onSearchClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Assalamu Alaikum",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontWeight = FontWeight.Light,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = (-0.5).sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Theme switcher
            IconButton(
                onClick = { viewModel.toggleTheme() },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (viewModel.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Notification Bell
            IconButton(
                onClick = { viewModel.selectAdminActivity("Inspected live notification drawer", "USER") },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Alerts",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // User Profile Frame (Gold highlighted)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                    .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "H",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ==========================================
// 1. HOME SCREEN (MAIN DASHBOARD)
// ==========================================
@Composable
fun HomeScreen(viewModel: IslamQuranViewModel) {
    val scrollState = rememberScrollState()
    
    // Countdown simulation for prayer
    var secondsLeft by remember { mutableStateOf(5820) } // Fajr countdown example
    LaunchedEffect(key1 = true) {
        while (true) {
            delay(1000)
            if (secondsLeft > 0) secondsLeft--
        }
    }
    
    val formattedCountdown = remember(secondsLeft) {
        val hours = secondsLeft / 3600
        val mins = (secondsLeft % 3600) / 60
        val secs = secondsLeft % 60
        String.format("%02d:%02d:%02d", hours, mins, secs)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        MainHeader(viewModel = viewModel, title = "Hamed")

        // PREMIUM CONTINUOUS QURAN TARGET CORNER GLOW
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    )
                )
                .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LAST READ SINCERELY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${viewModel.selectedSurah.name} - ${viewModel.selectedSurah.englishName}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    IconButton(
                        onClick = { viewModel.selectTab(AppTab.QURAN) },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Read",
                            tint = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(18.dp))
                
                Text(
                    text = "Verse ${viewModel.activeVerseIndex + 1} of ${viewModel.selectedSurah.verses.size} completed",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Continuous indicator line
                val currentProgress = (viewModel.activeVerseIndex + 1).toFloat() / viewModel.selectedSurah.verses.size.toFloat()
                LinearProgressIndicator(
                    progress = { currentProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }

        // DAILY VERSE & HADITH DUAL GRID LAYOUT
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "DAILY REFLEXIVE COMPASS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 20.dp),
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        val dailyVerse = viewModel.dailyAyahVerse
        val dailySurah = viewModel.dailyAyahSurah
        val isRefreshing = viewModel.isDailyAyahRefreshing

        if (dailyVerse != null && dailySurah != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.03f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            ) {
                AnimatedContent(
                    targetState = isRefreshing to dailyVerse,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "daily_ayah_transition"
                ) { (refreshingState, currentVerse) ->
                    if (refreshingState) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Preparing pristine inspiration...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "DAILY VERSE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = "Surah ${dailySurah.name} • ${dailySurah.englishName} [${dailySurah.id}:${currentVerse.number}]",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Bookmark button (compact but satisfying interactive touch target)
                                    val isBookmarked = viewModel.savedBookmarks.contains(currentVerse.id)
                                    IconButton(
                                        onClick = { viewModel.toggleBookmark(currentVerse.id) },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .testTag("daily_verse_bookmark_btn")
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark Verse",
                                            tint = if (isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Refresh daily ayah trigger button
                                    IconButton(
                                        onClick = { viewModel.loadDailyAyah(forceRandom = true) },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .testTag("daily_verse_refresh_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Shuffle Verse",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // Arabic line with spacious calligraphy feel
                            Text(
                                text = currentVerse.arabic,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Right,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                lineHeight = 28.sp
                            )
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Clean translation layout
                            Text(
                                text = currentVerse.english,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                lineHeight = 19.sp
                            )
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        viewModel.selectSurahAndVerse(dailySurah, dailySurah.verses.indexOf(currentVerse))
                                        viewModel.selectedVerseForTafsir = currentVerse
                                        viewModel.selectTab(AppTab.QURAN)
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("tafsir_button_today")
                                ) {
                                    Text(
                                        text = "EXPLORE TAFSIR",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // PRAYER TIMER WIDGET (FINTECH SLICK COUNTDOWN)
        Spacer(modifier = Modifier.height(12.dp))
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NEXT PRAYER COUNTDOWN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Fajr begins in",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = formattedCountdown,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Inline static visual gauge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val prayerList = listOf("Fajr" to "03:52", "Dhuhr" to "13:02", "Asr" to "17:01", "Maghrib" to "21:12", "Isha" to "22:50")
                prayerList.forEach { (pName, pTime) ->
                    val isActive = pName == "Fajr"
                    val itemBg = if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    }
                    val itemText = if (isActive) Color.White else MaterialTheme.colorScheme.onBackground
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(itemBg)
                            .border(
                                1.dp,
                                if (isActive) MaterialTheme.colorScheme.secondary else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = pName,
                                fontSize = 11.sp,
                                color = itemText.copy(alpha = if (isActive) 1f else 0.5f),
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = pTime,
                                fontSize = 12.sp,
                                color = itemText,
                                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // QUICK ACTIONS INTEGRATION GRID
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ecosystem tools".uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 20.dp),
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // Dual row action grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionGridCard(
                modifier = Modifier.weight(1f),
                title = "Mushaf Reader",
                subtitle = "Browse Chapters",
                icon = Icons.Default.Book,
                onClick = { viewModel.selectTab(AppTab.QURAN) }
            )
            ActionGridCard(
                modifier = Modifier.weight(1f),
                title = "Ask AI",
                subtitle = "Islamic Answers",
                icon = Icons.Default.AutoAwesome,
                onClick = { viewModel.selectTab(AppTab.AI_ASSISTANT) }
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionGridCard(
                modifier = Modifier.weight(1f),
                title = "Qibla Finder",
                subtitle = "Precise direction",
                icon = Icons.Default.CompassCalibration,
                onClick = {
                    viewModel.selectTab(AppTab.PRAYER)
                    viewModel.rotateQiblaSimulated()
                }
            )
            ActionGridCard(
                modifier = Modifier.weight(1f),
                title = "Monetize Panel",
                subtitle = "Admin Dashboard",
                icon = Icons.Default.AdminPanelSettings,
                onClick = { viewModel.selectTab(AppTab.MORE) }
            )
        }

        // AI INSIGHT MINI CARD SECTION
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "AI INSIGHT PATTERNS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 20.dp),
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        val suggestedPrompts = listOf(
            "Explain Surah Al-Fatiha in daily life context",
            "How do I improve prayer focus and Khushu?",
            "What constitutes patience (Sabr) in Tribulation?"
        )
        
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(suggestedPrompts) { promptText ->
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .clickable {
                            viewModel.selectTab(AppTab.AI_ASSISTANT)
                            viewModel.sendAiPrompt(promptText)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = "SUGGESTED DISCOVERY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = promptText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Ask Islam AI",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionGridCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .testTag("action_$title")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ==========================================
// 2. QURAN READER SCREEN
// ==========================================
@Composable
fun QuranReaderScreen(viewModel: IslamQuranViewModel) {
    var activeFilter by remember { mutableStateOf("") }
    var searchSubTab by remember { mutableStateOf("All") } // All, Chapters, Verses, AI Insights
    var isAudioPanelExpanded by remember { mutableStateOf(false) }
    
    val filteredSurahs = remember(activeFilter) {
        if (activeFilter.isEmpty()) QuranRepository.surahs
        else QuranRepository.surahs.filter { it.name.contains(activeFilter, ignoreCase = true) || it.englishName.contains(activeFilter, ignoreCase = true) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MainHeader(viewModel = viewModel, title = "Mushaf Al-Quran")

            // Modern Minimalist Search & Gemini Integration Field
            OutlinedTextField(
                value = viewModel.quranSearchQuery,
                onValueChange = { 
                    viewModel.quranSearchQuery = it 
                    if (it.isNotEmpty()) viewModel.quranSearchActive = true
                },
                placeholder = { 
                    Text(
                        "Search surahs, verses, or ask Gemini...", 
                        fontSize = 13.sp, 
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    ) 
                },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (viewModel.quranSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            viewModel.quranSearchQuery = ""
                            viewModel.quranSearchActive = false
                            viewModel.quranAiExplanationResponse = null
                        }) {
                            Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .testTag("quran_search_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-screen switching with subtle beautiful transition
            Crossfade(
                targetState = viewModel.quranSearchActive && viewModel.quranSearchQuery.isNotEmpty(),
                animationSpec = tween(durationMillis = 300),
                modifier = Modifier.weight(1f)
            ) { showSearch ->
                if (showSearch) {
                    // Intelligent Search Panel
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Sub-Tabs selection bar
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listOf("All", "Chapters", "Verses", "AI Insights")) { tab ->
                                val isSelected = tab == searchSubTab
                                Surface(
                                    onClick = { searchSubTab = tab },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent)
                                ) {
                                    Text(
                                        text = tab,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Search result lists and explanation engine card
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            // Gemini Explainer Card
                            if (searchSubTab == "All" || searchSubTab == "AI Insights") {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                                                    Text(
                                                        text = "GEMINI CONTEXTUAL EXPLAINER",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        letterSpacing = 1.sp
                                                    )
                                                    Text(
                                                        text = "Scholarly insights and context for: \"${viewModel.quranSearchQuery}\"",
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                                    )
                                                }
                                                
                                                if (!viewModel.isQuranAiLoading && viewModel.quranAiExplanationResponse == null) {
                                                    Button(
                                                        onClick = { viewModel.getQuranAiExplanation(viewModel.quranSearchQuery) },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = MaterialTheme.colorScheme.secondary,
                                                            contentColor = MaterialTheme.colorScheme.primary
                                                        ),
                                                        shape = RoundedCornerShape(8.dp),
                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                                    ) {
                                                        Text("Ask Gemini", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                            
                                            if (viewModel.isQuranAiLoading) {
                                                Spacer(modifier = Modifier.height(12.dp))
                                                LinearProgressIndicator(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(2.dp)
                                                        .clip(CircleShape),
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "Analyzing deep Quranic background context...",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                                )
                                            }
                                            
                                            viewModel.quranAiExplanationResponse?.let { explanation ->
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                                Spacer(modifier = Modifier.height(12.dp))
                                                
                                                Text(
                                                    text = explanation,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                                                    lineHeight = 19.sp
                                                )
                                                
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    TextButton(
                                                        onClick = { viewModel.quranAiExplanationResponse = null },
                                                        contentPadding = PaddingValues(0.dp)
                                                    ) {
                                                        Text("Clear Explanation", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Match Surahs (Chapters)
                            if (searchSubTab == "All" || searchSubTab == "Chapters") {
                                val matchedSurahs = QuranRepository.surahs.filter {
                                    it.name.contains(viewModel.quranSearchQuery, ignoreCase = true) ||
                                    it.englishName.contains(viewModel.quranSearchQuery, ignoreCase = true) ||
                                    it.rasm.contains(viewModel.quranSearchQuery, ignoreCase = true)
                                }

                                if (matchedSurahs.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "CHAPTERS MATCHES",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }

                                    items(matchedSurahs) { surah ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.selectSurah(surah)
                                                    viewModel.quranSearchQuery = ""
                                                    viewModel.quranSearchActive = false
                                                    viewModel.quranAiExplanationResponse = null
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                                            ),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(14.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(surah.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                    Text(surah.englishName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                                }
                                                Text(
                                                    text = surah.rasm,
                                                    fontSize = 17.sp,
                                                    fontFamily = FontFamily.Serif,
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Match Verses
                            if (searchSubTab == "All" || searchSubTab == "Verses") {
                                val matchedVerses = QuranRepository.surahs.flatMap { surah ->
                                    surah.verses.map { verse -> verse to surah }
                                }.filter { (verse, surah) ->
                                    verse.arabic.contains(viewModel.quranSearchQuery, ignoreCase = true) ||
                                    verse.english.contains(viewModel.quranSearchQuery, ignoreCase = true) ||
                                    verse.tafsir.contains(viewModel.quranSearchQuery, ignoreCase = true)
                                }

                                if (matchedVerses.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "VERSES MATCHES",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }

                                    items(matchedVerses) { (verse, surah) ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.selectSurahAndVerse(surah, surah.verses.indexOf(verse))
                                                    viewModel.quranSearchQuery = ""
                                                    viewModel.quranSearchActive = false
                                                    viewModel.quranAiExplanationResponse = null
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                                            ),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "${surah.name} • Verse ${verse.number}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.secondary
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowForward,
                                                        contentDescription = "Navigate to Verse",
                                                        modifier = Modifier.size(14.dp),
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = verse.arabic,
                                                    fontSize = 16.sp,
                                                    fontFamily = FontFamily.Serif,
                                                    textAlign = TextAlign.Right,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    lineHeight = 24.sp
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = verse.english,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Standard Reading Interface
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Surah fast horizontal filter picker
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredSurahs) { surah ->
                                val isSelected = surah.id == viewModel.selectedSurah.id
                                Surface(
                                    onClick = { viewModel.selectSurah(surah) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = surah.rasm,
                                            fontSize = 14.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = surah.name,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Translation toggle and view controller bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${viewModel.selectedSurah.name.uppercase()} • ${viewModel.selectedSurah.type.uppercase()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 1.sp
                            )

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                // Translation toggle
                                Row(
                                    modifier = Modifier.clickable { viewModel.showTranslation = !viewModel.showTranslation },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (viewModel.showTranslation) Icons.Default.CheckCircle else Icons.Default.Circle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Translation",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }

                        // Beautiful scrolling selection of multiple translations (Animated transition, dynamic API loading)
                        AnimatedVisibility(
                            visible = viewModel.showTranslation,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(TranslationEdition.values()) { edition ->
                                        val isSelected = viewModel.selectedTranslationEdition == edition
                                        val activeBg = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                                        val inactiveBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                                        
                                        Surface(
                                            onClick = { viewModel.selectTranslationEdition(edition) },
                                            shape = RoundedCornerShape(20.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.secondary else inactiveBg,
                                            border = BorderStroke(
                                                1.dp, 
                                                if (isSelected) MaterialTheme.colorScheme.secondary 
                                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                                            ),
                                            modifier = Modifier.animateContentSize()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = edition.displayName,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                                )
                                                Text(
                                                    text = "[${edition.language.take(3).uppercase()}]",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                viewModel.quranTranslationErrorMessage?.let { errMsg ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 22.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = errMsg,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                        TextButton(onClick = { viewModel.fetchCurrentSurahTranslation() }) {
                                            Text("Retry", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
                                }
                            }
                        }

                        Divider(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )

                        // READ LIST
                        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(viewModel.selectedSurah.verses) { verseIndex, verse ->
                    val isHighlighted = viewModel.isAudioPlaying && (viewModel.selectedSurah.verses.indexOf(verse) == viewModel.activeVerseIndex)
                    val isBookmarked = viewModel.savedBookmarks.contains(verse.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verse_${verse.number}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isHighlighted) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                            }
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isHighlighted) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top interactive row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            CircleShape
                                        )
                                        .size(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = verse.number.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Bookmark toggle
                                    IconButton(
                                        onClick = { viewModel.toggleBookmark(verse.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Save",
                                            tint = if (isBookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Tafsir open
                                    IconButton(
                                        onClick = { viewModel.selectedVerseForTafsir = verse },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = "Tafsir",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Arabic typography styled professionally
                            Text(
                                text = verse.arabic,
                                fontSize = 21.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                lineHeight = 34.sp
                            )

                            if (viewModel.showTranslation) {
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                val cacheKey = Pair(viewModel.selectedSurah.id, viewModel.selectedTranslationEdition.id)
                                val fetchedList = viewModel.quranTranslationsCache[cacheKey]
                                
                                AnimatedContent(
                                    targetState = viewModel.isQuranTranslationLoading to fetchedList,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                                    },
                                    label = "translation_text_transition"
                                ) { (loading, translations) ->
                                    if (loading && translations == null) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                color = MaterialTheme.colorScheme.secondary,
                                                strokeWidth = 1.dp,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Fetching translation...",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                            )
                                        }
                                    } else {
                                        val displayTranslation = if (translations != null && verseIndex < translations.size) {
                                            translations[verseIndex]
                                        } else {
                                            verse.english
                                        }
                                        
                                        Text(
                                            text = displayTranslation,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.82f),
                                            lineHeight = 18.sp,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
                     // FLOATING HIGH-END CONTROLLER COMPASS FOR AUDIO STREAMING
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .testTag("audio_controller_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessLow)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top row showing current state
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "RECITING SYNC • ${viewModel.selectedSurah.name.uppercase()}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Verse ${viewModel.activeVerseIndex + 1} of ${viewModel.selectedSurah.verses.size}",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = viewModel.selectedReciter,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.65f),
                                fontWeight = FontWeight.Normal
                            )
                        }

                        // Top right: Collapsed mini-controls, or Expand arrow
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (!isAudioPanelExpanded) {
                                IconButton(
                                    onClick = { viewModel.skipPrevVerse() },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.SkipPrevious, "Back", modifier = Modifier.size(20.dp))
                                }

                                IconButton(
                                    onClick = {
                                        if (viewModel.isAudioPlaying) viewModel.stopAudio() else viewModel.startAudio()
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (viewModel.isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Control",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.skipNextVerse() },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.SkipNext, "Skip", modifier = Modifier.size(20.dp))
                                }
                            }

                            IconButton(
                                onClick = { isAudioPanelExpanded = !isAudioPanelExpanded },
                                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                            ) {
                                Icon(
                                    imageVector = if (isAudioPanelExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Toggle Panel"
                                )
                            }
                        }
                    }

                    // Progress slider: Sleek timeline scrubbing. Visible in both states (slight variance in sizing)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    var localSliderProgress by remember { mutableStateOf<Float?>(null) }
                    val displayProgress = localSliderProgress ?: viewModel.audioProgress

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "0:00",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        
                        Slider(
                            value = displayProgress,
                            onValueChange = { localSliderProgress = it },
                            onValueChangeFinished = {
                                localSliderProgress?.let {
                                    viewModel.seekToProgress(it)
                                }
                                localSliderProgress = null
                            },
                            colors = SliderDefaults.colors(
                                activeTrackColor = MaterialTheme.colorScheme.secondary,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                                thumbColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(18.dp)
                        )

                        Text(
                            text = if (viewModel.isAudioPlaying) "LIVE" else "STOPPED",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (viewModel.isAudioPlaying) MaterialTheme.colorScheme.secondary else Color.White.copy(alpha = 0.4f),
                            letterSpacing = 0.5.sp
                        )
                    }

                    // EXPANDED PANEL DETAILS
                    AnimatedVisibility(
                        visible = isAudioPanelExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = Color.White.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Large center play pause
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { viewModel.skipPrevVerse() },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Default.SkipPrevious, "Previous Verse", modifier = Modifier.size(28.dp))
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                IconButton(
                                    onClick = {
                                        if (viewModel.isAudioPlaying) viewModel.stopAudio() else viewModel.startAudio()
                                    },
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (viewModel.isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                IconButton(
                                    onClick = { viewModel.skipNextVerse() },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Default.SkipNext, "Next Verse", modifier = Modifier.size(28.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // RECITERS SELECTOR
                            Text(
                                text = "SELECT RECITER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            val reciters = listOf(
                                "Sheikh Mishary Al-Afasy",
                                "Sheikh Abdul Rahman Al-Sudais",
                                "Sheikh Saad Al-Ghamdi"
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(reciters) { reciter ->
                                    val isSelected = viewModel.selectedReciter == reciter
                                    Surface(
                                        onClick = { 
                                            val wasPlaying = viewModel.isAudioPlaying
                                            viewModel.stopAudio()
                                            viewModel.selectedReciter = reciter 
                                            if (wasPlaying) {
                                                viewModel.startAudio()
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.secondary else Color.White.copy(alpha = 0.05f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.secondary else Color.White.copy(alpha = 0.15f)
                                        )
                                    ) {
                                        Text(
                                            text = reciter.replace("Sheikh ", ""),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // CONTROLLER MULTI-MODES: REPEAT & SPEED
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // REPEAT CONTROL COLUMN
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "REPEAT MODE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    
                                    val repeatOptions = listOf("None", "Verse", "Surah")
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                            .padding(3.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        repeatOptions.forEach { option ->
                                            val isSelected = viewModel.audioRepeatMode == option
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .background(
                                                        if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { viewModel.audioRepeatMode = option }
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = option,
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                                                )
                                            }
                                        }
                                    }
                                }

                                // SPEED CONTROL COLUMN
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "PLAYBACK SPEED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    
                                    val speedOptions = listOf(0.75f, 1.0f, 1.25f, 1.5f)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                            .padding(3.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        speedOptions.forEach { speed ->
                                            val isSelected = viewModel.audioSpeed == speed
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .background(
                                                        if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { viewModel.changeAudioSpeed(speed) }
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${speed}x",
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }       }
            }
        }
    }
}

    // TAFSIR EXPANDABLE BOTTOM SHEET DRAWER OVERLAY
        val openTafsir = viewModel.selectedVerseForTafsir
        AnimatedVisibility(
            visible = openTafsir != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { viewModel.selectedVerseForTafsir = null }
            )
        }

        AnimatedVisibility(
            visible = openTafsir != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                if (openTafsir != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.70f)
                            .clickable(enabled = false) {},
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                                        CircleShape
                                    )
                                    .align(Alignment.CenterHorizontally)
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Verse ${openTafsir.number} Tafsir Details",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(onClick = { viewModel.selectedVerseForTafsir = null }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = openTafsir.arabic,
                                    fontSize = 22.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth(),
                                    lineHeight = 36.sp
                                )
                                
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Translation Toggle",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        letterSpacing = 1.sp
                                    )
                                    Switch(
                                        checked = viewModel.showTranslation,
                                        onCheckedChange = { viewModel.showTranslation = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                            checkedTrackColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                                
                                AnimatedVisibility(visible = viewModel.showTranslation) {
                                    Column {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                        ) {
                                            Text(
                                                text = openTafsir.english,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                                                lineHeight = 18.sp,
                                                modifier = Modifier.padding(14.dp)
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Text(
                                    text = "Scholarly Tafsir Analysis",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    letterSpacing = 1.sp
                                )
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Text(
                                    text = openTafsir.tafsir,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. AI CHAT ASSISTANT
// ==========================================
@Composable
fun AiAssistantScreen(viewModel: IslamQuranViewModel) {
    var queryText by remember { mutableStateOf("") }
    val chatState = viewModel.chatMessages.value
    
    // Auto-scroll chat tracker index callback
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = chatState.size, key2 = viewModel.isAiLoading) {
        if (chatState.isNotEmpty()) {
            listState.animateScrollToItem(chatState.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MainHeader(viewModel = viewModel, title = "Islam Quran AI")

        // VERIFIED SECURITY BAR GAUGE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .background(
                    if (viewModel.isVerifiedSourcesMode) MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    RoundedCornerShape(10.dp)
                )
                .border(
                    1.dp,
                    if (viewModel.isVerifiedSourcesMode) MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                    else Color.Transparent,
                    RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "VERIFIED ISLAMIC SOURCES MODE ON",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
            }
            
            // Toggle Verified sources indicator
            IconButton(
                onClick = { viewModel.isVerifiedSourcesMode = !viewModel.isVerifiedSourcesMode },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (viewModel.isVerifiedSourcesMode) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                    contentDescription = "Modes",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // CHAT TIMELINE
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatState) { msg ->
                    val isAi = msg.sender == "ai"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                    ) {
                        Card(
                            modifier = Modifier
                                .widthIn(max = 290.dp)
                                .testTag("chat_msg_${msg.id}"),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isAi) 4.dp else 16.dp,
                                bottomEnd = if (isAi) 16.dp else 4.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAi) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isAi) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = msg.text,
                                    fontSize = 13.sp,
                                    color = if (isAi) MaterialTheme.colorScheme.onBackground else Color.White,
                                    lineHeight = 18.sp
                                )
                                
                                if (isAi && msg.sources.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "VERIFIED BASES:",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    msg.sources.forEach { source ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Link,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = source,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }

                                // Interactive save
                                if (isAi) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.toggleFavoriteMsg(msg.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (msg.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = "Save favorite",
                                                tint = if (msg.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (viewModel.isAiLoading) {
                    item {
                        Row(horizontalArrangement = Arrangement.Start) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Verifying classical texts...",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // INPUT CONSOLE COMPASS
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clear button
                IconButton(
                    onClick = { viewModel.clearChat() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                TextField(
                    value = viewModel.aiInputText,
                    onValueChange = { viewModel.aiInputText = it },
                    placeholder = {
                        Text(
                            "Inquire with verified sources...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_input_text"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        viewModel.sendAiPrompt(viewModel.aiInputText)
                    })
                )

                // Voice simulator with animation feedback
                IconButton(
                    onClick = {
                        viewModel.isVoiceActive = !viewModel.isVoiceActive
                        if (viewModel.isVoiceActive) {
                            viewModel.sendAiPrompt("How do I improve prayer focus?")
                            viewModel.isVoiceActive = false
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (viewModel.isVoiceActive) MaterialTheme.colorScheme.secondary else Color.Transparent,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (viewModel.isVoiceActive) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = if (viewModel.isVoiceActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = { viewModel.sendAiPrompt(viewModel.aiInputText) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    enabled = viewModel.aiInputText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Submit hint",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

// ==========================================
// 4. PRAYER & TOOLS SCREEN
// ==========================================
@Composable
fun PrayerToolsScreen(viewModel: IslamQuranViewModel) {
    var compassExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        MainHeader(viewModel = viewModel, title = "Prayer and Qibla Tools")

        // Location Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .clickable {
                    viewModel.selectedCity = if (viewModel.selectedCity.contains("London")) "Mecca, KSA" else "London, UK"
                    viewModel.rotateQiblaSimulated()
                }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "DETERMINED LOCATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
            Text(
                text = "${viewModel.selectedCity} (AUTODETECTED)",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // QIBLA LIVE 3D COMPASS DRAWING CANVAS
        Spacer(modifier = Modifier.height(16.dp))
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Interactive Qibla Finder",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${viewModel.qiblaAngle.toInt()} degrees North",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // COMPASS ROTATION ENGINE WITH PREMIUM CANVAS
            val targetAngle = viewModel.qiblaAngle
            val animatedAngle by animateFloatAsState(
                targetValue = targetAngle,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background compass drawing circle
                val emeraldColor = MaterialTheme.colorScheme.primary
                val goldColor = MaterialTheme.colorScheme.secondary

                Canvas(
                    modifier = Modifier
                        .size(170.dp)
                        .testTag("qibla_compass_canvas")
                ) {
                    // Outer ring
                    drawCircle(
                        color = goldColor.copy(alpha = 0.2f),
                        radius = size.width / 2f,
                        style = Stroke(width = 8.dp.toPx())
                    )
                    drawCircle(
                        color = emeraldColor,
                        radius = size.width / 2f - 4.dp.toPx(),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw static circular tick marks
                    for (i in 0 until 360 step 30) {
                        val angleRad = Math.toRadians(i.toDouble())
                        val cosVal = Math.cos(angleRad).toFloat()
                        val sinVal = Math.sin(angleRad).toFloat()
                        val startPx = size.width / 2f + (size.width / 2f - 14.dp.toPx()) * cosVal
                        val startPy = size.height / 2f + (size.height / 2f - 14.dp.toPx()) * sinVal
                        val endPx = size.width / 2f + (size.width / 2f - 4.dp.toPx()) * cosVal
                        val endPy = size.height / 2f + (size.height / 2f - 4.dp.toPx()) * sinVal
                        
                        drawLine(
                            color = goldColor.copy(alpha = 0.5f),
                            start = Offset(startPx, startPy),
                            end = Offset(endPx, endPy),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }

                    // Rotating indicator needle based on current angle selection
                    val rotRad = Math.toRadians(animatedAngle.toDouble() - 90.0) // 0 north adjustment
                    val needleCos = Math.cos(rotRad).toFloat()
                    val needleSin = Math.sin(rotRad).toFloat()

                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val needleLen = size.width / 2f - 20.dp.toPx()
                    val targetX = centerX + needleLen * needleCos
                    val targetY = centerY + needleLen * needleSin

                    // Gold pointer tip
                    drawLine(
                        color = goldColor,
                        start = Offset(centerX, centerY),
                        end = Offset(targetX, targetY),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    
                    // Small emerald balance bottom tip
                    drawLine(
                        color = emeraldColor.copy(alpha = 0.4f),
                        start = Offset(centerX, centerY),
                        end = Offset(centerX - 30.dp.toPx() * needleCos, centerY - 30.dp.toPx() * needleSin),
                        strokeWidth = 3.dp.toPx()
                    )

                    // Center pin point
                    drawCircle(
                        color = goldColor,
                        radius = 8.dp.toPx()
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = "Hold phone flat. Align the golden direction needle directly with the top center alignment of your screen.",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            
            Button(
                onClick = { viewModel.rotateQiblaSimulated() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("CALIBRATE SENSORS", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
        }

        // RAMADAN FAST TRACKER STATE WIDGET
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ramadan Progress Tracker".uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 20.dp),
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ramadan 1447 AH Calendar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Day ${viewModel.activeRamadanDay} completed successfully",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Checkbox(
                        checked = viewModel.isRamadanChecked,
                        onCheckedChange = {
                            viewModel.isRamadanChecked = it
                            viewModel.selectAdminActivity("Fasting tracker checked day ${viewModel.activeRamadanDay}", "USER")
                        },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary)
                    )
                    Text(
                        text = "DAY COMPLETED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Slider tracker
            Slider(
                value = viewModel.activeRamadanDay.toFloat(),
                onValueChange = { viewModel.activeRamadanDay = it.toInt() },
                valueRange = 1f..30f,
                steps = 28,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Day 1", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                Text(text = "Day 15 (Midway)", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                Text(text = "Day 30", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

// ==========================================
// 5. MORE SETTINGS & ADMIN PORTAL GATEWAY
// ==========================================
@Composable
fun MoreSettingsScreen(
    viewModel: IslamQuranViewModel,
    onAdminClick: () -> Unit
) {
    var expandedLanguage by remember { mutableStateOf(false) }
    var expandedReciter by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        MainHeader(viewModel = viewModel, title = "Settings")

        // MONETIZATION CONFIG: UPGRADE TO PREMIUM GATEWAY CARD
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (viewModel.isPremiumEnabled) {
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    }
                )
                .border(
                    1.5.dp,
                    if (viewModel.isPremiumEnabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Islam Quran AI Premium",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (viewModel.isPremiumEnabled) "Unlimited Scholar Verified Service Active" else "Support our servers and unlock advanced tools",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    
                    Switch(
                        checked = viewModel.isPremiumEnabled,
                        onCheckedChange = {
                            viewModel.isPremiumEnabled = it
                            viewModel.selectAdminActivity("User subscription state toggled to $it", "USER")
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Premium Features Active:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "• Advanced AI Tafsir Querying", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                        Text(text = "• Offline Reading Databases", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                        Text(text = "• Global Recitation Streaming", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (viewModel.isPremiumEnabled) "SICK ACCESS" else "$4.99/mo",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // ACCOUNT CONFIG LISTS
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "User Preference configuration".uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 20.dp),
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // Language Config Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                .clickable { expandedLanguage = !expandedLanguage }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, "Language", tint = MaterialTheme.colorScheme.primary)
                Text(text = "System Language", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = viewModel.selectedLanguage, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        }
        
        AnimatedVisibility(visible = expandedLanguage) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                val languages = listOf("English (US)", "Arabic (Al-Arabiya)", "Melayu (Malaysian)", "French (Français)")
                languages.forEach { lang ->
                    Text(
                        text = lang,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectedLanguage = lang
                                expandedLanguage = false
                                viewModel.selectAdminActivity("Admin configuration language set to $lang")
                            }
                            .padding(12.dp),
                        color = if (lang == viewModel.selectedLanguage) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Reciter Selection Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                .clickable { expandedReciter = !expandedReciter }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RecordVoiceOver, "Reciter", tint = MaterialTheme.colorScheme.primary)
                Text(text = "Favorite Reciter Voice", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = viewModel.selectedReciter.split(" ").last(), fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        }
        
        AnimatedVisibility(visible = expandedReciter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                val reciters = listOf("Sheikh Mishary Al-Afasy", "Sheikh Abdul Rahman Al-Sudais", "Sheikh Saad Al-Ghamdi")
                reciters.forEach { reciter ->
                    Text(
                        text = reciter,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectedReciter = reciter
                                expandedReciter = false
                                viewModel.selectAdminActivity("Selected primary reciter voice $reciter")
                            }
                            .padding(12.dp),
                        color = if (reciter == viewModel.selectedReciter) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // GATEWAY PORTAL ADMIN CONTROLLER BAR
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "SAAS LEVEL DEPLOYMENT ACCESS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 20.dp),
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onAdminClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(48.dp)
                .testTag("admin_dashboard_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AdminPanelSettings, "Admin")
                Text("LAUNCH MONETIZATION DASHBOARD", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
        }
    }
}

// ==========================================
// 6. SaaS ADMIN DASHBOARD
// ==========================================
@Composable
fun AdminDashboardScreen(
    viewModel: IslamQuranViewModel,
    onBackClick: () -> Unit
) {
    var adminActiveSubTab by remember { mutableStateOf("Overview") } // Overview, Mon, Users, AI Control
    val activityLogsList = viewModel.activityLogs.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 40.dp)
    ) {
        // Top Back Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.primary)
            }

            Text(
                text = "SYSTEM SaaS ADMIN",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "SERVER COMPILER PRO",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Horizontal dashboard section toggles
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val sections = listOf("Overview", "Users", "Monetization Manager", "AI Moderation Policy")
            items(sections) { sec ->
                val isActive = sec == adminActiveSubTab
                Surface(
                    onClick = { adminActiveSubTab = sec },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, if (isActive) MaterialTheme.colorScheme.secondary else Color.Transparent)
                ) {
                    Text(
                        text = sec.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        // MULTI-SCREEN SWITCH FOR ADMIN SECTIONS
        Crossfade(targetState = adminActiveSubTab) { subTab ->
            when (subTab) {
                "Overview" -> {
                    Column {
                        // Key Metric Grid cards
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AdminMetricCard(
                                modifier = Modifier.weight(1f),
                                label = "Total users tracked",
                                value = viewModel.totalUsers.toString(),
                                change = "+14% this month"
                            )
                            AdminMetricCard(
                                modifier = Modifier.weight(1f),
                                label = "Subscribers",
                                value = viewModel.activeSubscriptions.toString(),
                                change = "45% Premium ratio"
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AdminMetricCard(
                                modifier = Modifier.weight(1f),
                                label = "Ad revenue generated",
                                value = "$${viewModel.adRevenueAccrued.toInt()}",
                                change = "eCPM optimized"
                            )
                            AdminMetricCard(
                                modifier = Modifier.weight(1f),
                                label = "Ad impressions today",
                                value = viewModel.totalAdImpressions.toString(),
                                change = "Live server query"
                            )
                        }

                        // LIVE SYSTEM LOGS LIST (8pt grid)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "LIVE USER TELEMETRY RECORDS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 20.dp),
                            letterSpacing = 0.5.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            activityLogsList.take(6).forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = log.userName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = log.action,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    
                                    Text(
                                        text = log.timestamp,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                            }
                        }
                    }
                }
                
                "Users" -> {
                    Column {
                        // User Search
                        OutlinedTextField(
                            value = viewModel.adminSearchQuery,
                            onValueChange = { viewModel.adminSearchQuery = it },
                            placeholder = { Text("Search system users database...", fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .testTag("admin_user_search"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // Users list
                        val filteredUsers = viewModel.adminUsersList.value.filter {
                            it.name.contains(viewModel.adminSearchQuery, ignoreCase = true) || it.email.contains(viewModel.adminSearchQuery, ignoreCase = true)
                        }

                        filteredUsers.forEach { user ->
                            GlassmorphicCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = user.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${user.email} • Joined ${user.joinDate}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = user.subscription,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { viewModel.toggleUserStatus(user.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (user.status == "Active") {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.secondary
                                            }
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(
                                            text = if (user.status == "Active") "SUSPEND" else "RESTATE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                "Monetization Manager" -> {
                    Column {
                        // AD PLACEMENT EDITOR
                        Text(
                            text = "GLOBAL ADS REVENUE CONTROLS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                            letterSpacing = 0.5.sp
                        )

                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Enable Home Banner Ads",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Switch(
                                        checked = viewModel.isBannerAdsEnabled,
                                        onCheckedChange = {
                                            viewModel.isBannerAdsEnabled = it
                                            viewModel.selectAdminActivity("Toggled Home Banner Ads to $it")
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Enable Interstitial Transition Ads",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Switch(
                                        checked = viewModel.isInterstitialAdsEnabled,
                                        onCheckedChange = {
                                            viewModel.isInterstitialAdsEnabled = it
                                            viewModel.selectAdminActivity("Toggled Interstitial Transition Ads to $it")
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Enable Rewarded Ad Tokens",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Switch(
                                        checked = viewModel.isRewardedAdsEnabled,
                                        onCheckedChange = {
                                            viewModel.isRewardedAdsEnabled = it
                                            viewModel.selectAdminActivity("Toggled Rewarded Ad Tokens to $it")
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
                                    )
                                }
                            }
                        }

                        // SUBSCRIPTION GATING DEFINER
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "PREMIUM ACCOUNT GATING TIERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                            letterSpacing = 0.5.sp
                        )

                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Gate Offline Quran reading", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Switch(
                                        checked = viewModel.gateOfflineQuran,
                                        onCheckedChange = {
                                            viewModel.gateOfflineQuran = it
                                            viewModel.selectAdminActivity("Gated Offline Quran: $it")
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Gate High-Fidelity Recitations", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Switch(
                                        checked = viewModel.gatePremiumRecitations,
                                        onCheckedChange = {
                                            viewModel.gatePremiumRecitations = it
                                            viewModel.selectAdminActivity("Gated Custom High-Fidelity Recitations: $it")
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Gate Advanced AI Tafsir queries", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Switch(
                                        checked = viewModel.gateAdvancedAiTafsir,
                                        onCheckedChange = {
                                            viewModel.gateAdvancedAiTafsir = it
                                            viewModel.selectAdminActivity("Gated Advanced AI Tafsir: $it")
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
                                    )
                                }
                            }
                        }
                    }
                }
                
                "AI Moderation Policy" -> {
                    Column {
                        Text(
                            text = "AI SAFETY AND DB VERIFICATION DECISIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                            letterSpacing = 0.5.sp
                        )

                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "Enable AI Output Moderation", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Blocks hazardous non-theological statements dynamically", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                    }
                                    Switch(
                                        checked = viewModel.isAiModerationEnabled,
                                        onCheckedChange = {
                                            viewModel.isAiModerationEnabled = it
                                            viewModel.selectAdminActivity("AI output moderation toggled to $it")
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "Enforce verified reference strictness", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Restricts generated responses to verified reference databases", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                    }
                                    Switch(
                                        checked = viewModel.selectedVerifiedSourcesOnly,
                                        onCheckedChange = {
                                            viewModel.selectedVerifiedSourcesOnly = it
                                            viewModel.selectAdminActivity("Strict verification constraints: $it")
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Deep Verification timeout threshold",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Slider(
                                        value = viewModel.deepVerificationSpeedLimit.toFloat(),
                                        onValueChange = {
                                            viewModel.deepVerificationSpeedLimit = it.toInt()
                                            viewModel.selectAdminActivity("AI deep verification limit set to ${it.toInt()}s")
                                        },
                                        valueRange = 1f..10f,
                                        modifier = Modifier.weight(1f),
                                        colors = SliderDefaults.colors(
                                            thumbColor = MaterialTheme.colorScheme.secondary,
                                            activeTrackColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Text(
                                        text = "${viewModel.deepVerificationSpeedLimit}s",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    change: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = change,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
