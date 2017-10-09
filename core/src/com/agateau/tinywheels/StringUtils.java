/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.tinywheels;

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
