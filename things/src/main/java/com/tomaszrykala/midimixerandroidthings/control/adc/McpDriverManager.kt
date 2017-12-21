package com.tomaszrykala.midimixerandroidthings.control.adc

import com.tomaszrykala.midimixerandroidthings.Key
import com.tomaszrykala.midimixerandroidthings.control.MidiPot
import com.tomaszrykala.midimixerandroidthings.driver.Driver
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class McpDriverManager(private val presenter: MidiControllerContract.Presenter, driver: Driver) {

    companion object {
        val mixerAdcStartChannel = 10
    }

    private val keys: DoubleArray = doubleArrayOf(Key.C0, Key.Cs0, Key.D0, Key.Ds0, Key.E0, Key.F0, Key.Fs0, Key.G0)

    private val mixerMcpDriver: McpDriver = McpDriver(driver.getSpio0(), driver.getSclk(), driver.getMosi(), driver.getMiso())
    private val mixerMidiPots: MutableList<MidiPot> = mutableListOf()

    fun start() {
        mixerMcpDriver.start()
        (0 until keys.size).mapTo(mixerMidiPots) {
            MidiPot(mixerMcpDriver, presenter, it, (mixerAdcStartChannel).toByte(), keys[it].toByte()).apply { start() }
        }
    }

    fun stop() {
        mixerMidiPots.forEach { it.stop() }
        mixerMcpDriver.stop()
    }
}