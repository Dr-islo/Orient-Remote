package com.orientremote.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.orientremote.app.data.model.Brand
import com.orientremote.app.ui.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "orient_remote_prefs")

/**
 * Single source of truth for everything the app needs to remember between launches: the paired
 * TV profile, and user preferences. Everything is stored locally via Jetpack DataStore — never
 * synced anywhere.
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val BRAND = stringPreferencesKey("selected_brand")
        val CODE_SET_NUMBER = intPreferencesKey("selected_code_set_number")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BUTTON_SIZE = stringPreferencesKey("button_size")
        val ANIMATION_SPEED = stringPreferencesKey("animation_speed")
    }

    val onboardingComplete: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.ONBOARDING_COMPLETE] ?: false
    }

    val selectedBrand: Flow<Brand?> = context.dataStore.data.map {
        Brand.fromNameOrNull(it[Keys.BRAND])
    }

    val selectedCodeSetNumber: Flow<Int?> = context.dataStore.data.map {
        it[Keys.CODE_SET_NUMBER]
    }

    val vibrationEnabled: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.VIBRATION_ENABLED] ?: true
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map {
        ThemeMode.fromNameOrNull(it[Keys.THEME_MODE]) ?: ThemeMode.DARK
    }

    val buttonSize: Flow<ButtonSize> = context.dataStore.data.map {
        ButtonSize.fromNameOrNull(it[Keys.BUTTON_SIZE]) ?: ButtonSize.MEDIUM
    }

    val animationSpeed: Flow<AnimationSpeed> = context.dataStore.data.map {
        AnimationSpeed.fromNameOrNull(it[Keys.ANIMATION_SPEED]) ?: AnimationSpeed.NORMAL
    }

    suspend fun saveProfile(brand: Brand, codeSetNumber: Int) {
        context.dataStore.edit {
            it[Keys.BRAND] = brand.name
            it[Keys.CODE_SET_NUMBER] = codeSetNumber
            it[Keys.ONBOARDING_COMPLETE] = true
        }
    }

    suspend fun resetPairing() {
        context.dataStore.edit {
            it[Keys.ONBOARDING_COMPLETE] = false
            it.remove(Keys.BRAND)
            it.remove(Keys.CODE_SET_NUMBER)
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setButtonSize(size: ButtonSize) {
        context.dataStore.edit { it[Keys.BUTTON_SIZE] = size.name }
    }

    suspend fun setAnimationSpeed(speed: AnimationSpeed) {
        context.dataStore.edit { it[Keys.ANIMATION_SPEED] = speed.name }
    }
}

enum class ButtonSize { SMALL, MEDIUM, LARGE;
    companion object {
        fun fromNameOrNull(name: String?): ButtonSize? = entries.find { it.name == name }
    }
}

enum class AnimationSpeed { OFF, NORMAL, FAST;
    companion object {
        fun fromNameOrNull(name: String?): AnimationSpeed? = entries.find { it.name == name }
    }
}
