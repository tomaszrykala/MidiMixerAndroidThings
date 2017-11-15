package com.tomaszrykala.common

enum class MidiEventType(val byte: Byte) {

    STATUS_NOTE_OFF(0x80.toByte()), STATUS_NOTE_ON(0x90.toByte()), STATUS_CONTROL_CHANGE(0xB0.toByte())
}