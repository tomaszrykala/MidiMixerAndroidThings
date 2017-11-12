package com.tomaszrykala.midimixerandroidthings.midi

import com.tomaszrykala.common.MidiEventType
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

        fun noteOn(channel: Int, note: Int, velocity: Int) =
                MidiEvent(MidiEventType.STATUS_NOTE_ON.byte, channel.toByte(), note.toByte(), velocity.toByte())

        fun noteOff(channel: Int, note: Int, velocity: Int) =
                MidiEvent(MidiEventType.STATUS_NOTE_OFF.byte, channel.toByte(), note.toByte(), velocity.toByte())

        fun controlChange(channel: Int, note: Int, velocity: Int) =
                MidiEvent(MidiEventType.STATUS_CONTROL_CHANGE.byte, channel.toByte(), note.toByte(), velocity.toByte())
    }
}