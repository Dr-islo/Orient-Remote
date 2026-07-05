package com.orientremote.app.data.model

/**
 * Supported remote brands. Only [ORIENT] ships with real profiles in v1; the rest exist so the
 * brand-selection screen and repository layer never need to change shape when new brands are
 * added — only new [IrProfile] entries need to be supplied.
 */
enum class Brand(val displayName: String, val isAvailable: Boolean) {
    ORIENT("Orient", isAvailable = true),
    SAMSUNG("Samsung", isAvailable = false),
    LG("LG", isAvailable = false),
    SONY("Sony", isAvailable = false),
    TCL("TCL", isAvailable = false),
    HAIER("Haier", isAvailable = false),
    PANASONIC("Panasonic", isAvailable = false),
    PHILIPS("Philips", isAvailable = false),
    HISENSE("Hisense", isAvailable = false);

    companion object {
        fun fromNameOrNull(name: String?): Brand? = entries.find { it.name == name }
    }
}
