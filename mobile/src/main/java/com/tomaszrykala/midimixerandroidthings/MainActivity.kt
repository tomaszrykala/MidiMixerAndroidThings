package com.tomaszrykala.midimixerandroidthings

import android.Manifest
import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import android.support.v4.content.ContextCompat
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
        val TAG = MainActivity::class.java.simpleName + "_APP"
    }

    override fun onConnected(p0: Bundle?) {
        startAdvertising()
    }

    fun startAdvertising() {
        Nearby.Connections.startAdvertising(
                googleApiClient,
                serviceId,
                serviceId,
                midiConnectionCallback,
                AdvertisingOptions(Strategy.P2P_STAR)
        ).setResultCallback { result ->

            val statusCode = result.status.statusCode
            Log.d(TAG, statusCode.toString())
            val permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            Log.d(TAG, " Manifest.permission.ACCESS_COARSE_LOCATION: " + permissionCheck)

            if (result.status.isSuccess) {
                Log.d(TAG, "startAdvertising:onResult: SUCCESS")
            } else {
                Log.d(TAG, "startAdvertising:onResult: FAILURE ")

//                if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
//                    Log.d(TAG, "STATUS_ALREADY_ADVERTISING")
//                } else {
//                    Log.d(TAG, "STATE_READY")
//                }
            }
        }
    }


    override fun onConnectionSuspended(p0: Int) {
        Log.d(TAG, "onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed")
    }

    private val lifecycleRegistry: LifecycleRegistry by lazyFast { LifecycleRegistry(this) }
    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

    private lateinit var serviceId: String
    private lateinit var midiPayloadCallback: MidiPayloadCallback
    private lateinit var midiConnectionCallback: MidiConnectionCallback

    class MidiConnectionCallback(private val googleApiClient: GoogleApiClient,
                                 private val midiPayloadCallback: MidiPayloadCallback,
                                 private val mainActivity: MainActivity) : ConnectionLifecycleCallback() {

        override fun onConnectionResult(endpointId: String?, connectionResolution: ConnectionResolution?) {
            Log.d(TAG, connectionResolution.toString())
            when (connectionResolution?.status?.statusCode) {
                ConnectionsStatusCodes.SUCCESS -> {
                    Log.d(TAG, "onConnectionResult OK")
                    Nearby.Connections.stopAdvertising(googleApiClient)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> Log.d(TAG, "onConnectionResult REJECTED")
                else -> Log.d(TAG, "onConnectionResult not OK")
            }
        }

        override fun onDisconnected(endpointId: String?) {
            Log.d(TAG, "onDisconnected")
            mainActivity.startAdvertising()
        }

        override fun onConnectionInitiated(endpointId: String?, p1: ConnectionInfo?) {
            Log.d(TAG, "onConnectionInitiated")
            Nearby.Connections.acceptConnection(googleApiClient, endpointId, midiPayloadCallback)
        }
    }

    class MidiPayloadCallback(private val midiController: MidiController) : PayloadCallback() {

        override fun onPayloadReceived(endpointId: String?, payload: Payload?) {
            Log.d(TAG, "endpointId = ${endpointId}" + "payload = ${payload}")
            val bytes = payload?.asBytes()
            if (bytes is ByteArray) {
                // TODO change method arguments to bytes
                val channel = bytes[0].toInt()
                val note = bytes[1].toInt()
                val pressure = bytes[2].toFloat()
                midiController.noteOn(channel, note, pressure)

                Log.d(TAG, "channel = ${channel}" + "note = ${note}" + "pressure = ${pressure}")
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
        midiConnectionCallback = MidiConnectionCallback(googleApiClient, midiPayloadCallback, this)
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