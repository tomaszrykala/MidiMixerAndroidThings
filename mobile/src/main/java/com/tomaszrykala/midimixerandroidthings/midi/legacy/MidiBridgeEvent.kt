package com.tomaszrykala.midimixerandroidthings.midi.legacy

data class MidiBridgeEvent(val type: MidiEvent.Type,
                           @ByteRange(from = 0) val channel: Byte,
                           @ByteRange(from = 0) val data1: Byte,
                           @ByteRange(from = 0) val data2: Byte)