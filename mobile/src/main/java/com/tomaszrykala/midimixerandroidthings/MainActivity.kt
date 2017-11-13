package com.tomaszrykala.midimixerandroidthings

import android.Manifest
import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.tomaszrykala.common.MidiEventType
import com.tomaszrykala.midimixerandroidthings.midi.MidiController
import com.tomaszrykala.midimixerandroidthings.ui.DeviceAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    companion object {
        val TAG = MainActivity::class.java.simpleName
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
            log(statusCode.toString())
            val permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            log(" Manifest.permission.ACCESS_COARSE_LOCATION: " + permissionCheck)

            if (result.status.isSuccess) {
                log("startAdvertising:onResult: SUCCESS")
            } else {
                log("startAdvertising:onResult: FAILURE ")
                if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
                    log("STATUS_ALREADY_ADVERTISING")
                } else {
                    log("STATE_READY")
                }
            }
        }
    }


    override fun onConnectionSuspended(p0: Int) {
        log("onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        log("onConnectionFailed")
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
            mainActivity.log(connectionResolution.toString())
            when (connectionResolution?.status?.statusCode) {
                ConnectionsStatusCodes.SUCCESS -> {
                    mainActivity.log("onConnectionResult OK")
                    Nearby.Connections.stopAdvertising(googleApiClient)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> mainActivity.log("onConnectionResult REJECTED")
                else -> mainActivity.log("onConnectionResult not OK")
            }
        }

        override fun onDisconnected(endpointId: String?) {
            mainActivity.log("onDisconnected")
            mainActivity.startAdvertising()
        }

        override fun onConnectionInitiated(endpointId: String?, p1: ConnectionInfo?) {
            mainActivity.log("onConnectionInitiated")
            Nearby.Connections.acceptConnection(googleApiClient, endpointId, midiPayloadCallback)
        }
    }

    class MidiPayloadCallback(private val midiController: MidiController, private val mainActivity: MainActivity) : PayloadCallback() {

        override fun onPayloadReceived(endpointId: String?, payload: Payload?) {
            Log.d(TAG, "endpointId = $endpointId" + "payload = $payload")

            val data = payload?.asBytes()
            if (data is ByteArray && data.size == 4) {
                // val wrapper = MidiEventWrapper(data[0], data.copyOfRange(1, 3))
                val type = data[0]
                val channel = data[1].toInt()
                val note = data[2].toInt()
                val pressure = data[3].toFloat()
                when (type) {
                    MidiEventType.STATUS_NOTE_ON.byte -> midiController.noteOn(
                            channel,
                            note,
                            pressure)
                    MidiEventType.STATUS_NOTE_OFF.byte -> midiController.noteOff(
                            channel,
                            note,
                            pressure)
                    MidiEventType.STATUS_CONTROL_CHANGE.byte -> midiController.controlChange(
                            channel,
                            note,
                            pressure)
                    else -> throw IllegalArgumentException("Unsupported payload type")
                }

                Log.d(TAG, "channel = $channel" + "note = $note" + "pressure = " + "$pressure")
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

    private var textView: TextView? = null
    private var scrollView: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        textView = findViewById(R.id.outputTextView)
        scrollView = findViewById(R.id.outputScrollView)
        initLockTaskOrToolbarTitle(false)

        midiController.observeDevices(this, deviceAdapter)

        serviceId = getString(R.string.service_id)
        midiPayloadCallback = MidiPayloadCallback(midiController, this)
        midiConnectionCallback = MidiConnectionCallback(googleApiClient, midiPayloadCallback, this)
    }

    private fun log(log: String) {
        Log.d(TAG, log)
        if (outputTextView != null && scrollView != null) {
            outputTextView.text = StringBuilder(log).append("\n").append(outputTextView.text).toString()
            scrollView!!.fullScroll(ScrollView.FOCUS_DOWN)
        }
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