package com.tomaszrykala.common

class MidiEventWrapper(val data: ByteArray) {

    init {
        if (data.size != 3) throw IllegalArgumentException("Input data must be of size 3")
    }

    fun channel(): Int {
        return data[0].toInt()
    }

    fun note(): Int {
        return data[1].toInt()
    }

    fun pressure(): Float {
        return data[2].toFloat()
    }
}
