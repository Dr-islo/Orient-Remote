package com.orientremote.app.ui.screens.remote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orientremote.app.data.local.AnimationSpeed
import com.orientremote.app.data.local.ButtonSize
import com.orientremote.app.data.model.IrProfile
import com.orientremote.app.data.model.RemoteButton
import com.orientremote.app.data.repository.RemoteRepository
import com.orientremote.app.ir.IrTransmissionResult
import com.orientremote.app.util.Constants
import com.orientremote.app.util.VibrationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RemoteUiState(
    val hasIrHardware: Boolean = true,
    val profile: IrProfile? = null,
    val vibrationEnabled: Boolean = true,
    val buttonSize: ButtonSize = ButtonSize.MEDIUM,
    val animationSpeed: AnimationSpeed = AnimationSpeed.NORMAL,
    val lastError: String? = null
)

/** Buttons that support press-and-hold-to-repeat behavior, per the SRS. */
private val REPEATABLE_BUTTONS = setOf(
    RemoteButton.VOLUME_UP,
    RemoteButton.VOLUME_DOWN,
    RemoteButton.CHANNEL_UP,
    RemoteButton.CHANNEL_DOWN
)

@HiltViewModel
class RemoteViewModel @Inject constructor(
    private val repository: RemoteRepository,
    private val vibrationHelper: VibrationHelper
) : ViewModel() {

    val uiState: StateFlow<RemoteUiState> = combine(
        repository.activeProfile,
        repository.vibrationEnabled,
        repository.buttonSize,
        repository.animationSpeed
    ) { profile, vibration, buttonSize, animationSpeed ->
        RemoteUiState(
            hasIrHardware = repository.hasIrHardware,
            profile = profile,
            vibrationEnabled = vibration,
            buttonSize = buttonSize,
            animationSpeed = animationSpeed
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RemoteUiState())

    private var repeatJob: Job? = null

    fun isRepeatable(button: RemoteButton): Boolean = button in REPEATABLE_BUTTONS

    /** Single press: haptic tick + immediate transmit. */
    fun onPress(button: RemoteButton) {
        maybeVibrate()
        transmit(button)
    }

    /** Called once when a repeatable button's hold begins. */
    fun onHoldStart(button: RemoteButton) {
        if (!isRepeatable(button)) return
        repeatJob?.cancel()
        repeatJob = viewModelScope.launch {
            delay(Constants.LONG_PRESS_INITIAL_DELAY_MS)
            while (true) {
                transmit(button)
                maybeVibrate()
                delay(Constants.LONG_PRESS_REPEAT_INTERVAL_MS)
            }
        }
    }

    /** Called when the finger is released — stops repeat immediately, per the SRS. */
    fun onHoldEnd() {
        repeatJob?.cancel()
        repeatJob = null
    }

    private fun maybeVibrate() {
        if (uiState.value.vibrationEnabled) vibrationHelper.tick()
    }

    private fun transmit(button: RemoteButton) {
        when (val result = repository.sendCommand(button, uiState.value.profile)) {
            is IrTransmissionResult.Failure -> setError(result.message)
            IrTransmissionResult.NoIrHardware -> setError("This device does not contain an infrared transmitter.")
            IrTransmissionResult.Success -> clearError()
        }
    }

    private fun setError(message: String) {
        _errorState.value = message
    }

    private fun clearError() {
        _errorState.value = null
    }

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun consumeError() {
        _errorState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        repeatJob?.cancel()
    }
}
