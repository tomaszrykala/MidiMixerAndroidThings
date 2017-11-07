package com.tomaszrykala.midimixerandroidthings

import android.util.Log
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.tomaszrykala.midimixerandroidthings.control.MidiButton
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiControllerPresenter(private val view: MidiControllerContract.View,
                              private val service: String) : MidiControllerContract.Presenter {
    companion object {
        val TAG = MidiControllerPresenter::class.java.simpleName
    }

    private var endpoint: String? = null

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

    override fun onReset() {
        view.startDiscovery(service)
    }

    override fun onConnected() {
        view.startDiscovery(service)
    }

    override fun onEndpointFound(endpointId: String?, discoveredEndpointInfo: DiscoveredEndpointInfo?) {
        if (endpointId != null && endpointId != endpoint) {
            view.requestConnection(endpointId, service)
            endpoint = endpointId
        } else {
            // TODO something bad
        }
    }

    override fun onEndpointLost(endpointId: String?) {
        if (endpointId == endpoint) {
            view.startDiscovery(service)
            endpoint = null
        }
    }

    override fun onConnectionInitiated(endpointId: String?, info: ConnectionInfo?) {
        if (endpointId != null) {
            endpoint = endpointId
            view.acceptConnection(endpointId)
            Log.d(TAG, "onConnectionInitiated")
        }
    }

    override fun onConnectionResult(endpointId: String?, p1: ConnectionResolution?) {
        if (endpoint != endpointId) {
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

    override fun onDisconnected(endpointId: String?) {
        Log.d(TAG, "onDisconnected")
        if (endpoint == endpointId) {
            view.startDiscovery(service)
            endpoint = null
        }

    }

    override fun onPressed(button: MidiButton) {
        if (endpoint != null) {
            Log.d(MainActivity.TAG, "button A pressed:" + button)
            if (button.pressed) {
                view.sendPayload(endpoint!!, button.channel, button.note)
            }
        } else {
            // TODO ?
        }
    }
}
