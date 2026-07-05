package com.orientremote.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orientremote.app.data.model.Brand
import com.orientremote.app.data.model.IrProfile
import com.orientremote.app.data.model.RemoteButton
import com.orientremote.app.data.repository.RemoteRepository
import com.orientremote.app.util.VibrationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Which step of onboarding is currently shown. */
enum class OnboardingStep { WELCOME, BRAND_SELECTION, CODE_SEARCH, SUCCESS, EXHAUSTED }

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val hasIrHardware: Boolean = true,
    val selectedBrand: Brand? = null,
    val candidates: List<IrProfile> = emptyList(),
    val currentIndex: Int = 0,
    val savedProfile: IrProfile? = null
) {
    val totalCandidates: Int get() = candidates.size
    val currentProfile: IrProfile? get() = candidates.getOrNull(currentIndex)
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: RemoteRepository,
    private val vibrationHelper: VibrationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OnboardingUiState(hasIrHardware = repository.hasIrHardware)
    )
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun onGetStarted() {
        _uiState.value = _uiState.value.copy(step = OnboardingStep.BRAND_SELECTION)
    }

    fun onBrandSelected(brand: Brand) {
        val candidates = repository.candidateProfiles(brand)
        _uiState.value = _uiState.value.copy(
            selectedBrand = brand,
            candidates = candidates,
            currentIndex = 0,
            step = OnboardingStep.CODE_SEARCH
        )
        sendCurrentTestSignal()
    }

    /** Re-sends the Power code for the code currently being tested. */
    fun retrySignal() {
        sendCurrentTestSignal()
    }

    private fun sendCurrentTestSignal() {
        val profile = _uiState.value.currentProfile ?: return
        vibrationHelper.tick()
        repository.sendCommand(RemoteButton.POWER, profile)
    }

    fun onTvRespondedYes() {
        val profile = _uiState.value.currentProfile ?: return
        viewModelScope.launch {
            repository.saveProfile(profile)
            _uiState.value = _uiState.value.copy(step = OnboardingStep.SUCCESS, savedProfile = profile)
        }
    }

    fun onTvRespondedNo() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.totalCandidates) {
            _uiState.value = state.copy(step = OnboardingStep.EXHAUSTED)
            return
        }
        _uiState.value = state.copy(currentIndex = nextIndex)
        sendCurrentTestSignal()
    }

    fun restartSearch() {
        _uiState.value = _uiState.value.copy(currentIndex = 0, step = OnboardingStep.CODE_SEARCH)
        sendCurrentTestSignal()
    }

    fun changeBrand() {
        _uiState.value = _uiState.value.copy(step = OnboardingStep.BRAND_SELECTION)
    }
}
