/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
import com.badlogic.gdx.Gdx;

/** Implementation of Printer using Gdx.app logging facilities */
public class GdxPrinter implements NLog.Printer {
    private final String mPrefix;

    public GdxPrinter() {
        this("");
    }

    public GdxPrinter(String prefix) {
        mPrefix = prefix.isEmpty() ? "" : (prefix + ".");
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Override
    public void print(int level, String tag, String message) {
        tag = mPrefix + tag;
        if (level == Application.LOG_DEBUG) {
            Gdx.app.debug(tag, message);
        } else if (level == Application.LOG_INFO) {
            Gdx.app.log(tag, message);
        } else { // LOG_ERROR
            Gdx.app.error(tag, message);
        }
    }
}
