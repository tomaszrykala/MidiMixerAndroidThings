package com.tomaszrykala.midimixerandroidthings.callback

import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiEndpointDiscoveryCallback(private val presenter: MidiControllerContract.Presenter) {

    fun onEndpointFound(endpointId: String?) {
        presenter.onEndpointFound(endpointId)
    }

    fun onEndpointLost(endpointId: String?) {
        presenter.onEndpointLost(endpointId)
    }
}