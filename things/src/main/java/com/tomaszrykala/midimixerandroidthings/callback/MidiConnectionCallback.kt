package com.tomaszrykala.midimixerandroidthings.callback

import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiConnectionCallback(private val midiPresenter: MidiControllerContract.Presenter) : ConnectionLifecycleCallback() {

    override fun onConnectionResult(endpointId: String?, p1: ConnectionResolution?) {
        midiPresenter.onConnectionResult(endpointId, p1)
    }

    override fun onDisconnected(endpointId: String?) {
        midiPresenter.onDisconnected(endpointId)
    }

    override fun onConnectionInitiated(endpointId: String?, connectionInfo: ConnectionInfo?) {
        midiPresenter.onConnectionInitiated(endpointId, connectionInfo)
    }
}