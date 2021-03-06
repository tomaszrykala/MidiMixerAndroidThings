package com.tomaszrykala.midimixerandroidthings.driver

class Driver {

    private interface BoardDefaults {
        val spio0: String
        val spio1: String
        val miso: String
        val mosi: String
        val sclk: String

        val btn0: String
        val btn1: String
        val btn2: String
        val btn3: String
    }

    private class Rpi3BoardDefaults : BoardDefaults {
        override val spio0: String = "BCM8" // "SPI0.0"
        override val spio1: String = "BCM7" // "SPI0.1"
        override val miso: String = "BCM9"
        override val mosi: String = "BCM10"
        override val sclk: String = "BCM11"

        override val btn0: String = "BCM26"
        override val btn1: String = "BCM13"
        override val btn2: String = "BCM6"
        override val btn3: String = "BCM5"
    }

    private val boardDefaults = Rpi3BoardDefaults()

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

    fun getBtn2(): String {
        return boardDefaults.btn2
    }

    fun getBtn3(): String {
        return boardDefaults.btn3
    }
}