package com.tomaszrykala.midimixerandroidthings

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity(), MidiControllerContract.View,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    override fun acceptConnection(endpointId: String?) {

        Nearby.Connections.acceptConnection(googleApiClient, endpointId, midiPayloadCallback)
    }

    override fun connect() {
        googleApiClient.connect()
    }

    override fun disconnect() {
        if (googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }
    }

    override fun requestConnectionWithEndpoint(endpointId: String) {
        this.endpointId = endpointId
        requestConnection(endpointId)
//        Nearby.Connections.stopDiscovery(googleApiClient)
    }

    override fun sendPayload() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var serviceId: String
    private lateinit var midiEndpointDiscoveryCallback: MidiEndpointDiscoveryCallback
    private lateinit var midiConnectionCallback: MidiConnectionCallback
    private lateinit var midiPayloadCallback: MidiPayloadCallback

    private lateinit var buttonA: Button
    private lateinit var buttonB: Button

    private var endpointId: String? = null

    private lateinit var midiPresenter: MidiControllerContract.Presenter

    override fun onConnected(p0: Bundle?) {
        midiPresenter.onConnected()
    }

    override fun startDiscovery() {
        Nearby.Connections.startDiscovery(
                googleApiClient,
                serviceId,
                midiEndpointDiscoveryCallback,
                DiscoveryOptions(Strategy.P2P_CLUSTER)
        ).setResultCallback { result ->
            midiPresenter.onResultCallback(result)
        }
    }

    fun requestConnection(endpointId: String) {
        Nearby.Connections.requestConnection(
                googleApiClient,
                serviceId,
                endpointId,
                midiConnectionCallback
        ).setResultCallback { result ->
            midiPresenter.onResultCallback(result)
        }
    }

    class MidiEndpointDiscoveryCallback(private val mainActivity: MainActivity,
                                        private val midiPresenter: MidiControllerContract.Presenter) : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String?, discoveredEndpointInfo: DiscoveredEndpointInfo?) {
            midiPresenter.onEndpointFound(endpointId, discoveredEndpointInfo);
        }

        override fun onEndpointLost(endpointId: String?) {
            mainActivity.startDiscovery()
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d(TAG, "onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed")
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName + "_THINGS"
    }

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

        midiPresenter = MidiControllerPresenter(this)

        serviceId = getString(R.string.service_id) + "_APP" // THINGS
        midiPayloadCallback = MidiPayloadCallback()
        midiConnectionCallback = MidiConnectionCallback(midiPresenter)
        midiEndpointDiscoveryCallback = MidiEndpointDiscoveryCallback(this, midiPresenter)

        buttonA = RainbowHat.openButtonA().apply {
            setOnButtonEventListener { _, pressed ->
                Log.d(TAG, "button A pressed:" + pressed)

                Nearby.Connections.sendPayload(
                        googleApiClient,
                        endpointId,
                        Payload.fromBytes(byteArrayOf(0, 0, 64))
                )
            }
        }

        buttonB = RainbowHat.openButtonB().apply {
            setOnButtonEventListener { _, pressed ->
                startDiscovery()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        midiPresenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        midiPresenter.onStop()
    }

    class MidiConnectionCallback(private val midiPresenter: MidiControllerContract.Presenter) : ConnectionLifecycleCallback() {

        override fun onConnectionResult(endpointId: String?, p1: ConnectionResolution?) {
            midiPresenter.onConnectionResult(endpointId, p1)
        }

        override fun onDisconnected(endpointId: String?) {
            midiPresenter.onDisconnected(endpointId)
        }

        override fun onConnectionInitiated(endpointId: String?, connectionInfo: ConnectionInfo?) {
            midiPresenter.onConnectionInitiated(endpointId, connectionInfo)
        }
    }

    class MidiPayloadCallback : PayloadCallback() {

        override fun onPayloadReceived(endpointId: String?, payload: Payload?) {
            val bytes = payload?.asBytes()
            if (bytes is ByteArray) {
                // TODO change method arguments to bytes
                // midiController.noteOn(bytes[0].toInt(), bytes[1].toInt(), bytes[2].toFloat())
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String?, update: PayloadTransferUpdate?) {
        }

    }
}
