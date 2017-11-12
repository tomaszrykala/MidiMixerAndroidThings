package com.tomaszrykala.midimixerandroidthings.callback

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiEndpointDiscoveryCallback(private val presenter: MidiControllerContract.Presenter) : EndpointDiscoveryCallback() {

    override fun onEndpointFound(endpointId: String?, discoveredEndpointInfo: DiscoveredEndpointInfo?) {
        presenter.onEndpointFound(endpointId, discoveredEndpointInfo)
    }

    override fun onEndpointLost(endpointId: String?) {
        presenter.onEndpointLost(endpointId)
    }
}