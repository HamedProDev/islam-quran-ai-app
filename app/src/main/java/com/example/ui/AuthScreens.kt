package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IslamQuranViewModel
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.GreenGlow

enum class AuthMode {
    SIGN_IN, REGISTER, RECOVERY
}

@Composable
fun AuthScreensContainer(
    viewModel: IslamQuranViewModel,
    onAuthSuccess: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalBlack)
    ) {
        // Celestial subtle dark background glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-100).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFC8A24A).copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-100).dp, y = 100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GreenGlow.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                // Gold Crescent Icon Container
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(GreenGlow, CharcoalSurface)
                            )
                        )
                        .border(1.dp, Color(0xFFC8A24A).copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = "Logo",
                        tint = Color(0xFFC8A24A),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "ISLAM QURAN AI",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF7F3E8),
                    letterSpacing = 4.sp
                )

                Text(
                    text = "Verified Scholarly Hub",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFFC8A24A),
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Dynamic Form Section
            AnimatedContent(
                targetState = mode,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "AuthFormAnimation",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(Alignment.CenterVertically)
            ) { currentMode ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(CharcoalCard.copy(alpha = 0.85f))
                        .border(
                            1.dp,
                            Color(0xFFC8A24A).copy(alpha = 0.12f),
                            RoundedCornerShape(28.dp)
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val modeTitle = when (currentMode) {
                        AuthMode.SIGN_IN -> "Sign In"
                        AuthMode.REGISTER -> "Create Account"
                        AuthMode.RECOVERY -> "Retrieve Access"
                    }

                    val modeSubtitle = when (currentMode) {
                        AuthMode.SIGN_IN -> "Accrue authentic knowledge from verified streams."
                        AuthMode.REGISTER -> "Join the global network of scholars and readers."
                        AuthMode.RECOVERY -> "Reset your credentials securely via email link."
                    }

                    Text(
                        text = modeTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = modeSubtitle,
                        fontSize = 11.sp,
                        color = Color(0xFFF7F3E8).copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                    )

                    // Error Display
                    viewModel.authErrorMessage?.let { errMsg ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF442222).copy(alpha = 0.6f)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = errMsg,
                                color = Color(0xFFFFB2B2),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Success Display
                    viewModel.authSuccessMessage?.let { successMsg ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = GreenGlow.copy(alpha = 0.4f)
                            ),
                            border = BorderStroke(2.dp, Color(0xFFC8A24A).copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = successMsg,
                                color = Color(0xFFE5C16C),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // EMAIL FIELD
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input"),
                        label = { Text("Email Address", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFFC8A24A).copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFC8A24A),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedLabelColor = Color(0xFFC8A24A),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // PASSWORD FIELD (IF NOT IN RECOVERY)
                    if (currentMode != AuthMode.RECOVERY) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input"),
                            label = { Text("Password", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFC8A24A).copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.4f)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFC8A24A),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedLabelColor = Color(0xFFC8A24A),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // CONFIRM PASSWORD FIELD (IF REGISTER)
                    if (currentMode == AuthMode.REGISTER) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_confirm_password_input"),
                            label = { Text("Confirm Password", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFC8A24A).copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.4f)
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFC8A24A),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                focusedLabelColor = Color(0xFFC8A24A),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Bottom links (Forgot password, etc.)
                    if (currentMode == AuthMode.SIGN_IN) {
                        TextButton(
                            onClick = { mode = AuthMode.RECOVERY },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "Forgot Password?",
                                fontSize = 11.sp,
                                color = Color(0xFFC8A24A),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ACTION BUTTON
                    Button(
                        onClick = {
                            when (currentMode) {
                                AuthMode.SIGN_IN -> {
                                    viewModel.signInWithEmail(email, password, onAuthSuccess)
                                }
                                AuthMode.REGISTER -> {
                                    if (password != confirmPassword) {
                                        viewModel.authErrorMessage = "Passwords do not match."
                                    } else {
                                        viewModel.registerWithEmail(email, password, onAuthSuccess)
                                    }
                                }
                                AuthMode.RECOVERY -> {
                                    viewModel.recoverPassword(email)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_action_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC8A24A),
                            contentColor = CharcoalBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !viewModel.isAuthLoading
                    ) {
                        if (viewModel.isAuthLoading) {
                            CircularProgressIndicator(
                                color = CharcoalBlack,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            val buttonText = when (currentMode) {
                                AuthMode.SIGN_IN -> "Sign In Securely"
                                AuthMode.REGISTER -> "Register Scholars Gate"
                                AuthMode.RECOVERY -> "Send Reset Invitation"
                            }
                            Text(
                                text = buttonText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Footer Link Toggles
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val promptText = when (mode) {
                        AuthMode.SIGN_IN -> "First time logging in?"
                        AuthMode.REGISTER -> "Already have an account?"
                        AuthMode.RECOVERY -> "Remembered your credentials?"
                    }

                    val toggleText = when (mode) {
                        AuthMode.SIGN_IN -> "Sign Up"
                        AuthMode.REGISTER -> "Sign In"
                        AuthMode.RECOVERY -> "Sign In"
                    }

                    Text(
                        text = promptText,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    TextButton(
                        onClick = {
                            viewModel.authErrorMessage = null
                            viewModel.authSuccessMessage = null
                            mode = if (mode == AuthMode.SIGN_IN) AuthMode.REGISTER else AuthMode.SIGN_IN
                        }
                    ) {
                        Text(
                            text = toggleText,
                            fontSize = 12.sp,
                            color = Color(0xFFC8A24A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Fast bypass developer bypass
                Button(
                    onClick = {
                        // Bypass directly with full admin context
                        viewModel.signInWithEmail("hamussein01@gamil.com", "gapelgpdd003", onAuthSuccess)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                        .clip(CircleShape),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = Color(0xFFC8A24A).copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Access via Developer Guest Mode",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
