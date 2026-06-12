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
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures

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
@Composable
fun HomeScreen(viewModel: IslamQuranViewModel) {
    val scrollState = rememberScrollState()
    val times = viewModel.calculatedPrayerTimes

    var nextPrayerName by remember { mutableStateOf("Fajr") }
    var secondsLeft by remember { mutableStateOf(3600) }

    fun updateCountdown() {
        val now = java.util.Calendar.getInstance()
        val currSecs = now.get(java.util.Calendar.HOUR_OF_DAY) * 3600 + 
                       now.get(java.util.Calendar.MINUTE) * 60 + 
                       now.get(java.util.Calendar.SECOND)

        fun parseToSecs(timeStr: String): Int {
            return try {
                val parts = timeStr.split(":")
                parts[0].toInt() * 3600 + parts[1].toInt() * 60
            } catch (e: Exception) {
                0
            }
        }

        val pList = listOf(
            "Fajr" to parseToSecs(times.fajr),
            "Sunrise" to parseToSecs(times.sunrise),
            "Dhuhr" to parseToSecs(times.dhuhr),
            "Asr" to parseToSecs(times.asr),
            "Maghrib" to parseToSecs(times.maghrib),
            "Isha" to parseToSecs(times.isha)
        )

        var found = false
        for (p in pList) {
            if (p.second > currSecs) {
                nextPrayerName = p.first
                secondsLeft = p.second - currSecs
                found = true
                break
            }
        }

        if (!found) {
            nextPrayerName = "Fajr"
            secondsLeft = ((24 * 3600) - currSecs) + pList[0].second
        }
    }

    LaunchedEffect(times) {
        while (true) {
            updateCountdown()
            delay(1000)
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
                        text = "$nextPrayerName begins in",
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
                val prayerList = listOf(
                    "Fajr" to times.fajr,
                    "Sunrise" to times.sunrise,
                    "Dhuhr" to times.dhuhr,
                    "Asr" to times.asr,
                    "Maghrib" to times.maghrib,
                    "Isha" to times.isha
                )
                prayerList.forEach { (pName, pTime) ->
                    val isActive = pName == nextPrayerName
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
// AUDIO WAVE VISUALIZER
// ==========================================
@Composable
fun AudioWaveVisualizer(isPlaying: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val heights = (0..5).map { index ->
        val duration = when (index) {
            0 -> 600
            1 -> 850
            2 -> 700
            3 -> 950
            4 -> 550
            else -> 800
        }
        if (isPlaying) {
            infiniteTransition.animateFloat(
                initialValue = 0.15f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = duration
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            ).value
        } else {
            0.15f
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEach { heightVal ->
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .fillMaxHeight(heightVal)
                    .clip(RoundedCornerShape(1.2.dp))
                    .background(MaterialTheme.colorScheme.secondary)
            )
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
    var showReadabilitySettings by remember { mutableStateOf(false) }

    val quranFontFamily = when (viewModel.quranSelectedFontStyle) {
        "Elegant Sans" -> FontFamily.SansSerif
        "Monospace Tech" -> FontFamily.Monospace
        else -> FontFamily.Serif
    }
    
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

                                // Readability settings trigger
                                Row(
                                    modifier = Modifier.clickable { showReadabilitySettings = !showReadabilitySettings },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatSize,
                                        contentDescription = "Readability Typography",
                                        modifier = Modifier.size(16.dp),
                                        tint = if (showReadabilitySettings) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Size & Style",
                                        fontSize = 12.sp,
                                        color = if (showReadabilitySettings) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = showReadabilitySettings,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "READABILITY TYPOGRAPHY SETTINGS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        letterSpacing = 0.5.sp
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Arabic Font Size Slider
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "Arabic Font Size", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "${viewModel.quranArabicFontSize.toInt()} sp", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Black)
                                        }
                                        Slider(
                                            value = viewModel.quranArabicFontSize,
                                            onValueChange = { viewModel.quranArabicFontSize = it },
                                            valueRange = 16f..36f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = MaterialTheme.colorScheme.secondary,
                                                activeTrackColor = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    // English Font Size Slider
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "Translation Font Size", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "${viewModel.quranEnglishFontSize.toInt()} sp", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Black)
                                        }
                                        Slider(
                                            value = viewModel.quranEnglishFontSize,
                                            onValueChange = { viewModel.quranEnglishFontSize = it },
                                            valueRange = 10f..24f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = MaterialTheme.colorScheme.secondary,
                                                activeTrackColor = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    // Font Style Selector Row
                                    Text(text = "Script Style", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        viewModel.quranFontStylesList.forEach { style ->
                                            val isSelected = viewModel.quranSelectedFontStyle == style
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .background(
                                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent, RoundedCornerShape(8.dp))
                                                    .clickable { viewModel.quranSelectedFontStyle = style }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = style,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
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
                                fontSize = viewModel.quranArabicFontSize.sp,
                                fontFamily = quranFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                lineHeight = (viewModel.quranArabicFontSize * 1.6f).sp
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
                                            fontSize = viewModel.quranEnglishFontSize.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.82f),
                                            lineHeight = (viewModel.quranEnglishFontSize * 1.4f).sp,
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

                        if (viewModel.isAudioPlaying) {
                            AudioWaveVisualizer(
                                isPlaying = !viewModel.isAudioBuffering,
                                modifier = Modifier
                                    .height(18.dp)
                                    .padding(horizontal = 8.dp)
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
                                    if (viewModel.isAudioBuffering) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (viewModel.isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Control",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
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

                            // Custom Stream Alert Message if failed / falling back
                            viewModel.audioErrorMessage?.let { errMsg ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                        .background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color.Red.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = errMsg,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

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
                                    if (viewModel.isAudioBuffering) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(28.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 3.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (viewModel.isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play/Pause",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
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
                                "Sheikh Saad Al-Ghamdi",
                                "Sheikh Abdul Basit Samad",
                                "Sheikh Maher Al-Muaiqly",
                                "Sheikh Mahmoud Al-Hussary"
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

        // VERIFIED SECURITY BAR GAUGE WITH BREATHING/GLOW TRANSITION EFFECT
        val verifiedBorderAlpha by rememberInfiniteTransition(label = "verified_glow").animateFloat(
            initialValue = 0.15f,
            targetValue = 0.45f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "border_alpha"
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .background(
                    if (viewModel.isVerifiedSourcesMode) MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    if (viewModel.isVerifiedSourcesMode) MaterialTheme.colorScheme.secondary.copy(alpha = verifiedBorderAlpha)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (viewModel.isVerifiedSourcesMode) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                )
                Text(
                    text = if (viewModel.isVerifiedSourcesMode) "VERIFIED ACADEMIC AND SCRIPTURAL DATABASE IN ACTION" else "GENERAL INQUIRY ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.8.sp
                )
            }
            
            IconButton(
                onClick = { viewModel.isVerifiedSourcesMode = !viewModel.isVerifiedSourcesMode },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (viewModel.isVerifiedSourcesMode) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                    contentDescription = "Toggle Database Verification",
                    tint = if (viewModel.isVerifiedSourcesMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }
        }

        // Pinned high-fidelity verse recitation widget
        CompanionAudioPlayerWidget(viewModel = viewModel)

        // CHAT TIMELINE WITH FADE IN TRANSITION FOR MESSAGES
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(chatState) { msg ->
                    val isAi = msg.sender == "ai"
                    
                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(durationMillis = 400)) + expandVertically(animationSpec = tween(durationMillis = 400)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                        ) {
                            Card(
                                modifier = Modifier
                                    .widthIn(max = 300.dp)
                                    .testTag("chat_msg_${msg.id}")
                                    .animateContentSize(),
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isAi) 4.dp else 16.dp,
                                    bottomEnd = if (isAi) 16.dp else 4.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isAi) {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isAi) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                    else Color.Transparent
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = msg.text,
                                        fontSize = 13.sp,
                                        color = if (isAi) MaterialTheme.colorScheme.onBackground else Color.White,
                                        lineHeight = 19.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    
                                    if (isAi && msg.sources.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "VERIFIED SOURCES:",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            letterSpacing = 0.8.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        msg.sources.forEach { source ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(vertical = 2.dp)
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
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    if (isAi) {
                                        Spacer(modifier = Modifier.height(8.dp))
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
                                                    contentDescription = "Add to Favorites",
                                                    tint = if (msg.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (viewModel.isAiLoading) {
                    item {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(animationSpec = tween(durationMillis = 300))
                        ) {
                            Row(horizontalArrangement = Arrangement.Start) {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 3 Pulsating jumps/breathing indicator dots
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val infiniteTransition = rememberInfiniteTransition(label = "dots_loading")
                                            val animationDelays = listOf(0, 150, 300)
                                            
                                            animationDelays.forEach { delay ->
                                                val breathingAlpha by infiniteTransition.animateFloat(
                                                    initialValue = 0.2f,
                                                    targetValue = 1f,
                                                    animationSpec = infiniteRepeatable(
                                                        animation = keyframes {
                                                            durationMillis = 1000
                                                            0.2f at delay
                                                            1.0f at (delay + 300) % 1000
                                                            0.2f at (delay + 600) % 1000
                                                            0.2f at 1000
                                                        },
                                                        repeatMode = RepeatMode.Restart
                                                    ),
                                                    label = "dot_$delay"
                                                )
                                                
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = breathingAlpha))
                                                )
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Consulting verified Islamic resources...",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // DYNAMIC SMOOTH SUGGESTIONS ROW (Hides when custom chat builds up to keep clean interface)
        val suggestionsList = listOf(
            "Context of patience (Sabr)",
            "Concept of Khushu in prayer",
            "Greatness of Surah Al-Fatiha",
            "Story of Prophet Yusuf"
        )
        
        val recentQueriesList = viewModel.recentQueries
        
        AnimatedVisibility(
            visible = chatState.size <= 1 && !viewModel.isAiLoading && recentQueriesList.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(durationMillis = 500)) + expandVertically(animationSpec = tween(durationMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(animationSpec = tween(durationMillis = 300))
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "RECENT SEARCHES",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    letterSpacing = 0.8.sp
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    items(recentQueriesList) { query ->
                        Surface(
                            modifier = Modifier
                                .animateContentSize(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = query,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.clickable { viewModel.sendAiPrompt(query) }
                                )
                                IconButton(
                                    onClick = { viewModel.removeRecentQuery(query) },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove search",
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.49f),
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = chatState.size <= 1 && !viewModel.isAiLoading,
            enter = fadeIn(animationSpec = tween(durationMillis = 500)) + expandVertically(animationSpec = tween(durationMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(animationSpec = tween(durationMillis = 300))
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "SUGGESTED DISCOURSE TOPICS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    letterSpacing = 0.8.sp
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    items(suggestionsList) { topic ->
                        Card(
                            modifier = Modifier
                                .clickable { viewModel.sendAiPrompt(topic) }
                                .animateContentSize(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        ) {
                            Text(
                                text = topic,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // MINIMALIST MODERN INPUT PORTAL
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Clear Conversation Thread",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.61f)
                    )
                }

                TextField(
                    value = viewModel.aiInputText,
                    onValueChange = { viewModel.aiInputText = it },
                    placeholder = {
                        Text(
                            "Ask regarding verses or classical insights...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_input_text")
                        .padding(horizontal = 4.dp),
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
                        contentDescription = "Voice Assistant Simulator",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { viewModel.sendAiPrompt(viewModel.aiInputText) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    enabled = viewModel.aiInputText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message",
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
    val context = androidx.compose.ui.platform.LocalContext.current
    var activeSubTab by remember { mutableStateOf("Timings & Qibla") }
    var isCitiesExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 90.dp)
    ) {
        MainHeader(viewModel = viewModel, title = "Prayer and Qibla Tools")

        // Sub-tabs segment selector (no emojis, sleek minimal design with animated transitions)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Timings & Qibla", "Daily Adhkar").forEach { tab ->
                val isSelected = activeSubTab == tab
                val bgTrans by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    animationSpec = tween(250)
                )
                val textTrans by animateColorAsState(
                    targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    animationSpec = tween(250)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgTrans)
                        .clickable { activeSubTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textTrans,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Crossfade(
            targetState = activeSubTab,
            animationSpec = tween(300),
            modifier = Modifier.weight(1f)
        ) { tab ->
            when (tab) {
                "Timings & Qibla" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 1. ACTIVE INTEGRATED LOCATION PANEL
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
                                        text = "GEOGRAPHIC COORDINATES",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = viewModel.selectedCity,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Lat: ${String.format("%.4f", viewModel.userLatitude)}  |  Lon: ${String.format("%.4f", viewModel.userLongitude)}  |  TZ: UTC ${if (viewModel.activeTimezoneOffset >= 0) "+" else ""}${String.format("%.1f", viewModel.activeTimezoneOffset)}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.61f)
                                    )
                                }

                                if (viewModel.isLocationLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location Status",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            viewModel.locationErrorMsg?.let { error ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = error,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Location Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.requestAndRefreshLocation(context) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AUTODETECT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = { isCitiesExpanded = !isCitiesExpanded },
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCitiesExpanded) Icons.Default.ExpandLess else Icons.Default.LocationCity,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("MANUAL CITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            AnimatedVisibility(
                                visible = isCitiesExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "SELECT CLASSICAL HORIZONS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                        letterSpacing = 0.8.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    val cities = listOf(
                                        Triple("Mecca, KSA", 21.4225, 39.8262),
                                        Triple("London, UK", 51.5074, -0.1278),
                                        Triple("Cairo, Egypt", 30.0444, 31.2357),
                                        Triple("New York, NY", 40.7128, -74.0060),
                                        Triple("Medina, KSA", 24.4672, 39.6111),
                                        Triple("Tokyo, Japan", 35.6762, 139.6503)
                                    )

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(cities) { (name, lat, lon) ->
                                            Card(
                                                modifier = Modifier
                                                    .clickable {
                                                        viewModel.updateManualCoordinates(lat, lon, name)
                                                        viewModel.rotateQiblaSimulated()
                                                    },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (viewModel.selectedCity == name) {
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                                    } else {
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                                                    }
                                                ),
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (viewModel.selectedCity == name) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent
                                                )
                                            ) {
                                                Text(
                                                    text = name,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 1.5. CALCULATION CONVENTION SELECTOR
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "CALCULATION CONVENTION",
                            fontSize = 11.sp,
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
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                viewModel.calculationMethods.forEach { method ->
                                    val isSelected = viewModel.activeCalculationMethod == method
                                    val bgTrans by animateColorAsState(
                                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                                        animationSpec = tween(250)
                                    )
                                    val borderAlpha by animateFloatAsState(
                                        targetValue = if (isSelected) 0.25f else 0.04f,
                                        animationSpec = tween(250)
                                    )
                                    val textWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                                    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.61f)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(bgTrans)
                                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha), RoundedCornerShape(10.dp))
                                            .clickable {
                                                viewModel.activeCalculationMethod = method
                                                viewModel.recalculatePrayerTimes()
                                            }
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = method.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = textWeight,
                                            color = textColor,
                                            letterSpacing = 0.5.sp
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. DAILY PRAYER TIMINGS WITH NOTIFICATION CONFIGS
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "CALCULATED DAILY PRAYER TIMES".uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            modifier = Modifier.padding(horizontal = 20.dp),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val times = viewModel.calculatedPrayerTimes
                        val notificationsMap = viewModel.prayerNotificationSettings.value
                        val prayers = listOf(
                            Triple("Fajr", times.fajr, "Morning Prayer"),
                            Triple("Sunrise", times.sunrise, "Post-Dawn Horizon"),
                            Triple("Dhuhr", times.dhuhr, "Midday Prayer"),
                            Triple("Asr", times.asr, "Afternoon Prayer"),
                            Triple("Maghrib", times.maghrib, "Sunset Prayer"),
                            Triple("Isha", times.isha, "Night Prayer")
                        )

                        GlassmorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                prayers.forEach { (pName, pTime, pDesc) ->
                                    val isNotifyEnabled = notificationsMap[pName] ?: false
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.02f), RoundedCornerShape(12.dp))
                                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = pName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = pDesc,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                                        ) {
                                            Text(
                                                text = pTime,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Monospace,
                                                color = MaterialTheme.colorScheme.secondary
                                            )

                                            IconButton(
                                                onClick = { viewModel.togglePrayerNotification(pName) },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isNotifyEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                                    contentDescription = "Toggle Notification",
                                                    tint = if (isNotifyEnabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = { viewModel.triggerTestNotification(context, pName, pTime) },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f), CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Send,
                                                    contentDescription = "Test Notification Alarm",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // QIBLA LIVE 3D COMPASS DRAWING CANVAS
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val qiblaBearing = remember(viewModel.userLatitude, viewModel.userLongitude) {
                            val latMecca = Math.toRadians(21.4225)
                            val lonMecca = Math.toRadians(39.8262)
                            val latUser = Math.toRadians(viewModel.userLatitude)
                            val lonUser = Math.toRadians(viewModel.userLongitude)
                            val dLon = lonMecca - lonUser
                            val y = Math.sin(dLon) * Math.cos(latMecca)
                            val x = Math.cos(latUser) * Math.sin(latMecca) - Math.sin(latUser) * Math.cos(latMecca) * Math.cos(dLon)
                            var bearing = Math.toDegrees(Math.atan2(y, x))
                            (bearing + 360.0) % 360.0
                        }

                        var deviceHeading by remember { mutableStateOf(0f) }
                        var manualHeadingOffset by remember { mutableStateOf(0f) }

                        val context = androidx.compose.ui.platform.LocalContext.current

                        DisposableEffect(context) {
                            val sensorManager = context.getSystemService(android.content.Context.SENSOR_SERVICE) as? android.hardware.SensorManager
                            val rotationSensor = sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_ROTATION_VECTOR)
                                ?: sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_ORIENTATION)
                            
                            val listener = object : android.hardware.SensorEventListener {
                                private var accelerometerValues = FloatArray(3)
                                private var magneticValues = FloatArray(3)
                                private var hasAccel = false
                                private var hasMagnet = false

                                override fun onSensorChanged(event: android.hardware.SensorEvent) {
                                    if (event.sensor.type == android.hardware.Sensor.TYPE_ROTATION_VECTOR) {
                                        val rotationMatrix = FloatArray(9)
                                        android.hardware.SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                                        val orientationValues = FloatArray(3)
                                        android.hardware.SensorManager.getOrientation(rotationMatrix, orientationValues)
                                        val azimuthRad = orientationValues[0]
                                        var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                                        azimuthDeg = (azimuthDeg + 360f) % 360f
                                        deviceHeading = azimuthDeg
                                    } else if (event.sensor.type == android.hardware.Sensor.TYPE_ORIENTATION) {
                                        var azimuthDeg = event.values[0]
                                        azimuthDeg = (azimuthDeg + 360f) % 360f
                                        deviceHeading = azimuthDeg
                                    } else {
                                        if (event.sensor.type == android.hardware.Sensor.TYPE_ACCELEROMETER) {
                                            System.arraycopy(event.values, 0, accelerometerValues, 0, 3)
                                            hasAccel = true
                                        } else if (event.sensor.type == android.hardware.Sensor.TYPE_MAGNETIC_FIELD) {
                                            System.arraycopy(event.values, 0, magneticValues, 0, 3)
                                            hasMagnet = true
                                        }
                                        if (hasAccel && hasMagnet) {
                                            val r = FloatArray(9)
                                            val i = FloatArray(9)
                                            if (android.hardware.SensorManager.getRotationMatrix(r, i, accelerometerValues, magneticValues)) {
                                                val orientation = FloatArray(3)
                                                android.hardware.SensorManager.getOrientation(r, orientation)
                                                var azimuthDeg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                                                azimuthDeg = (azimuthDeg + 360f) % 360f
                                                deviceHeading = azimuthDeg
                                            }
                                        }
                                    }
                                }

                                override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
                            }

                            if (rotationSensor != null) {
                                sensorManager?.registerListener(listener, rotationSensor, android.hardware.SensorManager.SENSOR_DELAY_UI)
                            } else {
                                val accel = sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
                                val magnet = sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_MAGNETIC_FIELD)
                                if (accel != null && magnet != null) {
                                    sensorManager?.registerListener(listener, accel, android.hardware.SensorManager.SENSOR_DELAY_UI)
                                    sensorManager?.registerListener(listener, magnet, android.hardware.SensorManager.SENSOR_DELAY_UI)
                                }
                            }

                            onDispose {
                                sensorManager?.unregisterListener(listener)
                            }
                        }

                        val currentHeading = (deviceHeading + manualHeadingOffset + 360f) % 360f
                        val needleAngle = (qiblaBearing.toFloat() - currentHeading + 360f) % 360f

                        val headingDiff = (qiblaBearing - currentHeading + 360.0) % 360.0
                        val isAligned = headingDiff < 4.5 || headingDiff > 355.5

                        val alignmentGlowAlpha by animateFloatAsState(
                            targetValue = if (isAligned) 0.35f else 0.05f,
                            animationSpec = tween(400)
                        )
                        val needlePulseScale by animateFloatAsState(
                            targetValue = if (isAligned) 1.08f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.5f, stiffness = 150f)
                        )

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
                                        text = "INTEGRATED QIBLA LOCATOR",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = if (isAligned) "ALIGNED WITH KAABA" else "ROTATE TO ALIGN",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isAligned) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "TRUE BEARING",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "${String.format("%.1f", qiblaBearing)}° N",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            manualHeadingOffset = (manualHeadingOffset - dragAmount.x / 3f + 360f) % 360f
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Glow aura transitions
                                Box(
                                    modifier = Modifier
                                        .size(190.dp)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    if (isAligned) MaterialTheme.colorScheme.secondary.copy(alpha = alignmentGlowAlpha) else MaterialTheme.colorScheme.primary.copy(alpha = alignmentGlowAlpha),
                                                    Color.Transparent
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                )

                                val animatedCompassDialAngle by animateFloatAsState(
                                    targetValue = -currentHeading,
                                    animationSpec = spring(dampingRatio = 0.82f, stiffness = 120f)
                                )

                                val animatedNeedleAngle by animateFloatAsState(
                                    targetValue = needleAngle,
                                    animationSpec = spring(dampingRatio = 0.76f, stiffness = 160f)
                                )

                                val emeraldColor = MaterialTheme.colorScheme.primary
                                val goldColor = MaterialTheme.colorScheme.secondary
                                val onBackground = MaterialTheme.colorScheme.onBackground

                                Canvas(
                                    modifier = Modifier
                                        .size(175.dp)
                                        .testTag("qibla_compass_canvas")
                                ) {
                                    // Outer ring
                                    drawCircle(
                                        color = if (isAligned) goldColor.copy(alpha = 0.4f) else emeraldColor.copy(alpha = 0.15f),
                                        radius = size.width / 2f,
                                        style = Stroke(width = 6.dp.toPx())
                                    )
                                    drawCircle(
                                        color = if (isAligned) goldColor else emeraldColor.copy(alpha = 0.5f),
                                        radius = size.width / 2f - 4.dp.toPx(),
                                        style = Stroke(width = 1.dp.toPx())
                                    )

                                    // Rotate the tick marks and compass letters according to our heading orientation
                                    val dialCenter = size.width / 2f
                                    val dialRadius = size.width / 2f

                                    // Render Dial Tick marks + letters (N, E, S, W) rotated
                                    for (degree in 0 until 360 step 15) {
                                        val totalDegree = (degree.toFloat() + animatedCompassDialAngle + 360f) % 360f
                                        val isMajor = degree % 90 == 0
                                        val alpha = if (isMajor) 0.85f else 0.35f
                                        val strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                                        val tickLength = if (isMajor) 12.dp.toPx() else 7.dp.toPx()

                                        val rad = Math.toRadians(totalDegree.toDouble() - 90.0)
                                        val cosVal = Math.cos(rad).toFloat()
                                        val sinVal = Math.sin(rad).toFloat()

                                        val startX = dialCenter + (dialRadius - tickLength - 3.dp.toPx()) * cosVal
                                        val startY = dialCenter + (dialRadius - tickLength - 3.dp.toPx()) * sinVal
                                        val endX = dialCenter + (dialRadius - 3.dp.toPx()) * cosVal
                                        val endY = dialCenter + (dialRadius - 3.dp.toPx()) * sinVal

                                        val color = if (degree == 0) goldColor else if (isMajor) emeraldColor else onBackground

                                        drawLine(
                                            color = color.copy(alpha = alpha),
                                            start = Offset(startX, startY),
                                            end = Offset(endX, endY),
                                            strokeWidth = strokeWidth
                                        )
                                    }

                                    // Pointer needle to Kaaba (pointing at animatedNeedleAngle relative to screen's top)
                                    val needleRad = Math.toRadians(animatedNeedleAngle.toDouble() - 90.0)
                                    val nCos = Math.sin(Math.toRadians(animatedNeedleAngle.toDouble())).toFloat()
                                    val nSin = -Math.cos(Math.toRadians(animatedNeedleAngle.toDouble())).toFloat()

                                    val needleLen = (dialRadius - 28.dp.toPx()) * needlePulseScale
                                    val targetNeedleX = dialCenter + needleLen * nCos
                                    val targetNeedleY = dialCenter + needleLen * nSin

                                    // Left & right wings of the needle block to present a premium geometric compass head
                                    val wingRadLeft = Math.toRadians(animatedNeedleAngle.toDouble() - 90.0 - 15.0)
                                    val wingRadRight = Math.toRadians(animatedNeedleAngle.toDouble() - 90.0 + 15.0)
                                    val wingLen = 32.dp.toPx()

                                    val leftWingX = dialCenter + wingLen * Math.cos(wingRadLeft).toFloat()
                                    val leftWingY = dialCenter + wingLen * Math.sin(wingRadLeft).toFloat()

                                    val rightWingX = dialCenter + wingLen * Math.cos(wingRadRight).toFloat()
                                    val rightWingY = dialCenter + wingLen * Math.sin(wingRadRight).toFloat()

                                    val path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(targetNeedleX, targetNeedleY)
                                        lineTo(leftWingX, leftWingY)
                                        lineTo(dialCenter, dialCenter)
                                        close()
                                    }
                                    val rightPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(targetNeedleX, targetNeedleY)
                                        lineTo(rightWingX, rightWingY)
                                        lineTo(dialCenter, dialCenter)
                                        close()
                                    }

                                    // Draw Mecca pointer (glowing gold when aligned, or solid emerald/gold)
                                    drawPath(
                                        path = path,
                                        color = if (isAligned) goldColor else emeraldColor.copy(alpha = 0.8f)
                                    )
                                    drawPath(
                                        path = rightPath,
                                        color = if (isAligned) goldColor.copy(alpha = 0.7f) else emeraldColor.copy(alpha = 0.6f)
                                    )

                                    // Opposite balance tail indicator
                                    val tailCos = -nCos
                                    val tailSin = -nSin
                                    val tailLen = 25.dp.toPx()
                                    drawLine(
                                        color = if (isAligned) goldColor.copy(alpha = 0.6f) else onBackground.copy(alpha = 0.25f),
                                        start = Offset(dialCenter, dialCenter),
                                        end = Offset(dialCenter + tailLen * tailCos, dialCenter + tailLen * tailSin),
                                        strokeWidth = 3.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )

                                    // Center gold rivet core
                                    drawCircle(
                                        color = if (isAligned) goldColor else emeraldColor,
                                        radius = 8.dp.toPx()
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 3.dp.toPx()
                                    )
                                }

                                // Centered static indicator icon or label displaying "N" at the absolute top of the dial relative to rotation
                                Box(
                                    modifier = Modifier
                                        .size(175.dp)
                                        .padding(12.dp)
                                ) {
                                    // Static top alignment marker (always points straight up - the Z alignment notch of raw hardware)
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(if (isAligned) goldColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f), CircleShape)
                                            .align(Alignment.TopCenter)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            
                            val statusGuide = if (isAligned) {
                                "Hazarat, you are facing Mecca. Tap below to calibrate or check settings."
                            } else {
                                "Drag the compass dial or rotate your phone until the needle matches the alignment notch at the top."
                            }

                            Text(
                                text = statusGuide,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.61f),
                                modifier = Modifier.fillMaxWidth(),
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { 
                                        manualHeadingOffset = 0f
                                        viewModel.rotateQiblaSimulated()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("RESET DRAG", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.requestAndRefreshLocation(context)
                                    },
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("REFRESH GPS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
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
                "Daily Adhkar" -> {
                    AdhkarSection(viewModel = viewModel)
                }
            }
        }
    }
}

// ==========================================
// DAILY ADHKAR COMPOSITION
// ==========================================
@Composable
fun AdhkarSection(viewModel: IslamQuranViewModel) {
    var isMorningAdhkar by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        // Morning/Evening segmented switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Morning Adhkar", "Evening Adhkar").forEach { section ->
                val isSelected = (section == "Morning Adhkar" && isMorningAdhkar) || (section == "Evening Adhkar" && !isMorningAdhkar)
                val bgTrans by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    animationSpec = tween(250)
                )
                val textTrans by animateColorAsState(
                    targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    animationSpec = tween(250)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgTrans)
                        .clickable { isMorningAdhkar = (section == "Morning Adhkar") }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (section == "Morning Adhkar") Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = textTrans,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = section.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textTrans,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Action header (Reset all)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isMorningAdhkar) "AM PROTECTION SUPPLICATIONS" else "PM SEIZE THE NIGHT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            
            val activeList = if (isMorningAdhkar) viewModel.morningAdhkarList else viewModel.eveningAdhkarList
            val totalCompleted = activeList.count { it.currentCount == it.maxCount }
            
            Text(
                text = "$totalCompleted / ${activeList.size} COMPLETED",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 0.5.sp
            )
        }

        // List of supplications
        val activeList = if (isMorningAdhkar) viewModel.morningAdhkarList else viewModel.eveningAdhkarList

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(activeList, key = { it.id }) { item ->
                AdhkarItemCard(
                    item = item,
                    isMorning = isMorningAdhkar,
                    onIncrement = { viewModel.incrementAdhkar(item.id, isMorningAdhkar) },
                    onReset = { viewModel.resetAdhkar(item.id, isMorningAdhkar) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.resetAllAdhkar(isMorningAdhkar) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset All",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RESET ALL PROGRESS",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AdhkarItemCard(
    item: com.example.model.AdhkarItem,
    isMorning: Boolean,
    onIncrement: () -> Unit,
    onReset: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isCompleted = item.currentCount == item.maxCount

    // Core animations for tap effect
    val progressFraction = item.currentCount.toFloat() / item.maxCount.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    val scaleState = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    // Smooth color highlighting when completed
    val borderAlpha by animateFloatAsState(
        targetValue = if (isCompleted) 0.6f else 0.15f,
        animationSpec = tween(500)
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val cardBorderColor = if (isCompleted) {
        secondaryColor.copy(alpha = borderAlpha)
    } else {
        primaryColor.copy(alpha = borderAlpha)
    }

    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleState.value),
        borderStrokeColor = cardBorderColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left content space: Supplication text
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expanded = !expanded }
                    .padding(end = 12.dp)
            ) {
                // Arabic text with high readability
                Text(
                    text = item.textArabic,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 30.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Small expand hint without emojis
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "TAP TEXT TO ENGAGE DETAILS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                ) {
                    Column(
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "PHONETIC TRANSLITERATION",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.transliteration,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "ENGLISH TRANSLATION",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.textEnglish,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "REVEALED MERIT",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.merit,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Right content space: Highly modern circular counter trigger
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (!isCompleted) {
                                coroutineScope.launch {
                                    scaleState.animateTo(0.95f, animationSpec = tween(50))
                                    onIncrement()
                                    scaleState.animateTo(1.05f, animationSpec = spring(dampingRatio = 0.6f))
                                    scaleState.animateTo(1f, animationSpec = spring())
                                }
                            }
                        }
                        .background(
                            if (isCompleted) {
                                secondaryColor.copy(alpha = 0.12f)
                            } else {
                                primaryColor.copy(alpha = 0.05f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Custom track ring
                    Canvas(modifier = Modifier.size(60.dp)) {
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.08f),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    // Foreground progress arc
                    Canvas(modifier = Modifier.size(60.dp)) {
                        drawArc(
                            color = cardBorderColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Center text/icon with scale animation
                    androidx.compose.animation.AnimatedContent(
                        targetState = isCompleted,
                        transitionSpec = {
                            fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                        },
                        label = "center_content"
                    ) { completed ->
                        if (completed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${item.currentCount}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "OF ${item.maxCount}",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }

                // Individual reset link, animated to appear when count > 0
                androidx.compose.animation.AnimatedVisibility(
                    visible = item.currentCount > 0,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(250))
                ) {
                    Text(
                        text = "RESET",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clickable { onReset() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
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
                val reciters = listOf(
                    "Sheikh Mishary Al-Afasy",
                    "Sheikh Abdul Rahman Al-Sudais",
                    "Sheikh Saad Al-Ghamdi",
                    "Sheikh Abdul Basit Samad",
                    "Sheikh Maher Al-Muaiqly",
                    "Sheikh Mahmoud Al-Hussary"
                )
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

        // QURAN TEXT READABILITY CONFIGURATION PANEL
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Quran typography layout".uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 20.dp),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Arabic Layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Arabic Quran Size", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${viewModel.quranArabicFontSize.toInt()} sp", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Black)
                }
                Slider(
                    value = viewModel.quranArabicFontSize,
                    onValueChange = { viewModel.quranArabicFontSize = it },
                    valueRange = 16f..36f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Translation Layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Translation Text Size", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${viewModel.quranEnglishFontSize.toInt()} sp", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Black)
                }
                Slider(
                    value = viewModel.quranEnglishFontSize,
                    onValueChange = { viewModel.quranEnglishFontSize = it },
                    valueRange = 10f..24f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Style choices
                Text(text = "Preferred Script Style", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.quranFontStylesList.forEach { style ->
                        val isSelected = viewModel.quranSelectedFontStyle == style
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { viewModel.quranSelectedFontStyle = style }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = style,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
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

                                Spacer(modifier = Modifier.height(14.dp))
                                Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                Spacer(modifier = Modifier.height(10.dp))

                                // AI COMPANION TONE CONFIGURATION
                                Text(
                                    text = "Quran Companion AI Tone Style",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    viewModel.aiTonesList.forEach { tone ->
                                        val isSelected = viewModel.activeAiTone == tone
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .border(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent, RoundedCornerShape(8.dp))
                                                .clickable { 
                                                    viewModel.activeAiTone = tone 
                                                    viewModel.selectAdminActivity("AI Companion tone set to $tone")
                                                }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = tone,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // AI COMPANION RESPONSE LENGTH / DETAIL STYLE
                                Text(
                                    text = "Quran Companion AI Response Format",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    viewModel.aiResponseStylesList.forEach { style ->
                                        val isSelected = viewModel.activeAiResponseStyle == style
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .border(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent, RoundedCornerShape(8.dp))
                                                .clickable { 
                                                    viewModel.activeAiResponseStyle = style
                                                    viewModel.selectAdminActivity("AI Companion response format set to $style")
                                                }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = style,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                                textAlign = TextAlign.Center
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

@Composable
fun CompanionAudioPlayerWidget(viewModel: IslamQuranViewModel) {
    var expandedSurah by remember { mutableStateOf(false) }
    var expandedVerse by remember { mutableStateOf(false) }
    
    val selectedSurahForCompanion = remember { mutableStateOf(QuranRepository.surahs[0]) }
    val selectedVerseNumForCompanion = remember { mutableStateOf(1) }

    val isPlaying = viewModel.isCompanionAudioPlaying
    val currentSurah = viewModel.companionPlayingSurahId?.let { id -> QuranRepository.surahs.find { it.id == id } }
    val currentVerseNum = viewModel.companionPlayingVerseNum

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Companion Audio Player",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "COMPANION VERSE RECITATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 0.5.sp
                    )
                }
                
                if (currentSurah != null && currentVerseNum != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "PLAYING ${currentSurah.name.uppercase()} • AYA $currentVerseNum",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { expandedSurah = !expandedSurah },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedSurahForCompanion.value.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = expandedSurah,
                        onDismissRequest = { expandedSurah = false }
                    ) {
                        QuranRepository.surahs.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.name, fontSize = 12.sp) },
                                onClick = {
                                    selectedSurahForCompanion.value = s
                                    selectedVerseNumForCompanion.value = 1
                                    expandedSurah = false
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(0.7f)) {
                    Button(
                        onClick = { expandedVerse = !expandedVerse },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Verse ${selectedVerseNumForCompanion.value}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = expandedVerse,
                        onDismissRequest = { expandedVerse = false }
                    ) {
                        selectedSurahForCompanion.value.verses.forEach { v ->
                            DropdownMenuItem(
                                text = { Text("Verse ${v.number}", fontSize = 12.sp) },
                                onClick = {
                                    selectedVerseNumForCompanion.value = v.number
                                    expandedVerse = false
                                }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        val activeSelectorSurah = selectedSurahForCompanion.value.id
                        val activeSelectorVerse = selectedVerseNumForCompanion.value
                        val activePlayingSurah = viewModel.companionPlayingSurahId
                        val activePlayingVerse = viewModel.companionPlayingVerseNum
                        if (isPlaying && activePlayingSurah == activeSelectorSurah && activePlayingVerse == activeSelectorVerse) {
                            viewModel.stopCompanionVerse()
                        } else {
                            viewModel.playCompanionVerse(activeSelectorSurah, activeSelectorVerse)
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    val isCurrentPlayingThisSelection = isPlaying && 
                        viewModel.companionPlayingSurahId == selectedSurahForCompanion.value.id && 
                        viewModel.companionPlayingVerseNum == selectedVerseNumForCompanion.value
                    Icon(
                        imageVector = if (isCurrentPlayingThisSelection) Icons.Default.Square else Icons.Default.PlayArrow,
                        contentDescription = "Recite Verse",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            if (viewModel.isAudioBuffering && isPlaying) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            }
        }
    }
}
