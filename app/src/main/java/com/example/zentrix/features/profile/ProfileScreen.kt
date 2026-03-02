package com.example.zentrix.features.profile

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zentrix.core.designsystem.ChangePasswordBottomSheet
import com.example.zentrix.core.designsystem.EditProfileBottomSheet
import com.example.zentrix.ui.theme.ObsidianTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    hazeState: HazeState,
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    paddingValues : PaddingValues
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Collapsing toolbar effect
    val scrollOffset = remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
    val isCollapsed = remember { derivedStateOf { listState.firstVisibleItemIndex > 0 || scrollOffset.value > 100 } }

    val headerHeight by animateDpAsState(
        targetValue = if (isCollapsed.value) 80.dp else 200.dp,
        animationSpec = tween(300),
        label = "header_height"
    )

    val avatarSize by animateDpAsState(
        targetValue = if (isCollapsed.value) 40.dp else 90.dp,
        animationSpec = tween(300),
        label = "avatar_size"
    )

    var showSignOutDialog by remember { mutableStateOf(false) }
    var showEditProfileSheet by remember { mutableStateOf(false) }
    var showChangePasswordSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianTheme.background)
            .hazeSource(hazeState)
            .padding(paddingValues)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Collapsing Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(headerHeight)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ObsidianTheme.accent.copy(0.15f),
                                    ObsidianTheme.background
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = if (isCollapsed.value) Arrangement.Center else Arrangement.Bottom
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(avatarSize)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            ObsidianTheme.accent,
                                            Color(0xFFB06CF6)
                                        )
                                    )
                                )
                                .border(3.dp, ObsidianTheme.accent.copy(0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.name.firstOrNull()?.uppercase() ?: "?",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isCollapsed.value) 18.sp else 36.sp
                                ),
                                color = Color.White
                            )
                        }

                        if (!isCollapsed.value) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Name
                            Text(
                                text = userProfile.name.ifEmpty { "User" },
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ObsidianTheme.textPrimary
                            )

                            // Email
                            Text(
                                text = userProfile.email.ifEmpty { "email@example.com" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = ObsidianTheme.textSecondary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Edit Profile Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ObsidianTheme.accent.copy(0.15f))
                                    .border(0.8.dp, ObsidianTheme.accent.copy(0.4f), RoundedCornerShape(12.dp))
                                    .clickable { showEditProfileSheet = true }
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Edit Profile",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = ObsidianTheme.accent
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Account Section
            item {
                SectionHeader("Account")
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Rounded.Person,
                    title = "Personal Information",
                    subtitle = "Update your details",
                    onClick = { showEditProfileSheet = true }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Rounded.Lock,
                    title = "Change Password",
                    subtitle = "Secure your account",
                    onClick = { showChangePasswordSheet = true }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Rounded.Email,
                    title = "Email Preferences",
                    subtitle = "Manage notifications",
                    onClick = { /* Navigate to email preferences */ }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // App Settings Section
            item {
                SectionHeader("App Settings")
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Rounded.Notifications,
                    title = "Notifications",
                    subtitle = "Push notifications settings",
                    onClick = { /* Navigate to notifications */ }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Rounded.Language,
                    title = "Language",
                    subtitle = "English",
                    onClick = { /* Navigate to language selection */ }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Rounded.Palette,
                    title = "Appearance",
                    subtitle = "Dark mode",
                    onClick = { /* Navigate to appearance */ }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Support Section
            item {
                SectionHeader("Support")
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Rounded.HelpOutline,
                    title = "Help Center",
                    subtitle = "Get support",
                    onClick = { /* Navigate to help */ }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Rounded.Description,
                    title = "Terms & Privacy",
                    subtitle = "Legal information",
                    onClick = { /* Navigate to terms */ }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Rounded.Info,
                    title = "About",
                    subtitle = "Version 1.0.0",
                    onClick = { /* Navigate to about */ }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Sign Out Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ObsidianTheme.red.copy(0.12f))
                            .border(0.8.dp, ObsidianTheme.red.copy(0.3f), RoundedCornerShape(16.dp))
                            .clickable { showSignOutDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.Logout,
                                contentDescription = null,
                                tint = ObsidianTheme.red,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Sign Out",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ObsidianTheme.red
                            )
                        }
                    }
                }
            }
        }
    }

    // Sign Out Confirmation Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor = ObsidianTheme.surfaceElevated,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    "Sign Out?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ObsidianTheme.textPrimary
                )
            },
            text = {
                Text(
                    "You'll need to sign in again to use this app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ObsidianTheme.textSecondary
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianTheme.red.copy(alpha = 0.15f))
                        .border(0.5.dp, ObsidianTheme.red.copy(0.4f), RoundedCornerShape(12.dp))
                        .clickable {
                            showSignOutDialog = false
                            onSignOut()
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        "Sign out",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = ObsidianTheme.red
                    )
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianTheme.surfaceElevated)
                        .border(0.5.dp, ObsidianTheme.surfaceBorder, RoundedCornerShape(12.dp))
                        .clickable { showSignOutDialog = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        "Cancel",
                        style = MaterialTheme.typography.labelMedium,
                        color = ObsidianTheme.textSecondary
                    )
                }
            }
        )
    }

    // Edit Profile Bottom Sheet
    if (showEditProfileSheet) {
        EditProfileBottomSheet(
            currentProfile = userProfile,
            onDismiss = { showEditProfileSheet = false },
            onSave = { name, phone ->
                viewModel.updateProfile(name, phone)
                showEditProfileSheet = false
            }
        )
    }

    // Change Password Bottom Sheet
    if (showChangePasswordSheet) {
        ChangePasswordBottomSheet(
            onDismiss = { showChangePasswordSheet = false },
            onSave = { oldPassword, newPassword ->
                viewModel.changePassword(oldPassword, newPassword)
                showChangePasswordSheet = false
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        color = ObsidianTheme.textMuted,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = ObsidianTheme.surfaceElevated,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianTheme.accent.copy(0.12f))
                        .border(0.5.dp, ObsidianTheme.accent.copy(0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = ObsidianTheme.accent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ObsidianTheme.textPrimary
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = ObsidianTheme.textSecondary
                    )
                }
            }

            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = ObsidianTheme.textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}