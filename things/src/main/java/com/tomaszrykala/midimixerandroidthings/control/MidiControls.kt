package com.tomaszrykala.midimixerandroidthings.control

import android.view.KeyEvent
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

//interface MidiMixerButton {
//
//    // var isPressed : Boolean
//
//    fun getPin(): String
//
//    fun getKey(): Int
//}
//
//class MidiMixerButtonA : com.tomaszrykala.midimixerandroidthings.control.MidiMixerButton {
//
//    override fun getPin(): String {
//        return "BCM21"
//    }
//
//    override fun getKey(): Int {
//        return KeyEvent.KEYCODE_1
//    }
//}
//
//class MidiMixerButtonB : com.tomaszrykala.midimixerandroidthings.control.MidiMixerButton {
//
//    override fun getPin(): String {
//        return "BCM20"
//    }
//
//    override fun getKey(): Int {
//        return KeyEvent.KEYCODE_2
//    }
//}

enum class MixerButton(val pin: String, val key: Int, val channel: Byte) {

    BTN_CH1("BCM21", KeyEvent.KEYCODE_1, 0), BTN_CH2("BCM20", KeyEvent.KEYCODE_2, 1)
}


class MidiControls {

    private lateinit var buttonA: Button
    private lateinit var buttonB: Button
    private lateinit var buttonC: Button

    private lateinit var buttonInputDriverOne: ButtonInputDriver
    private lateinit var buttonInputDriverTwo: ButtonInputDriver

    val keys: IntArray = intArrayOf(10, 20, 30, 40, 50)
    val mixerButtons: MutableList<MixerButton> = mutableListOf()

    fun onStart(presenter: MidiControllerContract.Presenter) {

        buttonInputDriverOne = buttonInputDriver(MixerButton.BTN_CH1).apply {
            register(); mixerButtons.add(MixerButton.BTN_CH1)
        }
        buttonInputDriverTwo = buttonInputDriver(MixerButton.BTN_CH2).apply {
            register(); mixerButtons.add(MixerButton.BTN_CH2)
        }

//        buttonA = RainbowHat.openButtonA().apply {
//            setOnButtonEventListener { _, pressed ->
//                presenter.onPressed(MidiButton(0, pressed))
//            }
//        }
//
//        buttonB = RainbowHat.openButtonB().apply {
//            setOnButtonEventListener { _, pressed ->
//                presenter.onPressed(MidiButton(1, pressed))
//            }
//        }
//
//        buttonC = RainbowHat.openButtonC().apply {
//            setOnButtonEventListener { _, _ ->
//                presenter.onReset()
//            }
//        }
    }

    private fun buttonInputDriver(button: MixerButton): ButtonInputDriver {
        return ButtonInputDriver(button.pin, Button.LogicState.PRESSED_WHEN_LOW, button.key)
    }

    fun onClose() {
        buttonInputDriverOne.unregister()
        buttonInputDriverTwo.unregister()
        buttonInputDriverOne.close()
        buttonInputDriverTwo.close()

        buttonA.close()
        buttonB.close()
        buttonC.close()
    }
}