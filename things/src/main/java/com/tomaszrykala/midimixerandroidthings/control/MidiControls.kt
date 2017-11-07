package com.tomaszrykala.midimixerandroidthings.control

import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import com.tomaszrykala.midimixerandroidthings.mvp.MidiControllerContract

class MidiControls {

    private lateinit var buttonA: Button
    private lateinit var buttonB: Button
    private lateinit var buttonC: Button

    fun onStart(presenter: MidiControllerContract.Presenter) {

        buttonA = RainbowHat.openButtonA().apply {
            setOnButtonEventListener { _, pressed ->
                presenter.onPressed(MidiButton(0, pressed))
            }
        }

        buttonB = RainbowHat.openButtonB().apply {
            setOnButtonEventListener { _, pressed ->
                presenter.onPressed(MidiButton(1, pressed))
            }
        }

        buttonC = RainbowHat.openButtonC().apply {
            setOnButtonEventListener { _, _ ->
                presenter.onReset()
            }
        }
    }

    fun onClose() {
        buttonA.close()
        buttonB.close()
        buttonC.close()
    }
}