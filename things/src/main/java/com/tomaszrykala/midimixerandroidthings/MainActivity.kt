package com.tomaszrykala.midimixerandroidthings

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.ScrollView
import android.widget.TextView
import com.tomaszrykala.common.MidiEventWrapper
import com.tomaszrykala.midimixerandroidthings.callback.MidiConnectionCallback
import com.tomaszrykala.midimixerandroidthings.control.MidiControls
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), MidiControllerContract.View {

    companion object {
        val TAG = MainActivity::class.java.simpleName + "_THINGS"
    }

    private lateinit var midiPresenter: MidiControllerContract.Presenter
    private lateinit var midiControls: MidiControls
    private lateinit var midiConnectionCallback: MidiConnectionCallback

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
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
        midiPresenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
        midiPresenter.onStop()
    }

    override fun start() {
        midiControls.onStart()
    }

    override fun stop() {
        midiControls.onClose()
    }

    override fun startDiscovery(service: String) {
        log("startDiscovery $service")
        // MidiEndpointDiscoveryCallback(midiPresenter),
        // onResult -> midiPresenter.onResultCallback(result)
    }

    override fun stopDiscovery(service: String) {
        log("stopDiscovery $service")
    }

    override fun acceptConnection(endpointId: String) {
        log("acceptConnection $endpointId")
    }

    override fun requestConnection(endpointId: String, serviceId: String) {
        // midiConnectionCallback
        // onResult -> midiPresenter.onResultCallback(result)
        log("requestConnection $endpointId")

        // onConnected ->  midiPresenter.onConnected()
    }

    override fun sendPayload(endpointId: String, wrapper: MidiEventWrapper) {
        val bytes = byteArrayOf(wrapper.type(), wrapper.channel(), wrapper.note(), wrapper.pressure())
    }

    override fun log(log: String) {
        Log.d(TAG, log)
        if (outputTextView != null && scrollView != null) {
            outputTextView.text = StringBuilder(log).append("\n").append(outputTextView.text).toString()
            scrollView!!.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        onKey(keyCode, true)
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        onKey(keyCode, false)
        return super.onKeyUp(keyCode, event)
    }

    private fun onKey(keyCode: Int, pressed: Boolean) {
        midiControls.midiButtons
                .filter { keyCode == it.keyCode }
                .forEach { midiPresenter.onNoteOn(it, pressed) }
    }

}
