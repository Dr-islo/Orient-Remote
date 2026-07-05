@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.orientremote.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orientremote.app.data.local.AnimationSpeed
import com.orientremote.app.data.local.ButtonSize
import com.orientremote.app.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onPairingReset: () -> Unit,
    onRetestRemote: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SectionCard(title = "TV Pairing") {
                InfoRow("Brand", state.profile?.brand?.displayName ?: "Not paired")
                HorizontalDivider(color = MaterialTheme.colorScheme.background)
                InfoRow("Code Profile", state.profile?.let { "Code ${it.codeSetNumber}" } ?: "-")
                HorizontalDivider(color = MaterialTheme.colorScheme.background)
                ActionRow("Retest Remote", onClick = onRetestRemote)
                HorizontalDivider(color = MaterialTheme.colorScheme.background)
                ActionRow("Reset Pairing", destructive = true, onClick = { showResetDialog = true })
            } }

            item { SectionCard(title = "Appearance") {
                Text("Theme", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                ThemeSelector(state.themeMode, viewModel::setThemeMode)
                Spacer(Modifier.height(16.dp))
                Text("Button Size", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                ButtonSizeSelector(state.buttonSize, viewModel::setButtonSize)
                Spacer(Modifier.height(16.dp))
                Text("Animation Speed", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                AnimationSpeedSelector(state.animationSpeed, viewModel::setAnimationSpeed)
            } }

            item { SectionCard(title = "Feedback") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Vibration", style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = state.vibrationEnabled, onCheckedChange = viewModel::setVibrationEnabled)
                }
            } }

            item { SectionCard(title = "About") {
                InfoRow("Version", state.appVersion)
                HorizontalDivider(color = MaterialTheme.colorScheme.background)
                InfoRow("Privacy", "100% offline • No data collected")
            } }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Pairing?") },
            text = { Text("This will forget your paired TV. You'll need to run the automatic code search again.") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    viewModel.resetPairing(onPairingReset)
                }) { Text("Reset") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) { content() }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ActionRow(label: String, destructive: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onClick) {
            Text(
                label,
                color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ThemeSelector(current: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    val options = listOf(ThemeMode.DARK to "Dark", ThemeMode.LIGHT to "Light", ThemeMode.SYSTEM to "System")
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (mode, label) ->
            SegmentedButton(
                selected = current == mode,
                onClick = { onSelect(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) { Text(label) }
        }
    }
}

@Composable
private fun ButtonSizeSelector(current: ButtonSize, onSelect: (ButtonSize) -> Unit) {
    val options = listOf(ButtonSize.SMALL to "Small", ButtonSize.MEDIUM to "Medium", ButtonSize.LARGE to "Large")
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (size, label) ->
            SegmentedButton(
                selected = current == size,
                onClick = { onSelect(size) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) { Text(label) }
        }
    }
}

@Composable
private fun AnimationSpeedSelector(current: AnimationSpeed, onSelect: (AnimationSpeed) -> Unit) {
    val options = listOf(AnimationSpeed.OFF to "Off", AnimationSpeed.NORMAL to "Normal", AnimationSpeed.FAST to "Fast")
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (speed, label) ->
            SegmentedButton(
                selected = current == speed,
                onClick = { onSelect(speed) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) { Text(label) }
        }
    }
}
