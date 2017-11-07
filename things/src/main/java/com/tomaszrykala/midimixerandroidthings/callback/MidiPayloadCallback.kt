package com.tomaszrykala.midimixerandroidthings.callback

import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate

class MidiPayloadCallback : PayloadCallback() {

    override fun onPayloadReceived(endpointId: String?, payload: Payload?) {
        println("endpointId = ${endpointId}")
    }

    override fun onPayloadTransferUpdate(endpointId: String?, update: PayloadTransferUpdate?) {
    }
}