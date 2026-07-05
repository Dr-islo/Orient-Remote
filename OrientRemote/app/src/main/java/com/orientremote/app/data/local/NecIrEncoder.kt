package com.orientremote.app.data.local

import com.orientremote.app.data.model.IrCode

/**
 * Encodes standard NEC-protocol infrared frames.
 *
 * The vast majority of budget CRT/LED TV remotes manufactured for South/Southeast Asian markets
 * (the segment Orient's older TV lines belong to) use the NEC protocol: a 9ms/4.5ms leader,
 * 8-bit address + inverted address, 8-bit command + inverted command, each bit encoded as a
 * 560us mark followed by either a 560us (logical 0) or 1690us (logical 1) space, and a final
 * 560us stop mark.
 *
 * Building codes from this well-documented protocol — rather than hand-typing raw microsecond
 * arrays — keeps [OrientIrDatabase] readable and guarantees every generated [IrCode] is a
 * structurally valid signal a real NEC-compatible TV chassis can decode, once the correct
 * address/command byte pair for a given TV batch is known.
 */
object NecIrEncoder {

    private const val CARRIER_HZ = 38000
    private const val LEADER_MARK = 9000
    private const val LEADER_SPACE = 4500
    private const val BIT_MARK = 560
    private const val ZERO_SPACE = 560
    private const val ONE_SPACE = 1690
    private const val STOP_MARK = 560

    /**
     * Builds a full NEC frame for [address] (0-255) and [command] (0-255).
     * The inverted address/command bytes are computed automatically per spec.
     */
    fun encode(address: Int, command: Int): IrCode {
        val pattern = mutableListOf<Int>()
        pattern.add(LEADER_MARK)
        pattern.add(LEADER_SPACE)

        appendByte(pattern, address and 0xFF)
        appendByte(pattern, address.inv() and 0xFF)
        appendByte(pattern, command and 0xFF)
        appendByte(pattern, command.inv() and 0xFF)

        pattern.add(STOP_MARK)

        return IrCode(CARRIER_HZ, pattern.toIntArray())
    }

    private fun appendByte(pattern: MutableList<Int>, byte: Int) {
        for (bitIndex in 0 until 8) {
            val bit = (byte shr bitIndex) and 0x01
            pattern.add(BIT_MARK)
            pattern.add(if (bit == 1) ONE_SPACE else ZERO_SPACE)
        }
    }
}
