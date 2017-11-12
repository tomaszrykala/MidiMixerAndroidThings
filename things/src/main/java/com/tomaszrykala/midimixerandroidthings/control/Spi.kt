package com.tomaszrykala.midimixerandroidthings.control

import android.content.ContentValues
import android.os.Handler
import android.util.Log
import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.SpiDevice
import java.io.IOException

/* By default we have selected channel 0 in the ADC
* Before reading data we need to write about the channel which we will be reading from.
* */
class Spi {

    private var spiDevice: SpiDevice? = null
    private val spiAddress = "SPI0.0"

    val handler: Handler = Handler()
    val packetLength = 4 // 32

    val handlerDelay: Long = 100
    var readLastBytes: ByteArray = ByteArray(packetLength)

    fun connect() {

        val service = PeripheralManagerService()
        // https://issuetracker.google.com/issues/65071310
        var counter = 0
        while (spiDevice == null) {
            try {

                Log.i(ContentValues.TAG, "Open SPI Device; attempt: {$counter}")
                spiDevice = service.openSpiDevice(spiAddress)
                spiDevice?.let {
                    configureSpiDevice(spiDevice!!)
                    handler.post(deviceReadThread)
                }
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Error on PeripheralIO API", e)
            }
            counter++
        }
    }

    fun close() {
        handler.removeCallbacks(deviceReadThread)
        // Close the Gpio pin.

        if (spiDevice != null) {
            try {
                spiDevice!!.close()
                spiDevice = null
            } catch (e: IOException) {
                Log.w(ContentValues.TAG, "Unable to close SPI device", e)
            } finally {

            }
        }
    }

    private val deviceReadThread = object : Runnable {

        override fun run() {
            // Exit Runnable if the GPIO is already closed
            if (spiDevice == null) {
                return
            }
            try {
                val readArray = ByteArray(packetLength)
                if (spiDevice != null) {
                    spiDevice!!.read(readArray, readArray.size)

                    var newData = false
                    for (byte in readArray) {
                        if (byte != readLastBytes[readArray.indexOf(byte)]) {
                            readLastBytes = readArray
                            newData = true
                            break
                        }
                    }

                    if (newData) {
                        Log.i(ContentValues.TAG, "Reading from the SPI: data: {${readLastBytes.joinToString()}")
                        //                    readLastBytes.forEach { print(it) }
                    }
                }

                handler.postDelayed(this, handlerDelay)
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Error on PeripheralIO API", e)
            }

        }
    }


    @Throws(IOException::class)
    fun configureSpiDevice(device: SpiDevice) {
        // Low clock, leading edge transfer
        device.setMode(SpiDevice.MODE2) // SpiDevice.MODE0
        device.setBitsPerWord(8)          // 8 BPW
        device.setFrequency(1000000)     // 16MHz vs 1
        // device.setBitJustification(false) // MSB first
    }
}