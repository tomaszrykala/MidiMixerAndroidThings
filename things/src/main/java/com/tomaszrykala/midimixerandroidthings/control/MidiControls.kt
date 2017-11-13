package com.tomaszrykala.midimixerandroidthings.control

import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract


class MidiControls(private val presenter: MidiControllerContract.Presenter) {

    private lateinit var buttonInputDriverOne: ButtonInputDriver
    private lateinit var buttonInputDriverTwo: ButtonInputDriver
    private lateinit var mcpController: MCP3008.Controller

    val mixerButtons: MutableList<MixerButton> = mutableListOf()

    fun onStart() {

        buttonInputDriverOne = buttonInputDriver(MixerButton.BTN_CH1).apply {
            register()
            mixerButtons.add(MixerButton.BTN_CH1)
        }
        buttonInputDriverTwo = buttonInputDriver(MixerButton.BTN_CH2).apply {
            register()
            mixerButtons.add(MixerButton.BTN_CH2)
        }
        mcpController = MCP3008.Controller().apply { start(); setListener(0, CCListener(presenter)) }
    }

    private fun buttonInputDriver(button: MixerButton): ButtonInputDriver {
        return ButtonInputDriver(button.pin, Button.LogicState.PRESSED_WHEN_LOW, button.key)
    }

    fun onClose() {
        buttonInputDriverOne.unregister()
        buttonInputDriverTwo.unregister()
        buttonInputDriverOne.close()
        buttonInputDriverTwo.close()
        mcpController.stop()
    }

    class CCListener(private val presenter: MidiControllerContract.Presenter) : MCP3008.Listener {

        override fun onChange(read: Int) {
            presenter.onControlChange(read)
        }
    }
}