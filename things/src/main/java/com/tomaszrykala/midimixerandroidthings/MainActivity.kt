package com.tomaszrykala.midimixerandroidthings

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat


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
class MainActivity : Activity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var serviceId: String
    private lateinit var midiEndpointDiscoveryCallback: MidiEndpointDiscoveryCallback
    private lateinit var midiConnectionCallback: MidiConnectionCallback
    private lateinit var midiPayloadCallback: MidiPayloadCallback

    override fun onConnected(p0: Bundle?) {
        startDiscovery()
    }

    fun startDiscovery() {
        Nearby.Connections.startDiscovery(
                googleApiClient,
                serviceId,
                midiEndpointDiscoveryCallback,
                DiscoveryOptions(Strategy.P2P_CLUSTER)
        ).setResultCallback { result ->
            if (result.isSuccess) {
                Log.d(TAG, "startDiscovery:onResult: SUCCESS")
            } else {
                Log.d(TAG, "startDiscovery:onResult: FAILURE")
            }
        }
    }

    fun requestConnection(endpointId: String) {
        Nearby.Connections.requestConnection(
                googleApiClient,
                serviceId,
                endpointId,
                midiConnectionCallback
        ).setResultCallback { result ->
            if (result.status.isSuccess) {
                Log.d(TAG, "requestConnection:onResult: SUCCESS")

                // Detect when button 'A' is pressed.
                val button = RainbowHat.openButtonA()
                button.setOnButtonEventListener { _, pressed ->
                    Log.d(TAG, "button A pressed:" + pressed)

                    Nearby.Connections.sendPayload(
                            googleApiClient,
                            endpointId,
                            Payload.fromBytes(byteArrayOf(0, 0, 64))
                    )
                }

                // Close the device when done.
                button.close()


            } else {
                Log.d(TAG, "requestConnection:onResult: FAILURE ")
                val statusCode = result.status.statusCode
                if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_CONNECTED_TO_ENDPOINT) {
                    Log.d(TAG, "STATUS_ALREADY_CONNECTED_TO_ENDPOINT")
                } else {
                    Log.d(TAG, "STATE_READY")
                }
            }
        }
    }

    class MidiEndpointDiscoveryCallback(private val googleApiClient: GoogleApiClient,
                                        private val mainActivity: MainActivity) : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String?, discoveredEndpointInfo: DiscoveredEndpointInfo?) {
            Nearby.Connections.stopDiscovery(googleApiClient)
            if (endpointId != null) mainActivity.requestConnection(endpointId)
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
        val TAG = MainActivity::class.java.simpleName
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

        serviceId = getString(R.string.service_id) + "_THINGS"
        midiPayloadCallback = MidiPayloadCallback()
        midiConnectionCallback = MidiConnectionCallback(googleApiClient, midiPayloadCallback, this)
        midiEndpointDiscoveryCallback = MidiEndpointDiscoveryCallback(googleApiClient, this)
    }

    override fun onStart() {
        super.onStart()
        googleApiClient.connect()
    }

    override fun onStop() {
        super.onStop()
        if (googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }
    }

    class MidiConnectionCallback(private val googleApiClient: GoogleApiClient,
                                 private val midiPayloadCallback: MidiPayloadCallback,
                                 private val mainActivity: MainActivity) : ConnectionLifecycleCallback() {

        override fun onConnectionResult(endpointId: String?, p1: ConnectionResolution?) {
            when (p1?.status?.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(TAG, "onConnectionResult OK")
                    Nearby.Connections.stopDiscovery(googleApiClient)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> Log.d(TAG, "onConnectionResult REJECTED")
                else -> Log.d(TAG, "onConnectionResult not OK")
            }
        }

        override fun onDisconnected(endpointId: String?) {
            Log.d(TAG, "onDisconnected")
            mainActivity.startDiscovery()
        }

        override fun onConnectionInitiated(endpointId: String?, p1: ConnectionInfo?) {
            Log.d(TAG, "onConnectionInitiated")
            Nearby.Connections.acceptConnection(googleApiClient, endpointId, midiPayloadCallback)
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
