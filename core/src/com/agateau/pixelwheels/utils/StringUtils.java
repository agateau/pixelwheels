/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.pixelwheels.utils;

import static com.agateau.translations.Translator.tr;

import com.agateau.utils.log.NLog;
import java.util.Locale;

/** String format utils */
public class StringUtils {
    public static String formatRaceTime(float time) {
        int minutes = (int) (time / 60);
        int seconds = (int) (time) % 60;
        int millis = (int) (time * 1000) % 1000;
        return String.format(Locale.US, "%d:%02d.%03d", minutes, seconds, millis);
    }

    public static String formatRankInTable(int rank) {
        return format("%d.", rank);
    }

    public static String format(String fmt, Object... args) {
        return String.format(Locale.getDefault(), fmt, args);
    }

    public static String formatRankInHud(int rank) {
        switch (rank) {
            case 1:
                return tr("1st");
            case 2:
                return tr("2nd");
            case 3:
                return tr("3rd");
            case 4:
                return tr("4th");
            case 5:
                return tr("5th");
            case 6:
                return tr("6th");
            default:
                NLog.e("Unsupported rank %d", rank);
                return String.valueOf(rank);
        }
    }
}
