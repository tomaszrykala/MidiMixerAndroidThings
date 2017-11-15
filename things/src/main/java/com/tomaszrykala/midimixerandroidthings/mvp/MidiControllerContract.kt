package com.tomaszrykala.midimixerandroidthings.mvp

import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.tomaszrykala.common.MidiEventWrapper
import com.tomaszrykala.midimixerandroidthings.control.MixerButton

interface MidiControllerContract {

    interface View {

        fun connect()

        fun disconnect()

        fun startDiscovery(service: String)

        fun stopDiscovery(service: String)

        fun requestConnection(endpointId: String, serviceId: String)

        fun acceptConnection(endpointId: String)

        fun sendPayload(endpointId: String, wrapper: MidiEventWrapper)

    }

    interface Presenter {

        fun onStart()

        fun onStop()

        fun onResultCallback(result: Status)

        fun onEndpointFound(endpointId: String?, discoveredEndpointInfo: DiscoveredEndpointInfo?)

        fun onEndpointLost(endpointId: String?)

        fun onConnectionInitiated(endpointId: String?, info: ConnectionInfo?)

        fun onConnectionResult(endpointId: String?, p1: ConnectionResolution?)

        fun onDisconnected(endpointId: String?)

        fun onConnected()

        fun onPressed(button: MixerButton, pressed: Boolean)

        fun onControlChange(change: Int)
    }
}