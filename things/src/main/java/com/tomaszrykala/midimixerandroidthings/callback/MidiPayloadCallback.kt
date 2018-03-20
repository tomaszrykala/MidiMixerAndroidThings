package com.tomaszrykala.midimixerandroidthings.callback

class MidiPayloadCallback {

    fun onPayloadReceived(endpointId: String?) {
        println("endpointId = $endpointId")
    }

    fun onPayloadTransferUpdate(endpointId: String?) {
    }
}