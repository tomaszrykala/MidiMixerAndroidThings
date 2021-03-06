package com.tomaszrykala.midimixerandroidthings.midi

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Handler

class MidiController(
        context: Context,
        private val midiManager: MidiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager,
        private val midiDeviceMonitor: MidiDeviceMonitor = MidiDeviceMonitor(context, midiManager)
) : AndroidViewModel(context.applicationContext as Application) {

    private var midiInputPort: MidiInputPort? = null
    private var midiDevice: MidiDevice? = null
    private val handler: Handler = Handler()

    fun observeDevices(lifecycleOwner: LifecycleOwner, observer: Observer<List<MidiDeviceInfo>>) =
            midiDeviceMonitor.observe(lifecycleOwner, observer)

    fun open(midiDeviceInfo: MidiDeviceInfo) =
            close().also {
                midiDeviceInfo.ports.first {
                    it.type == MidiDeviceInfo.PortInfo.TYPE_INPUT
                }.portNumber.also { portNumber ->
                    midiManager.openDevice(midiDeviceInfo, {
                        midiDevice = it
                        midiInputPort = it.openInputPort(portNumber)
                    }, handler)
                }
            }

    private fun close() {
        midiInputPort?.close()
        midiInputPort = null
        midiDevice?.close()
        midiDevice = null
    }

    fun send(msg: ByteArray, timestamp: Long) {
        midiInputPort?.send(msg, 0, msg.size, timestamp)
    }

    fun closeAll() {
        //NO-OP yet
    }
}