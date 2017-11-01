package com.tomaszrykala.midimixerandroidthings.midi.legacy;

import android.media.midi.MidiReceiver;
import android.util.Log;

import java.io.IOException;

public final class MidiBridge {

    private static final String TAG = MidiBridge.class.getSimpleName();

    private MidiReceiver mMidiReceiver;

    private MidiBridge(MidiReceiver receiver) {
        mMidiReceiver = receiver;
    }

    /**
     * Creates an instance of {@link MidiBridge} with a {@link MidiReceiver} to send a {@link MidiBridgeEvent} to.
     *
     * @param receiver {@link MidiReceiver} to send the {@link MidiBridgeEvent} to.
     * @return A new instance of {@link MidiBridge} withs its receiver.
     */
    public static MidiBridge to(MidiReceiver receiver) {
        return new MidiBridge(receiver);
    }

    /**
     * Sends a new {@link MidiBridgeEvent}.
     *
     * @param event {@link MidiBridgeEvent} to send. Nothing is sent if it's null.
     */
    public void send(MidiBridgeEvent event) {
        if (event == null) return;
        midiCommand(event);
    }

    private void midiCommand(MidiBridgeEvent event) {
        midiCommand(event.getType().value + event.getChannel(), event.getData1(), event.getData2());
    }

    private void midiCommand(int status, int data1, int data2) {
        byte[] buffer = new byte[3];
        buffer[0] = (byte) status;
        buffer[1] = (byte) data1;
        buffer[2] = (byte) data2;
        long now = System.nanoTime();
        midiSend(buffer, now);
    }

    private void midiSend(byte[] buffer, long timestamp) {
        int count = buffer.length;
        try {
            if (mMidiReceiver != null) {
                mMidiReceiver.send(buffer, 0, count, timestamp);
            } else {
                Log.i(TAG, "Select receiver for keys.");
            }
        } catch (IOException e) {
            Log.e(TAG, "mKeyboardReceiverSelector.send() failed " + e);
        }
    }
}
