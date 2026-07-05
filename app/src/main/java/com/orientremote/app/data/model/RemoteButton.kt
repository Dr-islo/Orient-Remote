package com.orientremote.app.data.model

/**
 * Every button the Orient Remote UI can expose. Each value maps to a key inside an
 * [IrProfile]'s code map. Keeping this as a sealed enum (rather than raw strings scattered
 * through the codebase) means adding a new device category later only requires adding new
 * entries here plus corresponding codes in the database — no UI rewiring.
 */
enum class RemoteButton {
    POWER,
    MUTE,
    VOLUME_UP,
    VOLUME_DOWN,
    CHANNEL_UP,
    CHANNEL_DOWN,
    PREVIOUS_CHANNEL,
    MENU,
    HOME,
    EXIT,
    BACK,
    INFO,
    GUIDE,
    SOURCE,
    NAV_UP,
    NAV_DOWN,
    NAV_LEFT,
    NAV_RIGHT,
    OK,
    NUM_0,
    NUM_1,
    NUM_2,
    NUM_3,
    NUM_4,
    NUM_5,
    NUM_6,
    NUM_7,
    NUM_8,
    NUM_9,
    SLEEP,
    PICTURE_MODE,
    SOUND_MODE,
    RED,
    GREEN,
    YELLOW,
    BLUE,
    USB,
    HDMI,
    AV,
    ATV,
    VGA
}
