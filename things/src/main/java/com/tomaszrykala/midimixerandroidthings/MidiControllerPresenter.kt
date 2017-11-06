package com.tomaszrykala.midimixerandroidthings

import android.util.Log
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiControllerPresenter(val view: MidiControllerContract.View) : MidiControllerContract.Presenter {

    companion object {
        val TAG = MidiControllerPresenter::class.java.simpleName
    }

    override fun onResultCallback(result: Status) {
        val s = "onResultCallback:onResult: "
        Log.d(TAG, s + result.isSuccess)
        Log.d(TAG, s + result.status.statusCode.toString())
    }

    override fun onStart() {
        view.connect()
    }

    override fun onStop() {
        view.disconnect()
    }

    override fun onConnected() {
        view.startDiscovery()
    }

    private var endpointId: String? = null

    override fun onEndpointFound(id: String?, discoveredEndpointInfo: DiscoveredEndpointInfo?) {
        if (id != null && id != endpointId) {
            view.requestConnectionWithEndpoint(id)
            endpointId = id
        } else {
            // TODO something bad
        }
    }

    override fun onConnectionInitiated(endpointId: String?, info: ConnectionInfo?) {
        Log.d(TAG, "onConnectionInitiated")
        view.acceptConnection(endpointId)
    }

    override fun onConnectionResult(id: String?, p1: ConnectionResolution?) {
        if (endpointId != id) {
            when (p1?.status?.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(TAG, "onConnectionResult OK")
                    // Nearby.Connections.stopDiscovery(googleApiClient)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> Log.d(TAG, "onConnectionResult REJECTED")
                else -> Log.d(TAG, "onConnectionResult not OK")
            }
        } else {
            // TODO ?
        }
    }

    override fun onDisconnected(id: String?) {
        Log.d(TAG, "onDisconnected")
        if (this.endpointId == id) {
            view.startDiscovery()
            endpointId = null
        }

    }

}