package com.tomaszrykala.midimixerandroidthings.driver

import android.os.Build

class Driver {

    private interface BoardDefaults {
        val spio0: String
        val spio1: String
        val miso: String
        val mosi: String
        val sclk: String

        val btn0: String
        val btn1: String
    }

    private class Rpi3BoardDefaults : BoardDefaults {
        override val spio0: String = "BCM8" // "SPI0.0"
        override val spio1: String = "BCM7" // "SPI0.1"
        override val miso: String = "BCM9"
        override val mosi: String = "BCM10"
        override val sclk: String = "BCM11"

        override val btn0: String = "BCM21"
        override val btn1: String = "BCM20"
    }

    private class Imx7BoardDefaults : BoardDefaults {
        override val spio0: String = "SPI3 (SS0)" // "SPI3.0" // GPIO6_IO26
        override val spio1: String = "SPI3 (SS1)" // "SPI3.1" // GPIO6_IO24
        override val miso: String = "SPI3 (MISO)" // GPIO6_IO23
        override val mosi: String = "SPI3 (MOSI)" // GPIO6_IO19
        override val sclk: String = "SPI3 (SCLK)" // GPIO6_IO23

        override val btn0: String = "GPIO6_IO14"
        override val btn1: String = "GPIO6_IO15"
    }

    private val boardDefaults = if (Build.DEVICE == "rpi3") Rpi3BoardDefaults() else Imx7BoardDefaults()

    fun getSpio0(): String {
        return boardDefaults.spio0
    }

    fun getSpio1(): String {
        return boardDefaults.spio1
    }

    fun getMiso(): String {
        return boardDefaults.miso
    }

    fun getMosi(): String {
        return boardDefaults.mosi
    }

    fun getSclk(): String {
        return boardDefaults.sclk
    }

    fun getBtn0(): String {
        return boardDefaults.btn0
    }

    fun getBtn1(): String {
        return boardDefaults.btn1
    }
}