package com.tomaszrykala.midimixerandroidthings.control.adc;

import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class McpDriver {

    private static final String TAG = "MCP3008";
    private static final int CHANNELS = 8;

    private final List<Listener> listeners = new ArrayList<>(CHANNELS);
    private final MCP3008 mcp3008;
    private Handler handler;

    public interface Listener {
        void onChange(int read);
    }

    public McpDriver(String csPin, String clockPin, String mosiPin, String misoPin) {
        mcp3008 = new MCP3008(csPin, clockPin, mosiPin, misoPin);
    }

    public void start() {
        try {
            mcp3008.register();
        } catch (IOException e) {
            Log.d(TAG, "MCP initialization exception occurred: " + e.getMessage());
        }

        handler = new Handler();
        handler.post(readAdcRunnable);
    }

    public void stop() {
        listeners.clear();
        mcp3008.unregister();
        if (handler != null) {
            handler.removeCallbacks(readAdcRunnable);
        }
    }

    public void addListener(@IntRange(from = 0, to = 7) int analogChannel, @Nullable Listener listener) {
        listeners.add(analogChannel, listener);
    }

    private Runnable readAdcRunnable = new Runnable() {

        private static final long DELAY_MS = 100L; // 0.1 second
        private final int[] lastReads = new int[CHANNELS];

        @Override
        public void run() {
            try {
                for (int i = 0; i < listeners.size(); i++) {
                    final int readAdc = Math.round(mcp3008.readAdc(i) / CHANNELS);
                    if (lastReads[i] != readAdc) {
                        lastReads[i] = readAdc;
                        Log.d(TAG, "ADC " + i + ": " + readAdc);
                        listeners.get(i).onChange(readAdc);
                    }
                }
            } catch (IOException e) {
                Log.d(TAG, "Something went wrong while reading from the ADC: " + e.getMessage());
            }

            handler.postDelayed(this, DELAY_MS);
        }
    };
}
