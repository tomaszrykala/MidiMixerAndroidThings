package com.tomaszrykala.midimixerandroidthings.control

import android.view.KeyEvent

enum class MidiButton(val key: Int, val channel: Byte) {

    BTN_CH1(KeyEvent.KEYCODE_1, 11), BTN_CH2(KeyEvent.KEYCODE_2, 12)
}