package com.tomaszrykala.midimixerandroidthings.control

import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.tomaszrykala.midimixerandroidthings.control.adc.McpDriverManager
import com.tomaszrykala.midimixerandroidthings.driver.Driver
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiControls(presenter: MidiControllerContract.Presenter) {

    private val driver: Driver = Driver()
    private val mcpDriverManager: McpDriverManager = McpDriverManager(presenter, driver)
    private val midiButtonDrivers: MutableList<ButtonInputDriver> = mutableListOf()

    val midiButtons: MutableList<MidiButton> = mutableListOf()

    fun onStart() {
        midiButtonDrivers.add(buttonInputDriver(driver.getBtn0(), MidiButton.BTN_CH0))
        midiButtonDrivers.add(buttonInputDriver(driver.getBtn1(), MidiButton.BTN_CH1))

        mcpDriverManager.start()
    }

    private fun buttonInputDriver(pin: String, midiButton: MidiButton): ButtonInputDriver {
        return ButtonInputDriver(pin, Button.LogicState.PRESSED_WHEN_LOW, midiButton.key).apply {
            register()
            midiButtons.add(midiButton)
        }
    }

    fun onClose() {
        midiButtonDrivers.forEach {
            it.unregister()
            it.close()
        }
        mcpDriverManager.stop()
    }
}