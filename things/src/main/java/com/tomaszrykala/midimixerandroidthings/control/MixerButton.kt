package com.tomaszrykala.midimixerandroidthings.control

import android.view.KeyEvent

enum class MixerButton(val pin: String, val key: Int, val channel: Byte) {

    BTN_CH1("BCM21", KeyEvent.KEYCODE_1, 0), BTN_CH2("BCM20", KeyEvent.KEYCODE_2, 1)
}