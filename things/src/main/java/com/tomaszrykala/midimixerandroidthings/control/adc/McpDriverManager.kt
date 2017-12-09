package com.tomaszrykala.midimixerandroidthings.control.adc

import com.tomaszrykala.midimixerandroidthings.control.MidiPot
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class McpDriverManager(private val presenter: MidiControllerContract.Presenter) {

    companion object {
        val mixerAdcStartChannel = 10
    }

    //    private val fxController: McpDriver = McpDriver("BCM8", "BCM11", "BCM10", "BCM9")
    private val mixerMcpDriver: McpDriver = McpDriver("BCM8", "BCM11", "BCM10", "BCM9")
    private val mixerMidiPots: MutableList<MidiPot> = mutableListOf()

    fun start() {
        mixerMcpDriver.start()
        (0..6).mapTo(mixerMidiPots) {
            MidiPot(mixerMcpDriver, presenter, it, (mixerAdcStartChannel + it).toByte()).apply { start() }
        }
    }

    fun stop() {
        mixerMidiPots.forEach { it.stop() }
        mixerMcpDriver.stop()
    }
}