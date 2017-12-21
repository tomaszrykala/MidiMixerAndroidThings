package com.tomaszrykala.midimixerandroidthings.control

import android.view.KeyEvent

enum class MidiButton(val key: Int, val channel: Byte) {

    BTN_CH0(KeyEvent.KEYCODE_0, 0), BTN_CH1(KeyEvent.KEYCODE_1, 1), BTN_CH2(KeyEvent.KEYCODE_2, 2), BTN_CH3(KeyEvent.KEYCODE_3, 3)
}