package com.orientremote.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.orientremote.app.ui.AppRootViewModel
import com.orientremote.app.ui.navigation.OrientNavGraph
import com.orientremote.app.ui.navigation.Routes
import com.orientremote.app.ui.theme.OrientRemoteTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity app. Startup routing (onboarding vs. main remote) and theme selection are
 * driven by [AppRootViewModel], which reads persisted state from DataStore via the repository.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val rootViewModel: AppRootViewModel = hiltViewModel()
            val rootState by rootViewModel.state.collectAsState()

            OrientRemoteTheme(themeMode = rootState.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (!rootState.isLoading) {
                        val startDestination = if (rootState.onboardingComplete) {
                            Routes.REMOTE
                        } else {
                            Routes.WELCOME
                        }
                        OrientNavGraph(startDestination = startDestination)
                    }
                }
            }
        }
    }
}
