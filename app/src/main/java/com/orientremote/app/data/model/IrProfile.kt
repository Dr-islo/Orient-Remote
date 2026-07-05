package com.orientremote.app.data.model

/**
 * A single raw infrared signal, in the exact shape [android.hardware.ConsumerIrManager] expects:
 * a carrier frequency in Hz, and an alternating on/off pattern expressed in microseconds
 * (pattern[0] is "on" time, pattern[1] is "off" time, and so on — the array length must be even).
 */
data class IrCode(
    val carrierFrequencyHz: Int,
    val pattern: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IrCode) return false
        return carrierFrequencyHz == other.carrierFrequencyHz && pattern.contentEquals(other.pattern)
    }

    override fun hashCode(): Int {
        var result = carrierFrequencyHz
        result = 31 * result + pattern.contentHashCode()
        return result
    }
}

/**
 * A complete IR code set for one physical remote/TV chassis. Real-world universal remote
 * databases number these sequentially per brand (e.g. "Orient code 014"); we mirror that with
 * [codeSetNumber] so the automatic search step and the Settings screen can both display a
 * familiar "Code N" label.
 */
data class IrProfile(
    val id: Int,
    val brand: Brand,
    val codeSetNumber: Int,
    val codes: Map<RemoteButton, IrCode>
) {
    /** Returns the code for [button], or null if this profile has no mapping for it. */
    fun codeFor(button: RemoteButton): IrCode? = codes[button]
}
