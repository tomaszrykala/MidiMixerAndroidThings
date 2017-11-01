package com.tomaszrykala.midimixerandroidthings.midi

import kotlin.experimental.and
import kotlin.experimental.or

class MidiEvent constructor(
        private val type: Byte,
        private val channel: Byte,
        vararg private val payload: Byte) {

    val bytes: ByteArray
        get() = ByteArray(payload.size + 1) {
            when (it) {
                0 -> type and STATUS_MASK or (channel and CHANNEL_MASK)
                else -> payload[it - 1]
            }
        }

    companion object {
        private const val STATUS_MASK = 0xF0.toByte()
        private const val CHANNEL_MASK = 0x0F.toByte()

        private const val STATUS_NOTE_OFF = 0x80.toByte()
        private const val STATUS_NOTE_ON: Byte = 0x90.toByte()
        private const val STATUS_CONTROL_CHANGE = 0xB0.toByte()

//        private const val STATUS_POLYPHONIC_AFTERTOUCH = 0xA0.toByte()
//        private const val STATUS_PROGRAM_CHANGE = 0xC0.toByte()
//        private const val STATUS_CHANNEL_PRESSURE = 0xD0.toByte()
//        private const val STATUS_PITCH_BEND = 0xE0.toByte()

        fun noteOn(channel: Int, note: Int, velocity: Int) =
                MidiEvent(STATUS_NOTE_ON, channel.toByte(), note.toByte(), velocity.toByte())

        fun noteOff(channel: Int, note: Int, velocity: Int) =
                MidiEvent(STATUS_NOTE_OFF, channel.toByte(), note.toByte(), velocity.toByte())

        fun controlChange(channel: Int, note: Int, velocity: Int) =
                MidiEvent(STATUS_CONTROL_CHANGE, channel.toByte(), note.toByte(), velocity.toByte())
    }
}