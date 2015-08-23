package com.greenyetilab.tinywheels;

/**
 * Created by aurelien on 21/11/14.
 */
public class StringUtils {
    public static String formatRaceTime(float time) {
        int minutes = (int)(time / 60);
        int seconds = (int)(time) % 60;
        int fracs = (int)(time * 100) % 100;
        return String.format("%d:%02d.%02d", minutes, seconds, fracs);
    }

    public static String formatSpeed(float speedMPS) {
        int kmh = (int)(speedMPS * 3.6f);
        return String.valueOf(kmh) + " km/h";
    }

    public static String getRankSuffix(int rank) {
        switch (rank) {
        case 1:
            return "st";
        case 2:
            return "nd";
        case 3:
            return "rd";
        default:
            return "th";
        }
    }
}
