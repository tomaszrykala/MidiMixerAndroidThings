package com.tomaszrykala.midimixerandroidthings.control

import com.tomaszrykala.midimixerandroidthings.control.adc.McpDriver
import com.tomaszrykala.midimixerandroidthings.control.adc.McpDriver.Listener
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiPot(private val mcpDriver: McpDriver,
              private val presenter: MidiControllerContract.Presenter,
              private val analogChannel: Int,
              val midiChannel: Byte,
              val key: Byte) : Listener {

    override fun onChange(read: Int) {
        presenter.onControlChange(this, read.toByte())
    }

    fun start() {
        mcpDriver.addListener(analogChannel, this)
    }

    fun stop() {
        mcpDriver.addListener(analogChannel, null)
    }
}