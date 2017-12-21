package com.tomaszrykala.midimixerandroidthings;

/**
 * This is a list of the absolute frequencies in hertz (cycles per second) of the keys of a modern
 * 88-key standard or 102-key extended piano in twelve-tone equal temperament, with the 49th key,
 * the fifth A (called A4), tuned to 440 Hz (referred to as A440). Each successive pitch is derived
 * by multiplying (ascending) or dividing (descending) the previous by the twelfth root of two.
 * <p>
 * Source: https://en.wikipedia.org/wiki/Piano_key_frequencies
 * <p>
 * Naming convention: C4 = C4; Cs4 = C#4
 */
public interface Key {

    // octave 0
    double C0 = 16.3516;
    double Cs0 = 17.3239;
    double D0 = 18.3540;
    double Ds0 = 19.4454;
    double E0 = 20.6017;
    double F0 = 21.8268;
    double Fs0 = 23.1247;
    double G0 = 24.4997;
    double Gs0 = 25.9565;
    double A0 = 27.5000;
    double As0 = 29.1352;
    double B0 = 30.8677;

    // octave 1
    double C1 = 32.7032;
    double Cs1 = 34.6478;
    double D1 = 36.7081;
    double Ds1 = 38.8909;
    double E1 = 41.2034;
    double F1 = 43.6535;
    double Fs1 = 46.2493;
    double G1 = 48.9994;
    double Gs1 = 51.9131;
    double A1 = 55.0000;
    double As1 = 58.2705;
    double B1 = 61.7354;

    // octave 2
    double C2 = 65.4064;
    double Cs2 = 69.2957;
    double D2 = 73.4162;
    double Ds2 = 77.7817;
    double E2 = 82.4069;
    double F2 = 87.3071;
    double Fs2 = 92.4986;
    double G2 = 97.9989;
    double Gs2 = 103.826;
    double A2 = 110.000;
    double As2 = 116.541;
    double B2 = 123.471;

    // octave 3
    double C3 = 130.813;
    double Cs3 = 138.591;
    double D3 = 146.832;
    double Ds3 = 155.563;
    double E3 = 164.814;
    double F3 = 174.614;
    double Fs3 = 184.997;
    double G3 = 195.998;
    double Gs3 = 207.652;
    double A3 = 220.000;
    double As3 = 233.082;
    double B3 = 246.942;

    // octave 4
    double C4 = 261.626;
    double Cs4 = 277.183;
    double D4 = 293.665;
    double Ds4 = 311.127;
    double E4 = 329.628;
    double F4 = 349.228;
    double Fs4 = 369.994;
    double G4 = 391.995;
    double Gs4 = 415.305;
    double A4 = 440.000;
    double As4 = 466.164;
    double B4 = 493.883;

    // octave 5
    double C5 = 523.251;
    double Cs5 = 554.365;
    double D5 = 587.330;
    double Ds5 = 622.254;
    double E5 = 659.255;
    double F5 = 698.456;
    double Fs5 = 739.989;
    double G5 = 783.991;
    double Gs5 = 830.609;
    double A5 = 880.000;
    double As5 = 932.328;
    double B5 = 987.767;

    // octave 6
    double C6 = 1046.50;
    double Cs6 = 1108.73;
    double D6 = 1174.66;
    double Ds6 = 1244.51;
    double E6 = 1318.51;
    double F6 = 1396.91;
    double Fs6 = 1479.98;
    double G6 = 1567.98;
    double Gs6 = 1661.22;
    double A6 = 1760.00;
    double As6 = 1864.66;
    double B6 = 1975.53;

    // octave 7
    double C7 = 2093.00;
    double Cs7 = 2217.46;
    double D7 = 2349.32;
    double Ds7 = 2489.02;
    double E7 = 2637.02;
    double F7 = 2793.83;
    double Fs7 = 2959.96;
    double G7 = 3135.96;
    double Gs7 = 3322.44;
    double A7 = 3520.00;
    double As7 = 3729.31;
    double B7 = 3951.07;

    // octave 8
    double C8 = 4186.01;
    double Cs8 = 4434.92;
    double D8 = 4698.64;
    double Ds8 = 4978.03;
    double E8 = 5274.04;
    double F8 = 5587.65;
}
