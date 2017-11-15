package com.tomaszrykala.midimixerandroidthings

import android.util.Log
import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.tomaszrykala.common.MidiEventType
import com.tomaszrykala.common.MidiEventWrapper
import com.tomaszrykala.midimixerandroidthings.control.MixerButton
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiControllerPresenter(private val view: MidiControllerContract.View,
                              private val service: String) : MidiControllerContract.Presenter {
    companion object {
        val TAG = MidiControllerPresenter::class.java.simpleName

        val DEFAULT_VELOCITY: Byte = 64
        val DEFAULT_NOTE: Byte = 0
    }

    private var endpoint: String? = null

    override fun onResultCallback(result: Status) {
        val s = "onResultCallback:onResult: "
        Log.i(TAG, s + result.isSuccess)
        Log.i(TAG, s + result.status.statusCode.toString())
    }

    override fun onStart() {
        view.connect()
    }

    override fun onStop() {
        view.disconnect()
    }

    override fun onConnected() {
        view.startDiscovery(service)
    }

    override fun onEndpointFound(endpointId: String?, discoveredEndpointInfo: DiscoveredEndpointInfo?) {
        if (endpointId != null && endpointId != endpoint) {
            view.requestConnection(endpointId, service)
            endpoint = endpointId
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
            Log.i(TAG, "onConnectionInitiated")
        }
    }

    override fun onConnectionResult(endpointId: String?, p1: ConnectionResolution?) {
        if (endpoint != endpointId) {
            when (p1?.status?.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.i(TAG, "onConnectionResult OK")
                    view.stopDiscovery(service)
                }
                else -> {
                    Log.i(TAG, "onConnectionResult not OK")
                    view.stopDiscovery(service)
                    view.startDiscovery(service)
                }
            }
        }
    }

    override fun onDisconnected(endpointId: String?) {
        Log.i(TAG, "onDisconnected")
        if (endpoint == endpointId) {
            view.startDiscovery(service)
            endpoint = null
        }

    }

    override fun onPressed(button: MixerButton, pressed: Boolean) {
        if (endpoint != null) {
            if (pressed) {
                view.sendPayload(endpoint!!, MidiEventWrapper(
                        MidiEventType.STATUS_NOTE_ON,
                        button.channel,
                        DEFAULT_NOTE,
                        DEFAULT_VELOCITY))
            }
        } else {
            // TODO ?
        }
    }

    override fun onControlChange(change: Int) {
        if (endpoint != null) {
            view.sendPayload(endpoint!!, MidiEventWrapper(
                    MidiEventType.STATUS_CONTROL_CHANGE,
                    2,
                    DEFAULT_NOTE,
                    change.toByte()))
        }
    }
}
