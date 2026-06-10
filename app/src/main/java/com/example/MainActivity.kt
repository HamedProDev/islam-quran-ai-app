package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppTab
import com.example.model.IslamQuranViewModel
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.LightSurface

class MainActivity : ComponentActivity() {

    private val viewModel: IslamQuranViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Bind darkTheme toggle directly to our ViewModel state
            MyApplicationTheme(darkTheme = viewModel.isDarkMode) {
                var showAdminDashboard by remember { mutableStateOf(false) }

                val currentUserEmail = viewModel.currentUserEmail

                if (currentUserEmail == null) {
                    AuthScreensContainer(
                        viewModel = viewModel,
                        onAuthSuccess = {
                            viewModel.selectTab(AppTab.HOME)
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            // Hide user bottom nav when admin view is presented
                            if (!showAdminDashboard) {
                                AnimatedBottomNavigationBar(viewModel = viewModel)
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = if (showAdminDashboard) 0.dp else innerPadding.calculateBottomPadding())
                        ) {
                            AnimatedContent(
                                targetState = showAdminDashboard,
                                transitionSpec = {
                                    slideInVertically(animationSpec = spring(), initialOffsetY = { it }) togetherWith
                                            slideOutVertically(animationSpec = spring(), targetOffsetY = { it })
                                },
                                label = "AdminPanelSwap"
                            ) { isAdmin ->
                                if (isAdmin) {
                                    AdminDashboardScreen(
                                        viewModel = viewModel,
                                        onBackClick = { showAdminDashboard = false }
                                    )
                                } else {
                                    // User Side App Switcher
                                    Crossfade(
                                        targetState = viewModel.currentTab,
                                        animationSpec = spring(),
                                        label = "UserPagesCrossfade"
                                    ) { tab ->
                                        when (tab) {
                                            AppTab.HOME -> HomeScreen(viewModel = viewModel)
                                            AppTab.QURAN -> QuranReaderScreen(viewModel = viewModel)
                                            AppTab.AI_ASSISTANT -> AiAssistantScreen(viewModel = viewModel)
                                            AppTab.PRAYER -> PrayerToolsScreen(viewModel = viewModel)
                                            AppTab.MORE -> MoreSettingsScreen(
                                                viewModel = viewModel,
                                                onAdminClick = { showAdminDashboard = true }
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
fun AnimatedBottomNavigationBar(viewModel: IslamQuranViewModel) {
    // Custom High-End Minimalist Glassmorphic Bottom Navigation Bar (No emojis)
    val isDark = viewModel.isDarkMode
    val navBackground = if (isDark) {
        CharcoalCard.copy(alpha = 0.92f)
    } else {
        LightSurface.copy(alpha = 0.95f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                RoundedCornerShape(24.dp)
            ),
        color = navBackground,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                NavigationItem(AppTab.HOME, "Home", Icons.Default.Home),
                NavigationItem(AppTab.QURAN, "Quran", Icons.Default.Book),
                NavigationItem(AppTab.AI_ASSISTANT, "Assistant", Icons.Default.AutoAwesome, isCenterAccent = true),
                NavigationItem(AppTab.PRAYER, "Prayer", Icons.Default.CompassCalibration),
                NavigationItem(AppTab.MORE, "More", Icons.Default.Settings)
            )

            tabs.forEach { navItem ->
                val isSelected = viewModel.currentTab == navItem.tab
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_tab_${navItem.tab.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (navItem.isCenterAccent) {
                        // Floating Golden Premium action button style
                        IconButton(
                            onClick = { viewModel.selectTab(navItem.tab) },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                        ) {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.selectTab(navItem.tab) },
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = navItem.label,
                                    tint = if (isSelected) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = navItem.label,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)
                                    }
                                )
                                AnimatedVisibility(
                                    visible = isSelected,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .width(12.dp)
                                            .height(2.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.secondary,
                                                shape = RoundedCornerShape(1.dp)
                                            )
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

data class NavigationItem(
    val tab: AppTab,
    val label: String,
    val icon: ImageVector,
    val isCenterAccent: Boolean = false
)
