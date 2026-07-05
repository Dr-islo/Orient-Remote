package com.orientremote.app.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orientremote.app.R
import com.orientremote.app.ui.theme.AccentCyan
import com.orientremote.app.ui.theme.DangerRed

@Composable
fun CodeSearchScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state.step) {
            OnboardingStep.CODE_SEARCH -> CodeSearchInProgress(
                currentIndex = state.currentIndex,
                total = state.totalCandidates,
                onYes = viewModel::onTvRespondedYes,
                onNo = viewModel::onTvRespondedNo,
                onRetry = viewModel::retrySignal
            )
            OnboardingStep.SUCCESS -> CodeSearchResult(
                success = true,
                onPrimaryAction = onFinished,
                onSecondaryAction = null
            )
            OnboardingStep.EXHAUSTED -> CodeSearchResult(
                success = false,
                onPrimaryAction = viewModel::restartSearch,
                onSecondaryAction = viewModel::changeBrand
            )
            else -> Unit
        }
    }
}

@Composable
private fun CodeSearchInProgress(
    currentIndex: Int,
    total: Int,
    onYes: () -> Unit,
    onNo: () -> Unit,
    onRetry: () -> Unit
) {
    Spacer(Modifier.height(24.dp))
    Text(
        text = stringResource(R.string.code_search_title),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(24.dp))

    val progress = if (total == 0) 0f else (currentIndex + 1).toFloat() / total.toFloat()
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth().height(6.dp),
        color = AccentCyan
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(R.string.code_search_progress, currentIndex + 1, total),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(Modifier.height(48.dp))

    androidx.compose.material3.Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(120.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            com.orientremote.app.ui.components.RemoteActionButton(
                icon = Icons.Filled.PowerSettingsNew,
                size = 80.dp,
                containerColor = DangerRed,
                contentColor = Color.White,
                onPress = onRetry
            )
        }
    }

    Spacer(Modifier.height(40.dp))

    Text(
        text = stringResource(R.string.code_search_prompt),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(32.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onNo,
            modifier = Modifier.weight(1f).height(52.dp)
        ) {
            Text(stringResource(R.string.code_search_no))
        }
        Button(
            onClick = onYes,
            modifier = Modifier.weight(1f).height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(R.string.code_search_yes), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CodeSearchResult(
    success: Boolean,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: (() -> Unit)?
) {
    Spacer(Modifier.height(64.dp))
    Icon(
        imageVector = if (success) Icons.Filled.CheckCircle else Icons.Filled.ErrorOutline,
        contentDescription = null,
        tint = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        modifier = Modifier.size(72.dp)
    )
    Spacer(Modifier.height(24.dp))
    Text(
        text = stringResource(if (success) R.string.code_search_success_title else R.string.code_search_exhausted_title),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(if (success) R.string.code_search_success_body else R.string.code_search_exhausted_body),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(40.dp))
    Button(
        onClick = onPrimaryAction,
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Text(if (success) "Start Using Remote" else "Retry Search", fontWeight = FontWeight.SemiBold)
    }
    if (onSecondaryAction != null) {
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onSecondaryAction,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Change Brand")
        }
    }
}
