package com.tomaszrykala.midimixerandroidthings

import android.app.ActivityManager
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.tomaszrykala.midimixerandroidthings.midi.MidiController
import com.tomaszrykala.midimixerandroidthings.ui.DeviceAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onConnected(p0: Bundle?) {
//        startAdvertising()
    }

//    fun startAdvertising() {
//        progressBar.visibility = View.VISIBLE
//        Nearby.Connections.startAdvertising(
//                googleApiClient,
//                serviceId,
//                serviceId,
//                midiConnectionCallback,
//                AdvertisingOptions(Strategy.P2P_STAR)
//        ).setResultCallback { result ->
//            log(result.status.statusCode.toString())
//            if (result.status.isSuccess) {
//                log("startAdvertising:onResult: SUCCESS")
//            } else {
//                log("startAdvertising:onResult: FAILURE ")
//                if (result.status.statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
//                    log("STATUS_ALREADY_ADVERTISING")
//                } else {
//                    log("STATE_READY")
//                }
//            }
//        }
//    }

    override fun onConnectionSuspended(p0: Int) {
//        log("onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
//        log("onConnectionFailed")
    }

//    private val lifecycleRegistry: LifecycleRegistry by lazyFast { LifecycleRegistry(this) }
//    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

//    class MidiConnectionCallback(private val googleApiClient: GoogleApiClient,
//                                 private val midiPayloadCallback: MidiPayloadCallback,
//                                 private val mainActivity: MainActivity) : ConnectionLifecycleCallback() {
//
//        override fun onConnectionResult(endpointId: String?, connectionResolution: ConnectionResolution?) {
//            mainActivity.log(connectionResolution.toString())
//            when (connectionResolution?.status?.statusCode) {
//                ConnectionsStatusCodes.SUCCESS -> {
//                    mainActivity.log("onConnectionResult OK")
//                    Nearby.Connections.stopAdvertising(googleApiClient)
//                    mainActivity.progressBar.visibility = View.INVISIBLE
//                }
//                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> mainActivity.log("onConnectionResult REJECTED")
//                else -> mainActivity.log("onConnectionResult not OK")
//            }
//        }
//
//        override fun onDisconnected(endpointId: String?) {
//            mainActivity.log("onDisconnected")
////            mainActivity.startAdvertising()
//        }
//
//        override fun onConnectionInitiated(endpointId: String?, p1: ConnectionInfo?) {
//            mainActivity.log("onConnectionInitiated")
//            Nearby.Connections.acceptConnection(googleApiClient, endpointId, midiPayloadCallback)
//        }
//    }

//    class MidiPayloadCallback(private val midiController: MidiController) : PayloadCallback() {
//
//        override fun onPayloadReceived(endpointId: String?, payload: Payload?) {
//            Log.d(TAG, "endpointId = $endpointId" + "payload = $payload")
//
//            val data = payload?.asBytes()
//            if (data is ByteArray && data.size == 4) {
//                val bytes = byteArrayOf((data[0] + data[1]).toByte(), data[2], data[3])
//                midiController.send(bytes, System.nanoTime())
//            }
//        }
//
//        override fun onPayloadTransferUpdate(endpointId: String?, update: PayloadTransferUpdate?) {
//            // no - op
//        }
//    }

//    val midiController: MidiController by viewModelProvider {
//        MidiController(application)
//    }
//
//    private val deviceAdapter: DeviceAdapter by lazyFast {
//        DeviceAdapter(this, { it.inputPortCount > 0 })
//    }
//
//    private val googleApiClient: GoogleApiClient by lazyFast {
//        GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Nearby.CONNECTIONS_API)
//                .build()
//    }

    private lateinit var textView: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var progressBar: ProgressBar
    private lateinit var floatingActionButton: FloatingActionButton

    private lateinit var serviceId: String
    private lateinit var midiPayloadCallback: MidiPayloadCallback
    private lateinit var midiConnectionCallback: MidiConnectionCallback

    private lateinit var lockOpen: Drawable
    private lateinit var lockClosed: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        serviceId = getString(R.string.service_id)
        lockOpen = getDrawable(R.drawable.ic_lock_open_black_24dp)
        lockClosed = getDrawable(R.drawable.ic_lock_black_24dp)

        textView = findViewById(R.id.outputTextView)
        scrollView = findViewById(R.id.outputScrollView)
        progressBar = findViewById(R.id.progressBar)
        floatingActionButton = findViewById(R.id.lock_task_button)

        setScreenPinning()

        midiController.observeDevices(this, deviceAdapter)
        midiPayloadCallback = MidiPayloadCallback(midiController)
        midiConnectionCallback = MidiConnectionCallback(googleApiClient, midiPayloadCallback, this)
    }

    private fun isLocked(): Boolean {
        return (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
    }

    private fun setScreenPinning() {
        // init state
        if (isLocked()) {
            floatingActionButton.setImageDrawable(lockClosed)
        } else {
            floatingActionButton.setImageDrawable(lockOpen)
        }

        // following states
        floatingActionButton.setOnClickListener {
            if (isLocked()) {
                floatingActionButton.setImageDrawable(lockOpen)
                stopLockTask()
            } else {
                floatingActionButton.setImageDrawable(lockClosed)
                startLockTask()
            }
        }
    }

    private fun log(log: String) {
        Log.d(TAG, log)
        if (outputTextView != null) {
            outputTextView.text = StringBuilder(log).append("\n").append(outputTextView.text).toString()
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
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
        Nearby.Connections.stopAdvertising(googleApiClient)
        if (googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }
    }

    override fun onDestroy() {
        midiController.closeAll()
        super.onDestroy()
    }
}