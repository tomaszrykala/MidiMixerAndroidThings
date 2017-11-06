package com.tomaszrykala.midimixerandroidthings.mvp

import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo

interface MidiControllerContract {

    interface View {

        fun connect()

        fun disconnect()

        fun startDiscovery()

        fun requestConnectionWithEndpoint(endpointId: String)

        fun acceptConnection(endpointId: String?)

        fun sendPayload()
    }

    interface Presenter {

        fun onStart()

        fun onStop()

        fun onResultCallback(result: Status)

        fun onEndpointFound(endpointId: String?, discoveredEndpointInfo: DiscoveredEndpointInfo?)

        fun onConnectionInitiated(endpointId: String?, info: ConnectionInfo?)

        fun onConnectionResult(endpointId: String?, p1: ConnectionResolution?)

        fun onDisconnected(endpointId: String?)

        fun onConnected()
    }
}