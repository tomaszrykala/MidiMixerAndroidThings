package com.tomaszrykala.midimixerandroidthings.control

import android.content.ContentValues.TAG
import android.os.Handler
import android.util.Log
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.SpiDevice
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract
import java.io.IOException


class MidiControls {

    private lateinit var buttonA: Button
    private lateinit var buttonB: Button
    private lateinit var buttonC: Button

    private lateinit var buttonInputDriverOne: ButtonInputDriver
    private lateinit var buttonInputDriverTwo: ButtonInputDriver

    val mixerButtons: MutableList<MixerButton> = mutableListOf()

    private var spiDevice: SpiDevice? = null
    val handler: Handler = Handler()

    fun onStart(presenter: MidiControllerContract.Presenter) {

        buttonInputDriverOne = buttonInputDriver(MixerButton.BTN_CH1).apply {
            register(); mixerButtons.add(MixerButton.BTN_CH1)
        }
        buttonInputDriverTwo = buttonInputDriver(MixerButton.BTN_CH2).apply {
            register(); mixerButtons.add(MixerButton.BTN_CH2)
        }

        val service = PeripheralManagerService()
        val spiBusList = service.spiBusList
        Log.i(TAG, "SPI Bus List in the device " + spiBusList.size)

        if (spiBusList.size <= 0) {
            Log.i(TAG, "Sorry your device does not support SPI")
            return
        }

        try {

            Log.i(TAG, "Open SPI Device")
            spiDevice = service.openSpiDevice(spiBusList[0])
            Log.i(TAG, "SPI Device configuration")


            spiDevice?.let {
                configureSpiDevice(spiDevice!!)
                handler.post(deviceReadThread)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }

    }

    private fun buttonInputDriver(button: MixerButton): ButtonInputDriver {
        return ButtonInputDriver(button.pin, Button.LogicState.PRESSED_WHEN_LOW, button.key)
    }

    fun onClose() {
        buttonInputDriverOne.unregister()
        buttonInputDriverTwo.unregister()
        buttonInputDriverOne.close()
        buttonInputDriverTwo.close()

        buttonA.close()
        buttonB.close()
        buttonC.close()

        handler.removeCallbacks(deviceReadThread)
        // Close the Gpio pin.


        if (spiDevice != null) {
            try {
                spiDevice!!.close()
                spiDevice = null
            } catch (e: IOException) {
                Log.w(TAG, "Unable to close SPI device", e)
            } finally {

            }
        }
    }

    // SPI

    /* By default we have selected channel 0 in the ADC
    * Before reading data we need to write about the channel which we will be reading from.
    * */
    private val deviceReadThread = object : Runnable {
        override fun run() {
            // Exit Runnable if the GPIO is already closed
            if (spiDevice == null) {
                return
            }
            try {

                Log.i(TAG, "Reading from the SPI")

                val data = ByteArray(3)
                val response = ByteArray(3)
                data[0] = 1
                val a2dChannel = 0
                data[1] = (8 shl 4).toByte()
                data[2] = 0

                //full duplex mode
                spiDevice!!.transfer(data, response, 3)

//                var a2dVal = 0
//                a2dVal = response[1] shl 8 and 768 //merge data[1] & data[2] to get result
//                a2dVal = a2dVal or (response[2] and 0xff)


                handler.postDelayed(this, 100)
            } catch (e: IOException) {
                Log.e(TAG, "Error on PeripheralIO API", e)
            }

        }
    }


    @Throws(IOException::class)
    fun configureSpiDevice(device: SpiDevice) {
        // Low clock, leading edge transfer
        device.setMode(SpiDevice.MODE0)
        device.setBitsPerWord(8)          // 8 BPW
        device.setFrequency(1000000)     // 1MHz
        device.setBitJustification(false) // MSB first
    }
}