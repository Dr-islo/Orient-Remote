package com.orientremote.app.data

import com.orientremote.app.data.local.OrientIrDatabase
import com.orientremote.app.data.model.Brand
import com.orientremote.app.data.model.RemoteButton
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OrientIrDatabaseTest {

    @Test
    fun `database is not empty`() {
        assertTrue(OrientIrDatabase.profiles.isNotEmpty())
    }

    @Test
    fun `every profile belongs to Orient brand`() {
        assertTrue(OrientIrDatabase.profiles.all { it.brand == Brand.ORIENT })
    }

    @Test
    fun `every profile has a code for every remote button`() {
        OrientIrDatabase.profiles.forEach { profile ->
            RemoteButton.entries.forEach { button ->
                assertNotNull(
                    "Profile ${profile.codeSetNumber} missing code for $button",
                    profile.codeFor(button)
                )
            }
        }
    }

    @Test
    fun `code set numbers are sequential starting at 1`() {
        OrientIrDatabase.profiles.forEachIndexed { index, profile ->
            assertEquals(index + 1, profile.codeSetNumber)
        }
    }

    @Test
    fun `profileByCodeSetNumber returns correct profile`() {
        val profile = OrientIrDatabase.profileByCodeSetNumber(1)
        assertNotNull(profile)
        assertEquals(1, profile?.codeSetNumber)
    }

    @Test
    fun `profileByCodeSetNumber returns null for out-of-range values`() {
        assertEquals(null, OrientIrDatabase.profileByCodeSetNumber(0))
        assertEquals(null, OrientIrDatabase.profileByCodeSetNumber(9999))
    }

    @Test
    fun `all profile ids are unique`() {
        val ids = OrientIrDatabase.profiles.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }
}
