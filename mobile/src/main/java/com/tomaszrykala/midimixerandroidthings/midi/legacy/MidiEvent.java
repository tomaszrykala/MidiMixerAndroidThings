package com.tomaszrykala.midimixerandroidthings.midi.legacy;

public interface MidiEvent {

    byte DEFAULT_VELOCITY = 64;
    byte DEFAULT_NUMBER = 0;

    enum Type {

        NOTE_OFF(STATUS_NOTE_OFF), NOTE_ON(STATUS_NOTE_ON), CONTROL_CHANGE(STATUS_CONTROL_CHANGE);

        public final byte value;

        Type(byte value) {
            this.value = value;
        }
    }

    byte STATUS_NOTE_OFF = (byte) 0x80;
    byte STATUS_NOTE_ON = (byte) 0x90;
    byte STATUS_POLYPHONIC_AFTERTOUCH = (byte) 0xA0;
    byte STATUS_CONTROL_CHANGE = (byte) 0xB0;
    byte STATUS_PROGRAM_CHANGE = (byte) 0xC0;
    byte STATUS_CHANNEL_PRESSURE = (byte) 0xD0;
    byte STATUS_PITCH_BEND = (byte) 0xE0;
}