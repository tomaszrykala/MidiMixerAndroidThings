package com.tomaszrykala.midimixerandroidthings

import com.tomaszrykala.common.MidiEventType
import com.tomaszrykala.common.MidiEventWrapper
import com.tomaszrykala.midimixerandroidthings.control.MidiButton
import com.tomaszrykala.midimixerandroidthings.control.MidiPot
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiControllerPresenter(private val view: MidiControllerContract.View,
                              private val service: String) : MidiControllerContract.Presenter {

    private var endpoint: String? = null
    private var lastMidiButtonPressed: MidiButton? = null

    override fun onResultCallback() {
        val s = "onResultCallback:onResult: "
//        view.log(s + result.isSuccess)
//        view.log(s + result.status.statusCode.toString())
//        if (!result.isSuccess) {
//            if (endpoint != null) {
//                view.stopDiscovery(service)
//                endpoint = null
//            }
//            view.startDiscovery(service)
//        }
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

    override fun onEndpointFound(endpointId: String?) {
        view.log("onEndpointFound: $endpointId")
        if (endpointId != null && endpointId != endpoint) {
            view.requestConnection(endpointId, service)
            endpoint = endpointId
        }
    }

    override fun onEndpointLost(endpointId: String?) {
        view.log("onEndpointLost: $endpointId")
        if (endpointId == endpoint) {
            view.startDiscovery(service)
            endpoint = null
        }
    }

    override fun onConnectionInitiated(endpointId: String?) {
        view.log("onConnectionInitiated: $endpointId")
        if (endpointId != null) {
            endpoint = endpointId
            view.acceptConnection(endpointId)
        }
    }

    override fun onConnectionResult(endpointId: String?) {
//        if (endpoint != endpointId) {
//            when (p1?.status?.statusCode) {
//                ConnectionsStatusCodes.STATUS_OK -> {
//                    view.log("onConnectionResult OK")
//                    view.stopDiscovery(service)
//                }
//                else -> {
//                    view.log("onConnectionResult not OK")
//                    view.stopDiscovery(service)
//                    view.startDiscovery(service)
//                }
//            }
//        }
    }

    override fun onDisconnected(endpointId: String?) {
        view.log("onDisconnected")
        view.stopDiscovery(endpoint!!)
        view.startDiscovery(service)
        endpoint = null
    }

    override fun onNoteOn(midiButton: MidiButton, pressed: Boolean) {
        endpoint?.apply {
            if (pressed) {
                if (lastMidiButtonPressed != midiButton) {
                    view.sendPayload(endpoint!!,
                            MidiEventWrapper(MidiEventType.STATUS_NOTE_ON, midiButton.midiChannel, midiButton.key, midiButton.velocity))
                    lastMidiButtonPressed = midiButton
                }
            } else {
                lastMidiButtonPressed = null
            }
        }
    }

    override fun onControlChange(midiPot: MidiPot, velocity: Byte) {
        endpoint?.apply {
            view.sendPayload(endpoint!!,
                    MidiEventWrapper(MidiEventType.STATUS_CONTROL_CHANGE, midiPot.midiChannel, midiPot.key, velocity))
        }
    }
}
