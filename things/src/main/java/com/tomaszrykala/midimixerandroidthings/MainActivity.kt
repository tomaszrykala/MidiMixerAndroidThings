package com.tomaszrykala.midimixerandroidthings

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.Strategy
import com.tomaszrykala.midimixerandroidthings.callback.MidiConnectionCallback
import com.tomaszrykala.midimixerandroidthings.callback.MidiEndpointDiscoveryCallback
import com.tomaszrykala.midimixerandroidthings.callback.MidiPayloadCallback
import com.tomaszrykala.midimixerandroidthings.control.MidiControls
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MainActivity : Activity(),
        MidiControllerContract.View,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        println("MainActivity.onKeyDown")

        midiControls.mixerButtons
                .filter { keyCode == it.key }
                .forEach { midiPresenter.onPressed(it, true) }

//        if (keyCode == KeyEvent.KEYCODE_SPACE) {
//            midiPresenter.onPressed(MidiButton(0, true))
//        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        println("MainActivity.onKeyUp")

        midiControls.mixerButtons
                .filter { keyCode == it.key }
                .forEach { midiPresenter.onPressed(it, false) }

//        if (keyCode == KeyEvent.KEYCODE_SPACE) {
//            midiPresenter.onPressed(MidiButton(0, false))
//        }
        return super.onKeyUp(keyCode, event)
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName + "_THINGS"
    }

    private lateinit var midiConnectionCallback: MidiConnectionCallback
    private lateinit var midiPresenter: MidiControllerContract.Presenter

    private val midiControls = MidiControls()

    private val googleApiClient: GoogleApiClient by lazyFast {
        GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        midiPresenter = MidiControllerPresenter(this, getString(R.string.service_id))
        midiConnectionCallback = MidiConnectionCallback(midiPresenter)
    }

    override fun onStart() {
        super.onStart()
        midiPresenter.onStart()
        midiControls.onStart(midiPresenter)
    }

    override fun onStop() {
        super.onStop()
        midiPresenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        midiControls.onClose()
    }

    override fun startDiscovery(serviceId: String) {
        Nearby.Connections.startDiscovery(
                googleApiClient,
                serviceId,
                MidiEndpointDiscoveryCallback(midiPresenter),
                DiscoveryOptions(Strategy.P2P_STAR)
        ).setResultCallback { result ->
            midiPresenter.onResultCallback(result)
        }
    }

    override fun acceptConnection(endpointId: String) {
        Nearby.Connections.acceptConnection(googleApiClient, endpointId, MidiPayloadCallback())
    }

    override fun connect() {
        googleApiClient.connect()
    }

    override fun disconnect() {
        if (googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }
    }

    override fun requestConnection(endpointId: String, serviceId: String) {
        Nearby.Connections.requestConnection(
                googleApiClient,
                serviceId,
                endpointId,
                midiConnectionCallback
        ).setResultCallback { result ->
            midiPresenter.onResultCallback(result)
        }
    }

    override fun sendPayload(endpointId: String, channel: Byte, note: Byte) {
        Nearby.Connections.sendPayload(
                googleApiClient,
                endpointId,
                Payload.fromBytes(byteArrayOf(channel, note, 64)))
    }

    /** GoogleApiClient Callbacks */

    override fun onConnected(p0: Bundle?) {
        midiPresenter.onConnected()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d(TAG, "onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed")
    }

    /** GoogleApiClient Callbacks */
}
