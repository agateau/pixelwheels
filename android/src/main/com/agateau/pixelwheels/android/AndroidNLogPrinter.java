/*
 * Copyright 2023 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.android;

import android.util.Log;
import com.agateau.utils.log.NLog;

/** Implementation of NLog.Printer using Android logging facilities */
public class AndroidNLogPrinter implements NLog.Printer {
    @Override
    public void print(NLog.Level level, String tag, String message) {
        int priority = Log.ERROR;
        switch (level) {
            case DEBUG:
                priority = Log.DEBUG;
                break;
            case INFO:
                priority = Log.INFO;
                break;
            case ERROR:
                priority = Log.ERROR;
                break;
        }
        Log.println(priority, tag, message);
    }
}
