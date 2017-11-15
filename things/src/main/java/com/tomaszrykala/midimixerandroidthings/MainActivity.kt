package com.tomaszrykala.midimixerandroidthings

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.Strategy
import com.tomaszrykala.common.MidiEventWrapper
import com.tomaszrykala.midimixerandroidthings.callback.MidiConnectionCallback
import com.tomaszrykala.midimixerandroidthings.callback.MidiEndpointDiscoveryCallback
import com.tomaszrykala.midimixerandroidthings.callback.MidiPayloadCallback
import com.tomaszrykala.midimixerandroidthings.control.MidiControls
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), MidiControllerContract.View, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    companion object {
        val TAG = MainActivity::class.java.simpleName + "_THINGS"
    }

    private lateinit var midiPresenter: MidiControllerContract.Presenter
    private lateinit var midiControls: MidiControls
    private lateinit var midiConnectionCallback: MidiConnectionCallback

    private lateinit var googleApiClient: GoogleApiClient

    private var textView: TextView? = null
    private var scrollView: ScrollView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.outputTextView)
        scrollView = findViewById(R.id.outputScrollView)

        midiPresenter = MidiControllerPresenter(this, getString(R.string.service_id))
        midiControls = MidiControls(midiPresenter)
        midiConnectionCallback = MidiConnectionCallback(midiPresenter)
        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        midiControls.mixerButtons
                .filter { keyCode == it.key }
                .forEach { midiPresenter.onPressed(it, true) }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        midiControls.mixerButtons
                .filter { keyCode == it.key }
                .forEach { midiPresenter.onPressed(it, false) }
        return super.onKeyUp(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        midiPresenter.onStart()
        midiControls.onStart()
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

    override fun stopDiscovery(service: String) {
        Nearby.Connections.stopDiscovery(googleApiClient)
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

    override fun sendPayload(endpointId: String, wrapper: MidiEventWrapper) {
        Nearby.Connections.sendPayload(
                googleApiClient,
                endpointId,
                Payload.fromBytes(byteArrayOf(wrapper.type(), wrapper.channel(), wrapper.note(), wrapper.pressure())))
    }

    private fun output(output: String) {
        if (outputTextView != null && scrollView != null) {
            outputTextView.text = StringBuilder(output).append(outputTextView.text).toString()
            scrollView!!.fullScroll(ScrollView.FOCUS_DOWN)
        }
        Log.i(TAG, output)
    }

    /** GoogleApiClient Callbacks */

    override fun onConnected(p0: Bundle?) {
        midiPresenter.onConnected()
    }

    override fun onConnectionSuspended(p0: Int) {
        output("onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        output("onConnectionFailed")
    }

    /** GoogleApiClient Callbacks */
}
