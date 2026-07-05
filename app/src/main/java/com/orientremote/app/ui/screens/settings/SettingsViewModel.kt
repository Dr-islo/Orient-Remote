package com.orientremote.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orientremote.app.BuildConfig
import com.orientremote.app.data.local.AnimationSpeed
import com.orientremote.app.data.local.ButtonSize
import com.orientremote.app.data.model.IrProfile
import com.orientremote.app.data.repository.RemoteRepository
import com.orientremote.app.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val profile: IrProfile? = null,
    val vibrationEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val buttonSize: ButtonSize = ButtonSize.MEDIUM,
    val animationSpeed: AnimationSpeed = AnimationSpeed.NORMAL,
    val appVersion: String = BuildConfig.VERSION_NAME
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: RemoteRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        repository.activeProfile,
        repository.vibrationEnabled,
        repository.themeMode,
        repository.buttonSize,
        repository.animationSpeed
    ) { profile, vibration, theme, buttonSize, animationSpeed ->
        SettingsUiState(
            profile = profile,
            vibrationEnabled = vibration,
            themeMode = theme,
            buttonSize = buttonSize,
            animationSpeed = animationSpeed
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setVibrationEnabled(enabled: Boolean) = viewModelScope.launch {
        repository.setVibrationEnabled(enabled)
    }

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch {
        repository.setThemeMode(mode)
    }

    fun setButtonSize(size: ButtonSize) = viewModelScope.launch {
        repository.setButtonSize(size)
    }

    fun setAnimationSpeed(speed: AnimationSpeed) = viewModelScope.launch {
        repository.setAnimationSpeed(speed)
    }

    fun resetPairing(onDone: () -> Unit) = viewModelScope.launch {
        repository.resetPairing()
        onDone()
    }
}
