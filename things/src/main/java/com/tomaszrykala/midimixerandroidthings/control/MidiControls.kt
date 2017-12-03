package com.tomaszrykala.midimixerandroidthings.control

import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract


class MidiControls(private val presenter: MidiControllerContract.Presenter) {

    private val mcpController: MCP3008.Controller = MCP3008.Controller().apply { start() }
    private val midiPots: MutableList<MidiPot> = mutableListOf()
    val midiButtons: MutableList<MidiButton> = mutableListOf()

    private lateinit var buttonInputDriverOne: ButtonInputDriver
    private lateinit var buttonInputDriverTwo: ButtonInputDriver

    fun onStart() {
        buttonInputDriverOne = buttonInputDriver(MidiButton.BTN_CH1).apply {
            register()
            midiButtons.add(MidiButton.BTN_CH1)
        }
        buttonInputDriverTwo = buttonInputDriver(MidiButton.BTN_CH2).apply {
            register()
            midiButtons.add(MidiButton.BTN_CH2)
        }

        (0..3).mapTo(midiPots) {
            MidiPot(mcpController, presenter, it, (midiButtons.size + it).toByte()).apply { start() }
        }
    }

    private fun buttonInputDriver(button: MidiButton): ButtonInputDriver {
        return ButtonInputDriver(button.pin, Button.LogicState.PRESSED_WHEN_LOW, button.key)
    }

    fun onClose() {
        buttonInputDriverOne.unregister()
        buttonInputDriverTwo.unregister()
        buttonInputDriverOne.close()
        buttonInputDriverTwo.close()

        midiPots.forEach { it.stop() }
        mcpController.stop()
    }
}