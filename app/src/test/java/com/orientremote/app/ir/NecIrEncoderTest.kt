package com.orientremote.app.ir

import com.orientremote.app.data.local.NecIrEncoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NecIrEncoderTest {

    @Test
    fun `encode produces standard 38kHz carrier`() {
        val code = NecIrEncoder.encode(address = 0x00, command = 0x45)
        assertEquals(38000, code.carrierFrequencyHz)
    }

    @Test
    fun `encode produces correct frame length`() {
        // Leader (2) + 4 bytes * 8 bits * 2 (mark+space) + stop bit (1) = 2 + 64 + 1 = 67
        val code = NecIrEncoder.encode(address = 0x01, command = 0x02)
        assertEquals(67, code.pattern.size)
    }

    @Test
    fun `encode starts with NEC leader mark and space`() {
        val code = NecIrEncoder.encode(address = 0x00, command = 0x45)
        assertEquals(9000, code.pattern[0])
        assertEquals(4500, code.pattern[1])
    }

    @Test
    fun `encode ends with stop mark`() {
        val code = NecIrEncoder.encode(address = 0x00, command = 0x45)
        assertEquals(560, code.pattern.last())
    }

    @Test
    fun `different commands produce different patterns`() {
        val powerCode = NecIrEncoder.encode(address = 0x00, command = 0x45)
        val muteCode = NecIrEncoder.encode(address = 0x00, command = 0x0B)
        assertTrue(!powerCode.pattern.contentEquals(muteCode.pattern))
    }

    @Test
    fun `all pattern values are positive`() {
        val code = NecIrEncoder.encode(address = 0x14, command = 0x40)
        assertTrue(code.pattern.all { it > 0 })
    }
}
