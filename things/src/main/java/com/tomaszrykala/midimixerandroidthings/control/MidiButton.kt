package com.tomaszrykala.midimixerandroidthings.control

data class MidiButton(val channel: Byte, val pressed: Boolean) {
    val note: Byte = 64
}