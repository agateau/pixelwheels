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
package com.agateau.utils.log;

import com.badlogic.gdx.Application;

/** Implementation of Printer which logs to System.err */
public class SystemErrPrinter implements NLog.Printer {
    private final StringBuilder mStringBuilder = new StringBuilder();

    @Override
    public void print(int level, String tag, String message) {
        String levelString;
        if (level == Application.LOG_DEBUG) {
            levelString = "D";
        } else if (level == Application.LOG_INFO) {
            levelString = "I";
        } else { // LOG_ERROR
            levelString = "E";
        }
        mStringBuilder.setLength(0);

        NLogPrinterUtils.appendTimeStamp(mStringBuilder);

        mStringBuilder.append(' ');
        mStringBuilder.append(levelString);

        mStringBuilder.append(" [");
        mStringBuilder.append(tag);
        mStringBuilder.append("] ");
        mStringBuilder.append(message);
        System.err.println(mStringBuilder);
    }
}
