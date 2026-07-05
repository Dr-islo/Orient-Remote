package com.orientremote.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orientremote.app.data.repository.RemoteRepository
import com.orientremote.app.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AppRootState(
    val isLoading: Boolean = true,
    val onboardingComplete: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.DARK
)

@HiltViewModel
class AppRootViewModel @Inject constructor(
    repository: RemoteRepository
) : ViewModel() {

    val state: StateFlow<AppRootState> = combine(
        repository.onboardingComplete,
        repository.themeMode
    ) { onboardingComplete, themeMode ->
        AppRootState(isLoading = false, onboardingComplete = onboardingComplete, themeMode = themeMode)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppRootState())
}
