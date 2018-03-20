package com.tomaszrykala.midimixerandroidthings.midi

import android.content.Context
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import android.media.midi.MidiManager.DeviceCallback
import android.os.Handler

class MidiDeviceMonitor internal constructor(
        context: Context,
        private val midiManager: MidiManager,
        private val handler: Handler = Handler(context.mainLooper),
        private val data: MutableList<MidiDeviceInfo> = mutableListOf()
) {

    var controller: MidiController? = null // TODO added

    fun onActive(midiController: MidiController) {
        controller = midiController
        midiManager.registerDeviceCallback(deviceCallback, handler)
        data.addAll(midiManager.devices)
    }

    fun onInactive() {
        controller?.closeAll()
        midiManager.unregisterDeviceCallback(deviceCallback)
        data.clear()
    }

    private val deviceCallback = object : DeviceCallback() {
        override fun onDeviceAdded(device: MidiDeviceInfo?) {
            super.onDeviceAdded(device)
            device?.also {
                data.add(it)
                controller?.open(it) // TODO added
            }
        }

        override fun onDeviceRemoved(device: MidiDeviceInfo?) {
            super.onDeviceRemoved(device)
            device?.also {
                data.remove(it)
            }
        }
    }
}
