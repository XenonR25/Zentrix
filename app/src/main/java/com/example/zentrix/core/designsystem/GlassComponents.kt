package com.example.zentrix.core.designsystem


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentrix.ui.theme.ObsidianTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.SolidColor

// core/designsystem/GlassComponents.kt


// Shared top-highlight glass border brush
private val glassBorderBrush
    get() = Brush.verticalGradient(
        listOf(Color.White.copy(0.13f), Color.White.copy(0.02f))
    )

// ─────────────────────────────────────────────────────────────────────────────
// GlassCard
// ─────────────────────────────────────────────────────────────────────────────
//
// A frosted-obsidian container that blurs whatever content scrolls behind it.
// Usage: wrap any section content (login form, settings block, etc.)

@Composable
fun GlassCard(
    hazeState : HazeState,
    modifier  : Modifier = Modifier,
    shape     : Shape    = RoundedCornerShape(28.dp),
    content   : @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            // 1. Clip first — nothing escapes the rounded corners
            .clip(shape)
            // 2. Haze blur — renders inside the clipped bounds
            .hazeEffect(state = hazeState, style = obsidianGlassStyle())
            // 3. Thin dark fill so the card reads as a surface, not just blur
            .background(ObsidianTheme.surfaceElevated.copy(alpha = 0.45f))
            // 4. Glass-edge highlight border
            .border(width = 0.8.dp, brush = glassBorderBrush, shape = shape)
            .padding(24.dp)
    ) {
        Column(content = content)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassTextField
// ─────────────────────────────────────────────────────────────────────────────
//
// Obsidian-styled input field.
// • Unfocused: subtle dark fill + muted border
// • Focused:   accent-tinted border glow + slightly brighter fill

@Composable
fun GlassTextField(
    value               : String,
    onValueChange       : (String) -> Unit,
    label               : String,
    modifier            : Modifier              = Modifier,
    placeholder         : String                = "",
    isError             : Boolean               = false,
    errorMessage        : String?               = null,
    keyboardOptions     : KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation  = VisualTransformation.None,
    leadingIcon         : @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused         by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue   = when {
            isError   -> ObsidianTheme.red.copy(0.75f)
            isFocused -> ObsidianTheme.accent.copy(0.65f)
            else      -> ObsidianTheme.surfaceBorder.copy(0.85f)
        },
        animationSpec = tween(180),
        label         = "tf_border"
    )
    val fillColor by animateColorAsState(
        targetValue   = if (isFocused) ObsidianTheme.surfaceElevated.copy(0.75f)
        else           ObsidianTheme.surfaceElevated.copy(0.50f),
        animationSpec = tween(180),
        label         = "tf_fill"
    )

    val fieldShape = RoundedCornerShape(16.dp)

    Column(modifier = modifier.fillMaxWidth()) {
        // Label above the field
        if (label.isNotEmpty()) {
            Text(
                text  = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight    = FontWeight.SemiBold,
                    fontSize      = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                color    = if (isFocused) ObsidianTheme.accent else ObsidianTheme.textSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        BasicTextField(
            value                = value,
            onValueChange        = onValueChange,
            singleLine           = true,
            interactionSource    = interactionSource,
            keyboardOptions      = keyboardOptions,
            visualTransformation = visualTransformation,
            textStyle            = TextStyle(
                color      = ObsidianTheme.textPrimary,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Normal
            ),
            cursorBrush = SolidColor(ObsidianTheme.accent),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(fieldShape)
                        .background(fillColor)
                        .border(0.8.dp, borderColor, fieldShape)
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth().align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (leadingIcon != null) {
                            leadingIcon()
                            Spacer(Modifier.width(10.dp))
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            // Placeholder
                            if (value.isEmpty() && placeholder.isNotEmpty()) {
                                Text(
                                    text  = placeholder,
                                    style = TextStyle(
                                        color = ObsidianTheme.textMuted,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            }
        )

        // Error message
        if (isError && !errorMessage.isNullOrEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text  = errorMessage,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = ObsidianTheme.red
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassButton
// ─────────────────────────────────────────────────────────────────────────────
//
// Three visual variants that share the same shape and sizing:
//   Primary   — solid accent fill  (main CTA)
//   Secondary — glass/ghost style  (secondary action)
//   Danger    — red-tinted glass   (destructive action)

enum class GlassButtonVariant { Primary, Secondary, Danger }

@Composable
fun GlassButton(
    text      : String,
    onClick   : () -> Unit,
    modifier  : Modifier           = Modifier,
    variant   : GlassButtonVariant = GlassButtonVariant.Primary,
    isLoading : Boolean            = false,
    enabled   : Boolean            = true
) {
    val shape = RoundedCornerShape(16.dp)

    val (fillBrush, borderBrush, textColor) = when (variant) {
        GlassButtonVariant.Primary -> Triple(
            Brush.linearGradient(listOf(ObsidianTheme.accent, Color(0xFF9B8CF8))),
            Brush.linearGradient(listOf(ObsidianTheme.accent.copy(0.6f), ObsidianTheme.accent.copy(0.2f))),
            Color.White
        )
        GlassButtonVariant.Secondary -> Triple(
            Brush.linearGradient(listOf(ObsidianTheme.surfaceElevated.copy(0.80f), ObsidianTheme.surfaceElevated.copy(0.60f))),
            glassBorderBrush,
            ObsidianTheme.textPrimary
        )
        GlassButtonVariant.Danger -> Triple(
            Brush.linearGradient(listOf(ObsidianTheme.red.copy(0.22f), ObsidianTheme.red.copy(0.10f))),
            Brush.linearGradient(listOf(ObsidianTheme.red.copy(0.60f), ObsidianTheme.red.copy(0.20f))),
            ObsidianTheme.red
        )
    }

    val alpha = if (enabled && !isLoading) 1f else 0.45f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(shape)
            .background(brush = fillBrush, alpha = alpha)
            .border(0.8.dp, borderBrush, shape)
            // Ripple-free tap; add Indication if you want ripple
            .then(
                if (enabled && !isLoading)
                    Modifier.clickable(
                        indication        = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick           = onClick
                    )
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier  = Modifier.size(22.dp),
                color     = textColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text  = text,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp
                ),
                color = textColor.copy(alpha = alpha)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassDivider
// ─────────────────────────────────────────────────────────────────────────────
//
// A fine horizontal rule that matches the obsidian surface border tone.
// Useful between form sections inside a GlassCard.

@Composable
fun GlassDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(0.8.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        ObsidianTheme.surfaceBorder.copy(0.9f),
                        Color.Transparent
                    )
                )
            )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// GlassBadge
// ─────────────────────────────────────────────────────────────────────────────
//
// Reusable label pill — accent, success, error or neutral.
// Matches the TagBadge style used on product cards in HomeScreen.

enum class GlassBadgeVariant { Accent, Success, Error, Neutral }

@Composable
fun GlassBadge(
    text     : String,
    modifier : Modifier          = Modifier,
    variant  : GlassBadgeVariant = GlassBadgeVariant.Accent
) {
    val color = when (variant) {
        GlassBadgeVariant.Accent   -> ObsidianTheme.accent
        GlassBadgeVariant.Success  -> ObsidianTheme.green
        GlassBadgeVariant.Error    -> ObsidianTheme.red
        GlassBadgeVariant.Neutral  -> ObsidianTheme.textSecondary
    }
    val badgeShape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .clip(badgeShape)
            .background(color.copy(0.18f))
            .border(0.5.dp, color.copy(0.45f), badgeShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text  = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                fontSize      = 9.sp
            ),
            color = color
        )
    }
}
// ─────────────────────────────────────────────────────────────────────────────
// Glass style – obsidian frosted glass used by the nav bar
// ─────────────────────────────────────────────────────────────────────────────

 fun obsidianGlassStyle() = HazeStyle(
    tint        = HazeTint(Color(0xFF0A0A0C).copy(alpha = 0.62f)),
    blurRadius  = 30.dp,
    noiseFactor = 0.05f
)
