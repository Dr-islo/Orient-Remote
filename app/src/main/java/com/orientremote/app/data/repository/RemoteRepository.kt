package com.orientremote.app.data.repository

import com.orientremote.app.data.local.AnimationSpeed
import com.orientremote.app.data.local.ButtonSize
import com.orientremote.app.data.model.Brand
import com.orientremote.app.data.model.IrProfile
import com.orientremote.app.data.model.RemoteButton
import com.orientremote.app.ir.IrTransmissionResult
import com.orientremote.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * The single entry point ViewModels use to read/write pairing state, preferences, and to send IR
 * commands. Hiding [com.orientremote.app.data.local.OrientIrDatabase] and
 * [com.orientremote.app.ir.IrTransmitter] behind this interface keeps the UI layer free of any
 * hardware or storage details, per the app's Clean Architecture / Repository pattern.
 */
interface RemoteRepository {

    val hasIrHardware: Boolean

    val onboardingComplete: Flow<Boolean>
    val activeProfile: Flow<IrProfile?>
    val vibrationEnabled: Flow<Boolean>
    val themeMode: Flow<ThemeMode>
    val buttonSize: Flow<ButtonSize>
    val animationSpeed: Flow<AnimationSpeed>

    /** All known profiles for [brand], in the order the search wizard should try them. */
    fun candidateProfiles(brand: Brand): List<IrProfile>

    /** Persists [profile] as the paired TV and marks onboarding as complete. */
    suspend fun saveProfile(profile: IrProfile)

    /** Clears the paired TV, sending the user back through onboarding. */
    suspend fun resetPairing()

    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setButtonSize(size: ButtonSize)
    suspend fun setAnimationSpeed(speed: AnimationSpeed)

    /** Sends [button]'s code from the given [profile] (or the currently active one if null). */
    fun sendCommand(button: RemoteButton, profile: IrProfile? = null): IrTransmissionResult
}
