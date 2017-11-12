package com.tomaszrykala.midimixerandroidthings.control

import android.content.ContentValues
import android.os.Handler
import android.util.Log
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.SpiDevice
import java.io.IOException

/* By default we have selected channel 0 in the ADC
* Before reading data we need to write about the channel which we will be reading from.
* */
class Spi {

    private var spiDevice: SpiDevice? = null
    val handler: Handler = Handler()

    fun connect() {

        RainbowHat.openLedStrip();

        val service = PeripheralManagerService()
        val spiBusList = service.spiBusList
        Log.i(ContentValues.TAG, "SPI Bus List in the device " + spiBusList.size)

        if (spiBusList.size <= 0) {
            Log.i(ContentValues.TAG, "Sorry your device does not support SPI")
            return
        }

        // https://issuetracker.google.com/issues/65071310
        var counter = 0
        while (spiDevice == null) {
            try {

                Log.i(ContentValues.TAG, "Open SPI Device; attempt: {$counter}")
                spiDevice = service.openSpiDevice(spiBusList[0])
                Log.i(ContentValues.TAG, "SPI Device configuration")


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

        var lastBytes: MutableList<Byte> = mutableListOf(0, 0, 0)

        private val handlerDelay: Long = 100

        override fun run() {
            // Exit Runnable if the GPIO is already closed
            if (spiDevice == null) {
                return
            }
            try {
                // if (doFullDuplex()) return
                //                var a2dVal = 0
//                a2dVal = response[1] shl 8 and 768 //merge data[1] & data[2] to get result
//                a2dVal = a2dVal or (response[2] and 0xff)

                doHalfDuplex()

                handler.postDelayed(this, handlerDelay)
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Error on PeripheralIO API", e)
            }

        }

        var readLastBytes: ByteArray = ByteArray(32)

        private fun doHalfDuplex() {
            // half duplex read
            val readArray = ByteArray(32)
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
        }

        private fun doFullDuplex(): Boolean {
            //                Log.i(TAG, "Reading from the SPI")

            val data = ByteArray(3)
            val response = ByteArray(3)
            data[0] = 1
            val a2dChannel = 0
            data[1] = (8 shl 4).toByte()
            data[2] = 0

            for (byte in lastBytes) {
                val indexOf = lastBytes.indexOf(byte)
                val compareByte = data[indexOf]
                if (byte != compareByte) {
                    lastBytes = data.toList() as MutableList<Byte>
                    break
                }
                return true
            }

            Log.i(ContentValues.TAG, "Reading from the SPI: data: {${data.forEach { print(it) }}}")

            //full duplex mode
            spiDevice!!.transfer(data, response, 3)
            return false
        }
    }


    @Throws(IOException::class)
    fun configureSpiDevice(device: SpiDevice) {
        // Low clock, leading edge transfer
        device.setMode(SpiDevice.MODE0)
        device.setBitsPerWord(8)          // 8 BPW
        device.setFrequency(16000000)     // 16MHz vs 1
        device.setBitJustification(false) // MSB first
    }
}