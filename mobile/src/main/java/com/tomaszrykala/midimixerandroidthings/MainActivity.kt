package com.tomaszrykala.midimixerandroidthings

import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.ToggleButton
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.tomaszrykala.midimixerandroidthings.midi.MidiController
import com.tomaszrykala.midimixerandroidthings.ui.DeviceAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    companion object {
        val TAG = MainActivity.javaClass.simpleName
    }

    override fun onConnected(p0: Bundle?) {

        Nearby.Connections.startAdvertising(
                googleApiClient,
                serviceId,
                serviceId,
                midiConnectionCallback,
                AdvertisingOptions(Strategy.P2P_CLUSTER))
                .setResultCallback { result ->
                    if (result.status.isSuccess) {
                        Log.d(TAG, "startAdvertising:onResult: SUCCESS")
                    } else {
                        Log.d(TAG, "startAdvertising:onResult: FAILURE ")
                        val statusCode = result.status.statusCode
                        if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
                            Log.d(TAG, "STATUS_ALREADY_ADVERTISING")
                        } else {
                            Log.d(TAG, "STATE_READY")
                        }
                    }
                }
    }


    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    private val lifecycleRegistry: LifecycleRegistry by lazyFast { LifecycleRegistry(this) }
    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

    private lateinit var serviceId: String
    private lateinit var midiConnectionCallback: MidiConnectionCallback
    private lateinit var midiPayloadCallback: MidiPayloadCallback

    class MidiConnectionCallback(val googleApiClient: GoogleApiClient,
                                 val midiPayloadCallback: MidiPayloadCallback) : ConnectionLifecycleCallback() {

        override fun onConnectionResult(endpointId: String?, p1: ConnectionResolution?) {
            when (p1?.status?.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> Log.d(TAG, "onConnectionResult OK")
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> Log.d(TAG, "onConnectionResult REJECTED")
                else -> Log.d(TAG, "onConnectionResult not OK")
            }
        }

        override fun onDisconnected(endpointId: String?) {
            Log.d(TAG, "onDisconnected")
        }

        override fun onConnectionInitiated(endpointId: String?, p1: ConnectionInfo?) {
            Log.d(TAG, "onConnectionInitiated")
            Nearby.Connections.acceptConnection(googleApiClient, endpointId, midiPayloadCallback)
        }
    }

    class MidiPayloadCallback(private val midiController: MidiController) : PayloadCallback() {

        override fun onPayloadReceived(endpointId: String?, payload: Payload?) {
            val bytes = payload?.asBytes()
            if (bytes is ByteArray) {
                // TODO change method arguments to bytes
                midiController.noteOn(bytes[0].toInt(), bytes[1].toInt(), bytes[2].toFloat())
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String?, update: PayloadTransferUpdate?) {
        }

    }

    val midiController: MidiController by viewModelProvider {
        MidiController(application)
    }

    private val deviceAdapter: DeviceAdapter by lazyFast {
        DeviceAdapter(this, { it.inputPortCount > 0 })
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

        setSupportActionBar(toolbar)

        initLockTaskOrToolbarTitle(false)

        midiController.observeDevices(this, deviceAdapter)

        serviceId = getString(R.string.service_id)
        midiPayloadCallback = MidiPayloadCallback(midiController)
        midiConnectionCallback = MidiConnectionCallback(googleApiClient, midiPayloadCallback)
    }

    private fun initLockTaskOrToolbarTitle(initLockTaskButton: Boolean) {
        supportActionBar?.apply {
            if (!initLockTaskButton) {
                setTitle(R.string.app_name)
            }
        }
        findViewById<ToggleButton>(R.id.lock_task_button)?.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) startLockTask() else stopLockTask()
            }
            visibility = if (initLockTaskButton) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.findItem(R.id.app_bar_selector)?.actionView?.apply {
            findViewById<Spinner>(R.id.output_selector)?.apply {
                adapter = deviceAdapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        midiController.closeAll()
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        deviceAdapter[position].apply {
                            midiController.open(this)
                        }
                    }
                }
            }
        }
        return true
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

    override fun onDestroy() {
        midiController.closeAll()
        super.onDestroy()
    }
}