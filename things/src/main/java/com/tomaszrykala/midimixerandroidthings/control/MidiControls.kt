package com.tomaszrykala.midimixerandroidthings.control

import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract


class MidiControls(private val presenter: MidiControllerContract.Presenter) {

    private lateinit var buttonInputDriverOne: ButtonInputDriver
    private lateinit var buttonInputDriverTwo: ButtonInputDriver
    private lateinit var midiPotOne: MidiPot

    private lateinit var mcpController: MCP3008.Controller

    val midiButtons: MutableList<MidiButton> = mutableListOf()

    fun onStart() {
        buttonInputDriverOne = buttonInputDriver(MidiButton.BTN_CH1).apply {
            register()
            midiButtons.add(MidiButton.BTN_CH1)
        }
        buttonInputDriverTwo = buttonInputDriver(MidiButton.BTN_CH2).apply {
            register()
            midiButtons.add(MidiButton.BTN_CH2)
        }
        mcpController = MCP3008.Controller().apply { start() }
        midiPotOne = MidiPot(mcpController, presenter, 0, 2).apply { start() }
    }

    private fun buttonInputDriver(button: MidiButton): ButtonInputDriver {
        return ButtonInputDriver(button.pin, Button.LogicState.PRESSED_WHEN_LOW, button.key)
    }

    fun onClose() {
        buttonInputDriverOne.unregister()
        buttonInputDriverTwo.unregister()
        buttonInputDriverOne.close()
        buttonInputDriverTwo.close()

        mcpController.stop()
        midiPotOne.stop()
    }
}