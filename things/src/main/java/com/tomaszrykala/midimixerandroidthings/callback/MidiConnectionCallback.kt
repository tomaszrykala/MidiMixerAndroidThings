package com.tomaszrykala.midimixerandroidthings.callback

import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiConnectionCallback(private val midiPresenter: MidiControllerContract.Presenter) {

    fun onConnectionResult(endpointId: String?) {
        midiPresenter.onConnectionResult(endpointId)
    }

    fun onDisconnected(endpointId: String?) {
        midiPresenter.onDisconnected(endpointId)
    }

    fun onConnectionInitiated(endpointId: String??) {
        midiPresenter.onConnectionInitiated(endpointId)
    }
}