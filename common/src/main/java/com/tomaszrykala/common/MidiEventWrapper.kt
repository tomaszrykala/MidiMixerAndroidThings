package com.tomaszrykala.common

class MidiEventWrapper constructor(val type: MidiEventType, val data: ByteArray) {

    constructor(type: MidiEventType, channel: Byte, note: Byte, pressure: Byte) :
            this(type, byteArrayOf(channel, note, pressure))

    init {
        if (data.size != 3) throw IllegalArgumentException("Input data must be of size 3")
    }

    fun type(): Byte {
        return type.byte
    }

    fun channel(): Byte {
        return data[0]
    }

    fun note(): Byte {
        return data[1]
    }

    fun pressure(): Byte {
        return data[2]
    }
}
