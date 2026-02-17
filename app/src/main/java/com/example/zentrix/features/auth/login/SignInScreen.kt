package com.example.zentrix.features.auth.login

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zentrix.core.designsystem.GlassButton
import com.example.zentrix.core.designsystem.GlassButtonVariant
import com.example.zentrix.core.designsystem.GlassCard
import com.example.zentrix.core.designsystem.GlassDivider
import com.example.zentrix.core.designsystem.GlassTextField
import dev.chrisbanes.haze.rememberHazeState
import dev.chrisbanes.haze.hazeSource
import androidx.compose.foundation.text.KeyboardOptions
import com.example.zentrix.ui.theme.ObsidianTheme

@Composable
fun SignInScreen(
    viewModel          : SignInViewModel = hiltViewModel(),
    onNavigateToSignup : () -> Unit,
    onAuthSuccess      : () -> Unit
) {
    val hazeState        = rememberHazeState()
    var passwordVisible  by remember { mutableStateOf(false) }

    // ── Ambient animation — two orbs drift slowly around the background ──
    val infiniteTransition = rememberInfiniteTransition(label = "orb_transition")

    val orb1X by infiniteTransition.animateFloat(
        initialValue   = 0f,
        targetValue    = 1f,
        animationSpec  = infiniteRepeatable(tween(8000, easing = EaseInOutSine), RepeatMode.Reverse),
        label          = "orb1x"
    )
    val orb1Y by infiniteTransition.animateFloat(
        initialValue   = 0f,
        targetValue    = 1f,
        animationSpec  = infiniteRepeatable(tween(6500, easing = EaseInOutCubic), RepeatMode.Reverse),
        label          = "orb1y"
    )
    val orb2X by infiniteTransition.animateFloat(
        initialValue   = 1f,
        targetValue    = 0f,
        animationSpec  = infiniteRepeatable(tween(7200, easing = EaseInOutSine), RepeatMode.Reverse),
        label          = "orb2x"
    )
    val orb2Y by infiniteTransition.animateFloat(
        initialValue   = 1f,
        targetValue    = 0f,
        animationSpec  = infiniteRepeatable(tween(9000, easing = EaseInOutCubic), RepeatMode.Reverse),
        label          = "orb2y"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianTheme.background)
    ) {
        // ── Animated ambient orbs — the hazeSource so GlassCard blurs them ──
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
        ) {
            // Orb 1 — accent violet, drifts top-left area
            drawCircle(
                color  = ObsidianTheme.accent.copy(alpha = 0.28f),
                radius = size.minDimension * 0.55f,
                center = Offset(
                    x = size.width  * (0.1f + orb1X * 0.5f),
                    y = size.height * (0.05f + orb1Y * 0.45f)
                )
            )
            // Orb 2 — indigo-purple, drifts bottom-right area
            drawCircle(
                color  = Color(0xFF5B4CF6).copy(alpha = 0.20f),
                radius = size.minDimension * 0.45f,
                center = Offset(
                    x = size.width  * (0.5f + orb2X * 0.45f),
                    y = size.height * (0.5f  + orb2Y * 0.45f)
                )
            )
            // Orb 3 — static subtle gold hint, bottom-left
            drawCircle(
                color  = ObsidianTheme.gold.copy(alpha = 0.08f),
                radius = size.minDimension * 0.30f,
                center = Offset(x = size.width * 0.1f, y = size.height * 0.85f)
            )
        }

        // ── Content ──
        Column(
            modifier              = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.Center
        ) {

            // ── Logo / wordmark ──
            LogoMark()

            Spacer(Modifier.height(32.dp))

            // ── Glass card ──
            GlassCard(hazeState = hazeState) {

                // Heading
                Text(
                    text      = "Welcome back",
                    style     = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color     = ObsidianTheme.textPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Sign in to continue shopping",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ObsidianTheme.textSecondary
                )

                Spacer(Modifier.height(28.dp))

                // Email field
                GlassTextField(
                    value         = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label         = "Email",
                    placeholder   = "you@example.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon   = {
                        Icon(
                            Icons.Outlined.Email,
                            contentDescription = null,
                            tint     = ObsidianTheme.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Password field
                GlassTextField(
                    value                = viewModel.password,
                    onValueChange        = { viewModel.password = it },
                    label                = "Password",
                    placeholder          = "••••••••",
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Lock,
                            contentDescription = null,
                            tint     = ObsidianTheme.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                // Reveal toggle sits just below the password field, right-aligned
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Show/hide password
                    Row(
                        modifier = Modifier
                            .clickable(
                                indication        = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { passwordVisible = !passwordVisible }
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector        = if (passwordVisible) Icons.Outlined.VisibilityOff
                            else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint               = ObsidianTheme.textMuted,
                            modifier           = Modifier.size(15.dp)
                        )
                        Text(
                            text  = if (passwordVisible) "Hide" else "Show",
                            style = MaterialTheme.typography.labelSmall,
                            color = ObsidianTheme.textMuted
                        )
                    }

                    // Forgot password
                    Text(
                        text     = "Forgot password?",
                        style    = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color    = ObsidianTheme.accent,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable(
                                indication        = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { /* TODO */ }
                    )
                }

                // Error message
                if (!viewModel.errorMessage.isNullOrEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ObsidianTheme.red.copy(0.12f))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text  = viewModel.errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = ObsidianTheme.red
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Primary CTA
                GlassButton(
                    text      = "Sign In",
                    onClick   = { viewModel.onSignInClicked(onAuthSuccess) },
                    isLoading = viewModel.isLoading,
                    enabled   = !viewModel.isLoading,
                    variant   = GlassButtonVariant.Primary
                )

                Spacer(Modifier.height(20.dp))


                Spacer(Modifier.height(20.dp))



                GlassDivider()

                Spacer(Modifier.height(16.dp))

                // Navigate to sign-up
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Don't have an account? ",
                        style = MaterialTheme.typography.bodySmall,
                        color = ObsidianTheme.textSecondary
                    )
                    Text(
                        text     = "Sign up",
                        style    = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color    = ObsidianTheme.accent,
                        modifier = Modifier.clickable(
                            indication        = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onNavigateToSignup() }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Logo mark — obsidian pill with accent glow
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LogoMark() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Icon orb
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            ObsidianTheme.accent.copy(0.55f),
                            ObsidianTheme.surfaceElevated
                        )
                    )
                )
                .then(
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Shopping bag silhouette drawn with a simple unicode glyph
            Text("✦", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text          = "ZENTRIX",
            style         = MaterialTheme.typography.titleLarge.copy(
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            ),
            color         = ObsidianTheme.textPrimary
        )
        Text(
            text  = "Premium shopping, redefined",
            style = MaterialTheme.typography.bodySmall,
            color = ObsidianTheme.textMuted
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// "— or continue with —" divider
// ─────────────────────────────────────────────────────────────────────────────

