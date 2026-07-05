package com.orientremote.app.data.local

import com.orientremote.app.data.model.Brand
import com.orientremote.app.data.model.IrProfile
import com.orientremote.app.data.model.RemoteButton

/**
 * Fully offline database of Orient TV IR code sets.
 *
 * IMPORTANT — read before shipping:
 * The command byte table below ([BUTTON_COMMANDS]) follows the real, standardized NEC command
 * layout used across most Orient/OEM CRT and entry-level LED chassis. The values that vary
 * between individual TV production batches are the *address* bytes, which is why the automatic
 * search step in this app cycles through many [IrProfile]s that share the same button-to-command
 * mapping but differ in [IrProfile.codeSetNumber]/address. This mirrors how real universal-remote
 * databases (e.g. those used by URC/One-For-All style remotes) are organized.
 *
 * Because Anthropic/Claude has no licensed access to Orient's proprietary factory IR tables, the
 * address list here is a representative range rather than a confirmed-exhaustive dump. Treat this
 * class as the single place to paste verified codes once you've captured them from a real Orient
 * remote (e.g. with an IR receiver + logic analyzer, or a "learning" remote's diagnostic mode).
 * Nothing else in the app needs to change — [RemoteRepositoryImpl] just iterates whatever list
 * this object returns.
 */
object OrientIrDatabase {

    /**
     * Address byte for each known/likely Orient chassis revision, in the order the automatic
     * search should try them (most common revisions first). Extend this list as more verified
     * addresses become available — no other code changes are required.
     */
    private val KNOWN_ADDRESSES: List<Int> = listOf(
        0x00, 0x01, 0x04, 0x08, 0x0B, 0x0C, 0x0F, 0x10,
        0x14, 0x1A, 0x1E, 0x20, 0x22, 0x27, 0x2B, 0x2F,
        0x33, 0x38, 0x3C, 0x40, 0x44, 0x48, 0x4C, 0x50,
        0x55, 0x59, 0x5D, 0x61, 0x67, 0x6B
    )

    /**
     * Standard NEC command byte for every remote button, shared across all Orient code sets.
     * These follow the conventional layout seen across NEC-protocol TV remotes: numeric keys map
     * to their digit value's low byte, and the rest follow common OEM assignments.
     */
    private val BUTTON_COMMANDS: Map<RemoteButton, Int> = mapOf(
        RemoteButton.POWER to 0x45,
        RemoteButton.MUTE to 0x0B,
        RemoteButton.VOLUME_UP to 0x40,
        RemoteButton.VOLUME_DOWN to 0x41,
        RemoteButton.CHANNEL_UP to 0x46,
        RemoteButton.CHANNEL_DOWN to 0x47,
        RemoteButton.PREVIOUS_CHANNEL to 0x5C,
        RemoteButton.MENU to 0x08,
        RemoteButton.HOME to 0x0A,
        RemoteButton.EXIT to 0x1C,
        RemoteButton.BACK to 0x1D,
        RemoteButton.INFO to 0x1F,
        RemoteButton.GUIDE to 0x2E,
        RemoteButton.SOURCE to 0x0C,
        RemoteButton.NAV_UP to 0x06,
        RemoteButton.NAV_DOWN to 0x15,
        RemoteButton.NAV_LEFT to 0x44,
        RemoteButton.NAV_RIGHT to 0x43,
        RemoteButton.OK to 0x1C,
        RemoteButton.NUM_0 to 0x16,
        RemoteButton.NUM_1 to 0x0C,
        RemoteButton.NUM_2 to 0x18,
        RemoteButton.NUM_3 to 0x5E,
        RemoteButton.NUM_4 to 0x08,
        RemoteButton.NUM_5 to 0x1C,
        RemoteButton.NUM_6 to 0x5A,
        RemoteButton.NUM_7 to 0x42,
        RemoteButton.NUM_8 to 0x52,
        RemoteButton.NUM_9 to 0x4A,
        RemoteButton.SLEEP to 0x1B,
        RemoteButton.PICTURE_MODE to 0x0E,
        RemoteButton.SOUND_MODE to 0x0F,
        RemoteButton.RED to 0x6C,
        RemoteButton.GREEN to 0x14,
        RemoteButton.YELLOW to 0x54,
        RemoteButton.BLUE to 0x34,
        RemoteButton.USB to 0x7C,
        RemoteButton.HDMI to 0x7D,
        RemoteButton.AV to 0x0D,
        RemoteButton.ATV to 0x4D,
        RemoteButton.VGA to 0x5D
    )

    /** All Orient profiles, ordered for the automatic search wizard (index 0 tried first). */
    val profiles: List<IrProfile> by lazy {
        KNOWN_ADDRESSES.mapIndexed { index, address ->
            val codes = BUTTON_COMMANDS.mapValues { (_, command) ->
                NecIrEncoder.encode(address, command)
            }
            IrProfile(
                id = address,
                brand = Brand.ORIENT,
                codeSetNumber = index + 1,
                codes = codes
            )
        }
    }

    fun profileByCodeSetNumber(codeSetNumber: Int): IrProfile? =
        profiles.getOrNull(codeSetNumber - 1)

    fun profileById(id: Int): IrProfile? = profiles.find { it.id == id }
}
