package com.tomaszrykala.midimixerandroidthings

import com.google.android.gms.common.api.Status
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.tomaszrykala.common.MidiEventType
import com.tomaszrykala.common.MidiEventWrapper
import com.tomaszrykala.midimixerandroidthings.control.MidiButton
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiControllerPresenter(private val view: MidiControllerContract.View,
                              private val service: String) : MidiControllerContract.Presenter {
    companion object {
        val DEFAULT_VELOCITY: Byte = 64
        val DEFAULT_NOTE: Byte = 0
    }

    private var endpoint: String? = null

    override fun onResultCallback(result: Status) {
        val s = "onResultCallback:onResult: "
        view.log(s + result.isSuccess)
        view.log(s + result.status.statusCode.toString())
        if (!result.isSuccess) {
            if (endpoint != null) {
                view.stopDiscovery(service)
                endpoint = null
            }
            view.startDiscovery(service)
        }
    }

    override fun onStart() {
        view.start()
    }

    override fun onStop() {
        view.stop()
    }

    override fun onConnected() {
        view.startDiscovery(service)
    }

    override fun onEndpointFound(endpointId: String?, discoveredEndpointInfo: DiscoveredEndpointInfo?) {
        view.log("onEndpointFound: " + endpointId)
        if (endpointId != null && endpointId != endpoint) {
            view.requestConnection(endpointId, service)
            endpoint = endpointId
        }
    }

    override fun onEndpointLost(endpointId: String?) {
        view.log("onEndpointLost: " + endpointId)
        if (endpointId == endpoint) {
            view.startDiscovery(service)
            endpoint = null
        }
    }

    override fun onConnectionInitiated(endpointId: String?, info: ConnectionInfo?) {
        view.log("onConnectionInitiated: $endpointId; info: $info")
        if (endpointId != null) {
            endpoint = endpointId
            view.acceptConnection(endpointId)
        }
    }

    override fun onConnectionResult(endpointId: String?, p1: ConnectionResolution?) {
        if (endpoint != endpointId) {
            when (p1?.status?.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    view.log("onConnectionResult OK")
                    view.stopDiscovery(service)
                }
                else -> {
                    view.log("onConnectionResult not OK")
                    view.stopDiscovery(service)
                    view.startDiscovery(service)
                }
            }
        }
    }

    override fun onDisconnected(endpointId: String?) {
        view.log("onDisconnected")
        view.stopDiscovery(endpoint!!)
        view.startDiscovery(service)
        endpoint = null
    }

    var lastMidiButtonPressed: MidiButton? = null

    override fun onPressed(button: MidiButton, pressed: Boolean) {
        if (endpoint != null) {
            if (pressed) {
                if (lastMidiButtonPressed != button) {
                    view.sendPayload(endpoint!!, MidiEventWrapper(
                            MidiEventType.STATUS_NOTE_ON, button.channel, DEFAULT_NOTE, DEFAULT_VELOCITY))
                    lastMidiButtonPressed = button
                }
            } else {
                lastMidiButtonPressed = null
            }
        }
    }

    override fun onControlChange(change: Int, midiChannel: Byte, key: Byte) {
        if (endpoint != null) {
            view.sendPayload(endpoint!!, MidiEventWrapper(
                    MidiEventType.STATUS_CONTROL_CHANGE, midiChannel, key, change.toByte()))
        }
    }
}
