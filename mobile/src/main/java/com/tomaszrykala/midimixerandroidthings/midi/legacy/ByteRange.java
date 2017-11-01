package com.tomaszrykala.midimixerandroidthings.midi.legacy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Retention(CLASS)
@Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE})
public @interface ByteRange {

    /**
     * Smallest value, inclusive
     */
    byte from() default Byte.MIN_VALUE;

    /**
     * Largest value, inclusive
     */
    byte to() default Byte.MAX_VALUE;
}