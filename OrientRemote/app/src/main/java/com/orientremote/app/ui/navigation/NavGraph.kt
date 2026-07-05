package com.orientremote.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.orientremote.app.ui.screens.onboarding.BrandSelectionScreen
import com.orientremote.app.ui.screens.onboarding.CodeSearchScreen
import com.orientremote.app.ui.screens.onboarding.OnboardingViewModel
import com.orientremote.app.ui.screens.onboarding.WelcomeScreen
import com.orientremote.app.ui.screens.remote.RemoteScreen
import com.orientremote.app.ui.screens.settings.SettingsScreen

object Routes {
    const val WELCOME = "welcome"
    const val ONBOARDING_GRAPH = "onboarding_graph"
    const val BRAND_SELECTION = "brand_selection"
    const val CODE_SEARCH = "code_search"
    const val REMOTE = "remote"
    const val SETTINGS = "settings"
}

@Composable
fun OrientNavGraph(
    startDestination: String,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.WELCOME) {
            WelcomeScreen(onGetStarted = {
                navController.navigate(Routes.ONBOARDING_GRAPH)
            })
        }

        // Nested graph so BrandSelection and CodeSearch share one OnboardingViewModel instance,
        // scoped to this graph's back stack entry rather than to each individual destination.
        navigation(startDestination = Routes.BRAND_SELECTION, route = Routes.ONBOARDING_GRAPH) {
            composable(Routes.BRAND_SELECTION) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.ONBOARDING_GRAPH)
                }
                val onboardingViewModel: OnboardingViewModel = hiltViewModel(parentEntry)
                BrandSelectionScreen(onBrandSelected = { brand ->
                    onboardingViewModel.onBrandSelected(brand)
                    navController.navigate(Routes.CODE_SEARCH)
                })
            }

            composable(Routes.CODE_SEARCH) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.ONBOARDING_GRAPH)
                }
                val onboardingViewModel: OnboardingViewModel = hiltViewModel(parentEntry)
                CodeSearchScreen(
                    viewModel = onboardingViewModel,
                    onFinished = {
                        navController.navigate(Routes.REMOTE) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Routes.REMOTE) {
            RemoteScreen(onOpenSettings = { navController.navigate(Routes.SETTINGS) })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onPairingReset = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRetestRemote = {
                    navController.navigate(Routes.ONBOARDING_GRAPH)
                }
            )
        }
    }
}
