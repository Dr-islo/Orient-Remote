@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.orientremote.app.ui.screens.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orientremote.app.data.local.ButtonSize
import com.orientremote.app.data.model.RemoteButton
import com.orientremote.app.ui.components.DirectionPad
import com.orientremote.app.ui.components.RemoteActionButton
import com.orientremote.app.ui.components.RemoteButtonShape
import com.orientremote.app.ui.theme.ColorBlue
import com.orientremote.app.ui.theme.ColorGreen
import com.orientremote.app.ui.theme.ColorRed
import com.orientremote.app.ui.theme.ColorYellow
import com.orientremote.app.ui.theme.DangerRed

private fun ButtonSize.toDp() = when (this) {
    ButtonSize.SMALL -> 46.dp
    ButtonSize.MEDIUM -> 56.dp
    ButtonSize.LARGE -> 66.dp
}

@Composable
fun RemoteScreen(
    onOpenSettings: () -> Unit,
    viewModel: RemoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val error by viewModel.errorState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeError()
        }
    }

    val btn = uiState.buttonSize.toDp()
    val animationsEnabled = uiState.animationSpeed != com.orientremote.app.data.local.AnimationSpeed.OFF

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orient Remote", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) { data ->
            Snackbar(snackbarData = data)
        } },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (!uiState.hasIrHardware) {
            NoIrHardwareContent(padding)
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Power / Mute row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RemoteActionButton(
                    icon = Icons.Filled.PowerSettingsNew,
                    size = btn,
                    containerColor = DangerRed,
                    contentColor = MaterialTheme.colorScheme.background,
                    animationsEnabled = animationsEnabled,
                    onPress = { viewModel.onPress(RemoteButton.POWER) }
                )
                RemoteActionButton(
                    icon = Icons.Filled.VolumeOff,
                    size = btn,
                    animationsEnabled = animationsEnabled,
                    onPress = { viewModel.onPress(RemoteButton.MUTE) }
                )
                RemoteActionButton(
                    icon = Icons.Filled.Home,
                    size = btn,
                    animationsEnabled = animationsEnabled,
                    onPress = { viewModel.onPress(RemoteButton.HOME) }
                )
                RemoteActionButton(
                    label = "SRC",
                    size = btn,
                    animationsEnabled = animationsEnabled,
                    onPress = { viewModel.onPress(RemoteButton.SOURCE) }
                )
            }

            Spacer(Modifier.height(20.dp))

            // Volume / Channel rockers + D-pad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Rocker(
                    topIcon = Icons.Filled.Add,
                    bottomIcon = Icons.Filled.Remove,
                    label = "VOL",
                    animationsEnabled = animationsEnabled,
                    onTop = { viewModel.onPress(RemoteButton.VOLUME_UP) },
                    onBottom = { viewModel.onPress(RemoteButton.VOLUME_DOWN) },
                    onTopHoldStart = { viewModel.onHoldStart(RemoteButton.VOLUME_UP) },
                    onBottomHoldStart = { viewModel.onHoldStart(RemoteButton.VOLUME_DOWN) },
                    onHoldEnd = { viewModel.onHoldEnd() }
                )

                DirectionPad(
                    animationsEnabled = animationsEnabled,
                    onPress = { viewModel.onPress(it) }
                )

                Rocker(
                    topIcon = Icons.Filled.Add,
                    bottomIcon = Icons.Filled.Remove,
                    label = "CH",
                    animationsEnabled = animationsEnabled,
                    onTop = { viewModel.onPress(RemoteButton.CHANNEL_UP) },
                    onBottom = { viewModel.onPress(RemoteButton.CHANNEL_DOWN) },
                    onTopHoldStart = { viewModel.onHoldStart(RemoteButton.CHANNEL_UP) },
                    onBottomHoldStart = { viewModel.onHoldStart(RemoteButton.CHANNEL_DOWN) },
                    onHoldEnd = { viewModel.onHoldEnd() }
                )
            }

            Spacer(Modifier.height(20.dp))

            // Menu / Back / Exit / Info / Guide / Previous channel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RemoteActionButton(label = "MENU", shape = RemoteButtonShape.ROUNDED_SQUARE, size = btn, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.MENU) })
                RemoteActionButton(icon = Icons.Filled.ArrowBack, size = btn, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.BACK) })
                RemoteActionButton(icon = Icons.Filled.Close, size = btn, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.EXIT) })
                RemoteActionButton(icon = Icons.Filled.Info, size = btn, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.INFO) })
                RemoteActionButton(icon = Icons.Filled.List, size = btn, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.GUIDE) })
            }

            Spacer(Modifier.height(24.dp))

            // Numeric keypad
            NumericKeypad(btn, animationsEnabled) { viewModel.onPress(it) }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RemoteActionButton(label = "PRE-CH", shape = RemoteButtonShape.PILL, size = btn, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.PREVIOUS_CHANNEL) })
                RemoteActionButton(icon = Icons.Filled.Bedtime, size = btn, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.SLEEP) })
                RemoteActionButton(icon = Icons.Filled.Palette, size = btn, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.PICTURE_MODE) })
                RemoteActionButton(icon = Icons.Filled.VolumeUp, size = btn, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.SOUND_MODE) })
            }

            Spacer(Modifier.height(20.dp))

            // Color buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RemoteActionButton(size = 40.dp, containerColor = ColorRed, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.RED) })
                RemoteActionButton(size = 40.dp, containerColor = ColorGreen, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.GREEN) })
                RemoteActionButton(size = 40.dp, containerColor = ColorYellow, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.YELLOW) })
                RemoteActionButton(size = 40.dp, containerColor = ColorBlue, animationsEnabled = animationsEnabled, onPress = { viewModel.onPress(RemoteButton.BLUE) })
            }

            Spacer(Modifier.height(20.dp))

            // Input sources
            InputSourceRow(btn, animationsEnabled) { viewModel.onPress(it) }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Rocker(
    topIcon: androidx.compose.ui.graphics.vector.ImageVector,
    bottomIcon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    animationsEnabled: Boolean,
    onTop: () -> Unit,
    onBottom: () -> Unit,
    onTopHoldStart: () -> Unit,
    onBottomHoldStart: () -> Unit,
    onHoldEnd: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        RemoteActionButton(
            icon = topIcon,
            size = 52.dp,
            shape = RemoteButtonShape.ROUNDED_SQUARE,
            animationsEnabled = animationsEnabled,
            isRepeatable = true,
            onPress = onTop,
            onHoldStart = onTopHoldStart,
            onHoldEnd = onHoldEnd
        )
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        RemoteActionButton(
            icon = bottomIcon,
            size = 52.dp,
            shape = RemoteButtonShape.ROUNDED_SQUARE,
            animationsEnabled = animationsEnabled,
            isRepeatable = true,
            onPress = onBottom,
            onHoldStart = onBottomHoldStart,
            onHoldEnd = onHoldEnd
        )
    }
}

