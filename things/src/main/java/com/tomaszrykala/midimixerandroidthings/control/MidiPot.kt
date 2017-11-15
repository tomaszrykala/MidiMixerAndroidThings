package com.tomaszrykala.midimixerandroidthings.control

import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiPot constructor(private val mcpController: MCP3008.Controller,
                          private val presenter: MidiControllerContract.Presenter,
                          private val analogChannel: Int,
                          private val midiChannel: Byte) : MCP3008.Listener {

    override fun onChange(read: Int) {
        presenter.onControlChange(read, midiChannel)
    }

    fun start() {
        mcpController.setListener(analogChannel, this)
    }

    fun stop() {
        mcpController.setListener(analogChannel, null)
    }
}