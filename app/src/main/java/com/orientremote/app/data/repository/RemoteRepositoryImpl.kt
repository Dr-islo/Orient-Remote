package com.orientremote.app.data.repository

import com.orientremote.app.data.local.AnimationSpeed
import com.orientremote.app.data.local.ButtonSize
import com.orientremote.app.data.local.OrientIrDatabase
import com.orientremote.app.data.local.PreferencesManager
import com.orientremote.app.data.model.Brand
import com.orientremote.app.data.model.IrProfile
import com.orientremote.app.data.model.RemoteButton
import com.orientremote.app.ir.IrTransmissionResult
import com.orientremote.app.ir.IrTransmitter
import com.orientremote.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val irTransmitter: IrTransmitter
) : RemoteRepository {

    override val hasIrHardware: Boolean
        get() = irTransmitter.hasIrEmitter

    override val onboardingComplete: Flow<Boolean> = preferencesManager.onboardingComplete

    override val activeProfile: Flow<IrProfile?> = combine(
        preferencesManager.selectedBrand,
        preferencesManager.selectedCodeSetNumber
    ) { brand, codeSetNumber ->
        if (brand == null || codeSetNumber == null) return@combine null
        when (brand) {
            Brand.ORIENT -> OrientIrDatabase.profileByCodeSetNumber(codeSetNumber)
            else -> null // Future brands plug in here without touching call sites.
        }
    }

    override val vibrationEnabled: Flow<Boolean> = preferencesManager.vibrationEnabled
    override val themeMode: Flow<ThemeMode> = preferencesManager.themeMode
    override val buttonSize: Flow<ButtonSize> = preferencesManager.buttonSize
    override val animationSpeed: Flow<AnimationSpeed> = preferencesManager.animationSpeed

    override fun candidateProfiles(brand: Brand): List<IrProfile> = when (brand) {
        Brand.ORIENT -> OrientIrDatabase.profiles
        else -> emptyList()
    }

    override suspend fun saveProfile(profile: IrProfile) {
        preferencesManager.saveProfile(profile.brand, profile.codeSetNumber)
    }

    override suspend fun resetPairing() {
        preferencesManager.resetPairing()
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) =
        preferencesManager.setVibrationEnabled(enabled)

    override suspend fun setThemeMode(mode: ThemeMode) =
        preferencesManager.setThemeMode(mode)

    override suspend fun setButtonSize(size: ButtonSize) =
        preferencesManager.setButtonSize(size)

    override suspend fun setAnimationSpeed(speed: AnimationSpeed) =
        preferencesManager.setAnimationSpeed(speed)

    override fun sendCommand(button: RemoteButton, profile: IrProfile?): IrTransmissionResult {
        val target = profile ?: return IrTransmissionResult.Failure("No TV paired")
        val code = target.codeFor(button)
            ?: return IrTransmissionResult.Failure("Button not supported by this profile")
        return irTransmitter.transmit(code)
    }
}
