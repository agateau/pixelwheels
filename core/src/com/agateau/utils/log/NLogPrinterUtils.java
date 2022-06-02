/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.utils.log;

/** Helper methods to implement printers */
public class NLogPrinterUtils {
    public static void appendTimeStamp(StringBuilder builder) {
        long timeSpent = System.currentTimeMillis();
        long secondsInDay = (timeSpent / 1000) % 86400;
        long hour = secondsInDay / 3600;
        long minutes = (secondsInDay % 3600) / 60;
        long seconds = secondsInDay % 60;
        long millis = timeSpent % 1000;
        appendNumber(builder, hour, 2);
        builder.append(':');
        appendNumber(builder, minutes, 2);
        builder.append(':');
        appendNumber(builder, seconds, 2);
        builder.append('.');
        appendNumber(builder, millis, 3);
    }

    private static void appendNumber(StringBuilder builder, long value, int width) {
        int digitCount = 1;
        for (long v = value; v > 9; v /= 10) {
            digitCount += 1;
        }
        for (int idx = 0; idx < width - digitCount; ++idx) {
            builder.append('0');
        }
        builder.append(value);
    }
}
