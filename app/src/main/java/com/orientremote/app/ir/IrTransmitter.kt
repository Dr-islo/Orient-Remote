package com.orientremote.app.ir

import com.orientremote.app.data.model.IrCode

/** Result of attempting to transmit a signal. */
sealed interface IrTransmissionResult {
    data object Success : IrTransmissionResult
    data object NoIrHardware : IrTransmissionResult
    data class Failure(val message: String) : IrTransmissionResult
}

/**
 * Abstraction over the device's infrared blaster. Kept as an interface (rather than exposing
 * [android.hardware.ConsumerIrManager] directly to ViewModels) so the transmission engine can be
 * unit-tested with a fake, and so future device categories (AC, fan, projector...) can reuse it
 * without touching the hardware-facing implementation.
 */
interface IrTransmitter {
    /** True if this device has usable IR-blaster hardware. */
    val hasIrEmitter: Boolean

    /** Sends a single raw [IrCode]. Safe to call from any coroutine dispatcher. */
    fun transmit(code: IrCode): IrTransmissionResult
}
