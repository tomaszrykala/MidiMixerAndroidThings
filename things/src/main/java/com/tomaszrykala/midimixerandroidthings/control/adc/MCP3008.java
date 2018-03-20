/*
 * Copyright 2017, Paul Trebilcox-Ruiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
    Paul Trebilcox-Ruiz
    PaulTR@Gmail.com

    Library for communicating with the MCP3008 Analog to Digital Converter.

    Based on an Arduino library by: Uros Petrevski (https://github.com/nodesign/MCP3008)

    Ported from Python code originaly written by Adafruit learning system for rPI:
        http://learn.adafruit.com/send-raspberry-pi-data-to-cosm/python-script

    Initializing with a Raspberry Pi 3B and a TMP36 connected to ADC CH 0.
    Can change these pin numbers in your own projects to be the proper board pins.

    Pinout for sample like so:


    ADC channel 0 -|*   |- VIN
    ADC channel 1 -|    |- VIN
    ADC channel 2 -|    |- Analog GND (For this sample app, I used this ground)
    ADC channel 3 -|    |- Clock
    ADC channel 4 -|    |- MISO pin on board (sometimes listed as D-OUT on chip diagram)
    ADC channel 5 -|    |- MOSI pin on board (sometimes listed as DIN on chip diagram)
    ADC channel 6 -|    |- Chip select (CS)
    ADC channel 7 -|    |- Digital GND (For this sample app, I left this GND disconnected)

    While testing I wired BCM12 to CS, BCM21 to Clock, BCM16 to MOSI (D-OUT) and BCM20 to MISO (D-IN)
*/

package com.tomaszrykala.midimixerandroidthings.control.adc;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

class MCP3008 {

    private final String csGpio;
    private final String clockGpio;
    private final String mosiGpio;
    private final String misoGpio;

    private Gpio csPin;
    private Gpio clockPin;
    private Gpio mosiPin;
    private Gpio misoPin;

    private final Set<Gpio> gpios = new LinkedHashSet<>();

    MCP3008(String csGpio, String clockGpio, String mosiGpio, String misoGpio) {
        this.csGpio = csGpio;
        this.clockGpio = clockGpio;
        this.mosiGpio = mosiGpio;
        this.misoGpio = misoGpio;
    }

    void register() throws IOException {
        PeripheralManager peripheralManager = PeripheralManager.getInstance();
        csPin = peripheralManager.openGpio(csGpio);
        clockPin = peripheralManager.openGpio(clockGpio);
        mosiPin = peripheralManager.openGpio(mosiGpio);
        misoPin = peripheralManager.openGpio(misoGpio);

        csPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        clockPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        mosiPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        misoPin.setDirection(Gpio.DIRECTION_IN);

        gpios.add(csPin);
        gpios.add(clockPin);
        gpios.add(mosiPin);
        gpios.add(misoPin);
    }

    int readAdc(int channel) throws IOException {
        if (channel < 0 || channel > 7) {
            throw new IOException("ADC channel must be between 0 and 7");
        }

        initReadState();
        initChannelSelect(channel);

        return getValueFromSelectedChannel();
    }

    private int getValueFromSelectedChannel() throws IOException {
        int value = 0x0;

        for (int i = 0; i < 12; i++) {
            toggleClock();

            value <<= 0x1;
            if (misoPin.getValue()) {
                value |= 0x1;
            }
        }

        csPin.setValue(true);

        value >>= 0x1; // first bit is 'null', so drop it

        return value;
    }

    private void initReadState() throws IOException {
        csPin.setValue(true);
        clockPin.setValue(false);
        csPin.setValue(false);
    }

    private void initChannelSelect(int channel) throws IOException {
        int commandOut = channel;
        commandOut |= 0x18; // start bit + single-ended bit
        commandOut <<= 0x3; // we only need to send 5 bits

        for (int i = 0; i < 5; i++) {

            if ((commandOut & 0x80) != 0x0) {
                mosiPin.setValue(true);
            } else {
                mosiPin.setValue(false);
            }

            commandOut <<= 0x1;

            toggleClock();
        }
    }

    private void toggleClock() throws IOException {
        clockPin.setValue(true);
        clockPin.setValue(false);
    }

    void unregister() {
        for (Gpio gpio : gpios) {
            if (gpio != null)
                try {
                    gpio.close();
                } catch (IOException ignore) {
                    // do nothing
                }
        }
    }
}
