package com.tomaszrykala.midimixerandroidthings.control

import android.view.KeyEvent
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.tomaszrykala.midimixerandroidthings.control.adc.McpDriverManager
import com.tomaszrykala.midimixerandroidthings.driver.Driver
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiControls(presenter: MidiControllerContract.Presenter) {

    private val driver: Driver = Driver()
    private val mcpDriverManager: McpDriverManager = McpDriverManager(presenter, driver)
    private val midiButtonDrivers: MutableList<ButtonInputDriver> = mutableListOf()
    private val midiButtonMapping: Map<String, Int> = mapOf(
            driver.getBtn0() to KeyEvent.KEYCODE_0,
            driver.getBtn1() to KeyEvent.KEYCODE_1,
            driver.getBtn2() to KeyEvent.KEYCODE_2,
            driver.getBtn3() to KeyEvent.KEYCODE_3
    )

    val midiButtons: MutableList<MidiButton> = mutableListOf()

    fun onStart() {
        midiButtonMapping.asIterable().forEachIndexed { index, entry ->
            midiButtonDrivers.add(buttonInputDriver(entry.key, MidiButton(entry.value, index.toByte())))
        }
        mcpDriverManager.start()
    }

    private fun buttonInputDriver(pin: String, midiButton: MidiButton): ButtonInputDriver {
        return ButtonInputDriver(pin, Button.LogicState.PRESSED_WHEN_LOW, midiButton.keyCode).apply {
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