package com.example.zentrix.features.auth.signup

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.zentrix.core.designsystem.GlassButton
import com.example.zentrix.core.designsystem.GlassButtonVariant
import com.example.zentrix.core.designsystem.GlassCard
import com.example.zentrix.core.designsystem.GlassDivider
import com.example.zentrix.core.designsystem.GlassTextField
import com.example.zentrix.ui.theme.ObsidianTheme
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

// ─────────────────────────────────────────────────────────────────────────────
// Password strength helpers
// ─────────────────────────────────────────────────────────────────────────────

private data class PasswordStrength(
    val score  : Int,     // 0–4
    val label  : String,
    val color  : Color
)

private fun evaluateStrength(password: String, accentColor: Color, green: Color, gold: Color, red: Color): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength(0, "", accentColor)
    var score = 0
    if (password.length >= 8)                          score++
    if (password.any { it.isUpperCase() })             score++
    if (password.any { it.isDigit() })                 score++
    if (password.any { !it.isLetterOrDigit() })        score++
    return when (score) {
        1    -> PasswordStrength(1, "Weak",   red)
        2    -> PasswordStrength(2, "Fair",   gold)
        3    -> PasswordStrength(3, "Good",   accentColor)
        4    -> PasswordStrength(4, "Strong", green)
        else -> PasswordStrength(0, "",       accentColor)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SignupScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SignupScreen(
    viewModel          : SignupViewModel = hiltViewModel(),
    onNavigateToLogin  : () -> Unit,
    onNavigateBack     : () -> Unit,
    onAuthSuccess      : () -> Unit
) {
    val hazeState              = rememberHazeState()
    var passwordVisible        by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val scrollState            = rememberScrollState()

    // ── Animated ambient orbs — mirrored from SignInScreen but with different timing ──
    val infiniteTransition = rememberInfiniteTransition(label = "orb_signup")

    val orb1X by infiniteTransition.animateFloat(
        initialValue  = 0.8f, targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(9000, easing = EaseInOutSine), RepeatMode.Reverse),
        label         = "orb1x"
    )
    val orb1Y by infiniteTransition.animateFloat(
        initialValue  = 0.1f, targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(7000, easing = EaseInOutCubic), RepeatMode.Reverse),
        label         = "orb1y"
    )
    val orb2X by infiniteTransition.animateFloat(
        initialValue  = 0.1f, targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(8500, easing = EaseInOutSine), RepeatMode.Reverse),
        label         = "orb2x"
    )
    val orb2Y by infiniteTransition.animateFloat(
        initialValue  = 0.9f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(6800, easing = EaseInOutCubic), RepeatMode.Reverse),
        label         = "orb2y"
    )

    // Password strength derived from current value
    val passwordStrength = evaluateStrength(
        password    = viewModel.password,
        accentColor = ObsidianTheme.accent,
        green       = ObsidianTheme.green,
        gold        = ObsidianTheme.gold,
        red         = ObsidianTheme.red
    )
    val passwordsMatch = viewModel.password.isNotEmpty() &&
            viewModel.confirmPassword.isNotEmpty() &&
            viewModel.password == viewModel.confirmPassword

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianTheme.background)
    ) {

        // ── Animated ambient orbs — hazeSource so GlassCard blurs through them ──
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
        ) {
            drawCircle(
                color  = ObsidianTheme.accent.copy(alpha = 0.26f),
                radius = size.minDimension * 0.52f,
                center = Offset(size.width * orb1X, size.height * orb1Y)
            )
            drawCircle(
                color  = Color(0xFF3B4CF6).copy(alpha = 0.18f),
                radius = size.minDimension * 0.42f,
                center = Offset(size.width * orb2X, size.height * orb2Y)
            )
            // Static subtle green tint — top-right corner
            drawCircle(
                color  = ObsidianTheme.green.copy(alpha = 0.07f),
                radius = size.minDimension * 0.28f,
                center = Offset(size.width * 0.9f, size.height * 0.08f)
            )
        }

        // ── Scrollable content — extra bottom padding so last item clears keyboard ──
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo mark (same as SignInScreen) ──
            SignupLogoMark()

            Spacer(Modifier.height(32.dp))

            // ── Glass card ──
            GlassCard(hazeState = hazeState) {

                // Heading
                Text(
                    text  = "Create account",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = ObsidianTheme.textPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Join Zentrix and start shopping",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ObsidianTheme.textSecondary
                )

                Spacer(Modifier.height(28.dp))

                // ── Full name ──
                GlassTextField(
                    value         = viewModel.fullName,
                    onValueChange = { viewModel.fullName = it },
                    label         = "Full name",
                    placeholder   = "Alex Johnson",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    leadingIcon   = {
                        Icon(
                            Icons.Outlined.Person, null,
                            tint     = ObsidianTheme.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                // ── Email ──
                GlassTextField(
                    value           = viewModel.email,
                    onValueChange   = { viewModel.email = it },
                    label           = "Email",
                    placeholder     = "you@example.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon     = {
                        Icon(
                            Icons.Outlined.Email, null,
                            tint     = ObsidianTheme.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                // ── Password ──
                GlassTextField(
                    value                = viewModel.password,
                    onValueChange        = { viewModel.password = it },
                    label                = "Password",
                    placeholder          = "Min. 8 characters",
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon          = {
                        Icon(
                            Icons.Outlined.Lock, null,
                            tint     = ObsidianTheme.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                // Show/hide toggle for password
                PasswordToggleRow(
                    visible   = passwordVisible,
                    onToggle  = { passwordVisible = !passwordVisible }
                )

                // Password strength meter — only shown when user has typed something
                if (viewModel.password.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    PasswordStrengthMeter(strength = passwordStrength)
                }

                Spacer(Modifier.height(16.dp))

                // ── Confirm password ──
                GlassTextField(
                    value                = viewModel.confirmPassword,
                    onValueChange        = { viewModel.confirmPassword = it },
                    label                = "Confirm password",
                    placeholder          = "Re-enter your password",
                    isError              = viewModel.confirmPassword.isNotEmpty() && !passwordsMatch,
                    errorMessage         = if (viewModel.confirmPassword.isNotEmpty() && !passwordsMatch)
                        "Passwords don't match" else null,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon          = {
                        Icon(
                            Icons.Outlined.Lock, null,
                            tint     = if (viewModel.confirmPassword.isNotEmpty() && !passwordsMatch)
                                ObsidianTheme.red.copy(0.7f)
                            else ObsidianTheme.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                // Show/hide toggle for confirm password
                PasswordToggleRow(
                    visible  = confirmPasswordVisible,
                    onToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                    // Show a match indicator on the right when confirm is non-empty
                    trailingContent = {
                        if (viewModel.confirmPassword.isNotEmpty()) {
                            MatchIndicator(matches = passwordsMatch)
                        }
                    }
                )

                // Error banner (API / server errors from ViewModel)
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

                // ── Terms notice ──
                TermsNotice()

                Spacer(Modifier.height(16.dp))

                // ── Primary CTA ──
                GlassButton(
                    text      = "Create Account",
                    onClick   = { viewModel.onSignupClicked(onAuthSuccess) },
                    isLoading = viewModel.isLoading,
                    enabled   = !viewModel.isLoading,
                    variant   = GlassButtonVariant.Primary
                )

                Spacer(Modifier.height(20.dp))

                GlassDivider()

                Spacer(Modifier.height(16.dp))

                // ── Already have an account ──
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Already have an account? ",
                        style = MaterialTheme.typography.bodySmall,
                        color = ObsidianTheme.textSecondary
                    )
                    Text(
                        text     = "Sign in",
                        style    = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color    = ObsidianTheme.accent,
                        modifier = Modifier.clickable(
                            indication        = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onNavigateToLogin() }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Logo mark — mirrors SignInScreen but with a different glyph tint
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SignupLogoMark() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(ObsidianTheme.accent.copy(0.55f), ObsidianTheme.surfaceElevated)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
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
            text  = "Your account, your world",
            style = MaterialTheme.typography.bodySmall,
            color = ObsidianTheme.textMuted
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Show / hide password toggle row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PasswordToggleRow(
    visible          : Boolean,
    onToggle         : () -> Unit,
    trailingContent  : @Composable () -> Unit = {}
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    indication        = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onToggle() }
                .padding(top = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector        = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                contentDescription = null,
                tint               = ObsidianTheme.textMuted,
                modifier           = Modifier.size(15.dp)
            )
            Text(
                text  = if (visible) "Hide" else "Show",
                style = MaterialTheme.typography.labelSmall,
                color = ObsidianTheme.textMuted
            )
        }
        Box(Modifier.padding(top = 8.dp)) { trailingContent() }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Password strength meter — 4 segmented bars
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PasswordStrengthMeter(strength: PasswordStrength) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            (1..4).forEach { segment ->
                val filled  = segment <= strength.score
                val barColor by animateFloatAsState(
                    targetValue   = if (filled) 1f else 0f,
                    animationSpec = tween(220),
                    label         = "bar_$segment"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            // Animate between filled accent color and dim surface
                            if (filled) strength.color.copy(alpha = barColor)
                            else ObsidianTheme.surfaceBorder
                        )
                )
            }
        }
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text  = "Password strength",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = ObsidianTheme.textMuted
            )
            if (strength.label.isNotEmpty()) {
                Text(
                    text  = strength.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 10.sp
                    ),
                    color = strength.color
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Passwords-match indicator pill
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MatchIndicator(matches: Boolean) {
    val color = if (matches) ObsidianTheme.green else ObsidianTheme.red
    val label = if (matches) "✓ Match" else "✗ No match"
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(0.14f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize   = 10.sp
            ),
            color = color
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Terms & privacy notice
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TermsNotice() {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Checkbox placeholder pill
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(ObsidianTheme.accentSoft)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("✓", color = ObsidianTheme.accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text  = "By creating an account you agree to our Terms of Service and Privacy Policy.",
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = ObsidianTheme.textMuted
        )
    }
}