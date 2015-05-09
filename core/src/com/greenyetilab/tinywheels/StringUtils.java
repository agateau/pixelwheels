package com.greenyetilab.tinywheels;

/**
 * Created by aurelien on 21/11/14.
 */
public class StringUtils {
    public static String formatRaceTime(float time) {
        int minutes = (int)(time / 60);
        int seconds = (int)(time) % 60;
        int tens = (int)(time * 10) % 10;
        return String.format("%d:%02d.%d", minutes, seconds, tens);
    }

    public  static String formatSpeed(float speedMPS) {
        int kmh = (int)(speedMPS * 3.6f);
        return String.valueOf(kmh) + " km/h";
    }
}
