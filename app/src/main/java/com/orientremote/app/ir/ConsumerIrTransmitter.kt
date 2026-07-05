package com.orientremote.app.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import com.orientremote.app.data.model.IrCode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real hardware-backed [IrTransmitter]. Every call is defensive: a device can report having
 * [ConsumerIrManager] as a system service yet still return false for [ConsumerIrManager.hasIrEmitter]
 * (no physical blaster), and [ConsumerIrManager.transmit] can throw if the OS refuses the request
 * (e.g. thermal throttling on some OEM skins) — both cases are surfaced as typed results instead
 * of crashing the app.
 */
@Singleton
class ConsumerIrTransmitter @Inject constructor(
    @ApplicationContext context: Context
) : IrTransmitter {

    private val irManager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    override val hasIrEmitter: Boolean
        get() = irManager?.hasIrEmitter() == true

    override fun transmit(code: IrCode): IrTransmissionResult {
        val manager = irManager ?: return IrTransmissionResult.NoIrHardware
        if (!manager.hasIrEmitter()) return IrTransmissionResult.NoIrHardware

        return try {
            manager.transmit(code.carrierFrequencyHz, code.pattern)
            IrTransmissionResult.Success
        } catch (e: Exception) {
            IrTransmissionResult.Failure(e.message ?: "Unknown IR transmission error")
        }
    }
}
