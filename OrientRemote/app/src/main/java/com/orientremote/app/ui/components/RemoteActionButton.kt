package com.orientremote.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orientremote.app.ui.components.RemoteIcon

enum class RemoteButtonShape { CIRCLE, ROUNDED_SQUARE, PILL }

/**
 * The single button primitive used everywhere on the remote screen. Handles:
 * - Ripple + scale-down press animation
 * - Immediate [onPress] on finger-down (matches how a physical remote feels)
 * - Optional hold-to-repeat via [onHoldStart]/[onHoldEnd] for volume/channel buttons
 */
@Composable
fun RemoteActionButton(
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: ImageVector? = null,
    size: Dp = 56.dp,
    shape: RemoteButtonShape = RemoteButtonShape.CIRCLE,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    animationsEnabled: Boolean = true,
    isRepeatable: Boolean = false,
    onPress: () -> Unit,
    onHoldStart: () -> Unit = {},
    onHoldEnd: () -> Unit = {}
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed && animationsEnabled) 0.90f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "buttonPressScale"
    )
    val composeShape: Shape = when (shape) {
        RemoteButtonShape.CIRCLE -> CircleShape
        RemoteButtonShape.ROUNDED_SQUARE -> RoundedCornerShape(16.dp)
        RemoteButtonShape.PILL -> RoundedCornerShape(50)
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .background(containerColor, composeShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        onPress()
                        if (isRepeatable) onHoldStart()
                        try {
                            tryAwaitRelease()
                        } finally {
                            pressed = false
                            if (isRepeatable) onHoldEnd()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            icon != null -> RemoteIcon(icon = icon, tint = contentColor)
            label != null -> Text(
                text = label,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
