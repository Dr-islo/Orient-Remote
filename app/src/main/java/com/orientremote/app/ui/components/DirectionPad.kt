package com.orientremote.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orientremote.app.data.model.RemoteButton

/**
 * Premium circular D-pad: four arrow segments around a solid center OK button, matching the
 * layout users expect from a real TV remote.
 */
@Composable
fun DirectionPad(
    animationsEnabled: Boolean,
    onPress: (RemoteButton) -> Unit,
    modifier: Modifier = Modifier
) {
    val ringSize = 220.dp
    val armSize = 64.dp
    val centerSize = 84.dp

    Box(
        modifier = modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(ringSize)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )

        RemoteActionButton(
            modifier = Modifier.align(Alignment.TopCenter),
            icon = Icons.Filled.KeyboardArrowUp,
            size = armSize,
            containerColor = MaterialTheme.colorScheme.surface,
            animationsEnabled = animationsEnabled,
            onPress = { onPress(RemoteButton.NAV_UP) }
        )
        RemoteActionButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            icon = Icons.Filled.KeyboardArrowDown,
            size = armSize,
            containerColor = MaterialTheme.colorScheme.surface,
            animationsEnabled = animationsEnabled,
            onPress = { onPress(RemoteButton.NAV_DOWN) }
        )
        RemoteActionButton(
            modifier = Modifier.align(Alignment.CenterStart),
            icon = Icons.Filled.KeyboardArrowLeft,
            size = armSize,
            containerColor = MaterialTheme.colorScheme.surface,
            animationsEnabled = animationsEnabled,
            onPress = { onPress(RemoteButton.NAV_LEFT) }
        )
        RemoteActionButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            icon = Icons.Filled.KeyboardArrowRight,
            size = armSize,
            containerColor = MaterialTheme.colorScheme.surface,
            animationsEnabled = animationsEnabled,
            onPress = { onPress(RemoteButton.NAV_RIGHT) }
        )

        RemoteActionButton(
            label = "OK",
            size = centerSize,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            animationsEnabled = animationsEnabled,
            onPress = { onPress(RemoteButton.OK) }
        )
    }
}
