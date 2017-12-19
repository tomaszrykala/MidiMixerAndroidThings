package com.tomaszrykala.midimixerandroidthings.control

import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.tomaszrykala.midimixerandroidthings.control.adc.McpDriverManager
import com.tomaszrykala.midimixerandroidthings.driver.Driver
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiControls(presenter: MidiControllerContract.Presenter) {

    private val driver: Driver = Driver()
    private val mcpDriverManager: McpDriverManager = McpDriverManager(presenter, driver)

    val midiButtons: MutableList<MidiButton> = mutableListOf()
    private lateinit var buttonInputDriverOne: ButtonInputDriver
    private lateinit var buttonInputDriverTwo: ButtonInputDriver

    fun onStart() {
        buttonInputDriverOne = buttonInputDriver(driver.getBtn0(), MidiButton.BTN_CH1).apply {
            register()
            midiButtons.add(MidiButton.BTN_CH1)
        }
        buttonInputDriverTwo = buttonInputDriver(driver.getBtn1(), MidiButton.BTN_CH2).apply {
            register()
            midiButtons.add(MidiButton.BTN_CH2)
        }

        mcpDriverManager.start()
    }

    private fun buttonInputDriver(pin: String, button: MidiButton): ButtonInputDriver {
        return ButtonInputDriver(pin, Button.LogicState.PRESSED_WHEN_LOW, button.key)
    }

    fun onClose() {
        buttonInputDriverOne.unregister()
        buttonInputDriverTwo.unregister()
        buttonInputDriverOne.close()
        buttonInputDriverTwo.close()

        mcpDriverManager.stop()
    }
}