@Composable
private fun NumericKeypad(btn: androidx.compose.ui.unit.Dp, animationsEnabled: Boolean, onPress: (RemoteButton) -> Unit) {
    val rows = listOf(
        listOf(RemoteButton.NUM_1, RemoteButton.NUM_2, RemoteButton.NUM_3),
        listOf(RemoteButton.NUM_4, RemoteButton.NUM_5, RemoteButton.NUM_6),
        listOf(RemoteButton.NUM_7, RemoteButton.NUM_8, RemoteButton.NUM_9),
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                row.forEach { number ->
                    RemoteActionButton(
                        label = digitLabel(number),
                        size = btn,
                        animationsEnabled = animationsEnabled,
                        onPress = { onPress(number) }
                    )
                }
            }
        }
        RemoteActionButton(
            label = "0",
            size = btn,
            animationsEnabled = animationsEnabled,
            onPress = { onPress(RemoteButton.NUM_0) }
        )
    }
}

private fun digitLabel(button: RemoteButton): String = when (button) {
    RemoteButton.NUM_0 -> "0"; RemoteButton.NUM_1 -> "1"; RemoteButton.NUM_2 -> "2"
    RemoteButton.NUM_3 -> "3"; RemoteButton.NUM_4 -> "4"; RemoteButton.NUM_5 -> "5"
    RemoteButton.NUM_6 -> "6"; RemoteButton.NUM_7 -> "7"; RemoteButton.NUM_8 -> "8"
    RemoteButton.NUM_9 -> "9"; else -> ""
}

@Composable
private fun InputSourceRow(btn: androidx.compose.ui.unit.Dp, animationsEnabled: Boolean, onPress: (RemoteButton) -> Unit) {
    val sources = listOf(
        "HDMI" to RemoteButton.HDMI,
        "USB" to RemoteButton.USB,
        "AV" to RemoteButton.AV,
        "ATV" to RemoteButton.ATV,
        "VGA" to RemoteButton.VGA
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        sources.forEach { (label, button) ->
            RemoteActionButton(
                label = label,
                shape = RemoteButtonShape.ROUNDED_SQUARE,
                size = 48.dp,
                animationsEnabled = animationsEnabled,
                onPress = { onPress(button) }
            )
        }
    }
}

@Composable
private fun NoIrHardwareContent(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.height(48.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "This device does not contain an infrared transmitter.",
            style = MaterialTheme.typography.titleLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
