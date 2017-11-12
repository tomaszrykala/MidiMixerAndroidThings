package com.tomaszrykala.midimixerandroidthings.control

import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract


class MidiControls {

    private lateinit var buttonA: Button
    private lateinit var buttonB: Button
    private lateinit var buttonC: Button

    private lateinit var buttonInputDriverOne: ButtonInputDriver
    private lateinit var buttonInputDriverTwo: ButtonInputDriver

    val mixerButtons: MutableList<MixerButton> = mutableListOf()

    fun onStart(presenter: MidiControllerContract.Presenter) {

        buttonInputDriverOne = buttonInputDriver(MixerButton.BTN_CH1).apply {
            register(); mixerButtons.add(MixerButton.BTN_CH1)
        }
        buttonInputDriverTwo = buttonInputDriver(MixerButton.BTN_CH2).apply {
            register(); mixerButtons.add(MixerButton.BTN_CH2)
        }
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