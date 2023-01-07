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

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;

/** Implementation of Gdx ApplicationLogger to route Gdx logs through NLog */
public class NLogGdxApplicationLogger implements ApplicationLogger {
    public static void install() {
        Gdx.app.setApplicationLogger(new NLogGdxApplicationLogger());
    }

    @Override
    public void log(String tag, String message) {
        NLog.print(NLog.Level.INFO, tag, message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        NLog.print(NLog.Level.INFO, tag, "%s. Exception: %s", message, exception.getStackTrace());
    }

    @Override
    public void error(String tag, String message) {
        NLog.print(NLog.Level.ERROR, tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        NLog.print(NLog.Level.ERROR, tag, "%s. Exception: %s", message, exception.getStackTrace());
    }

    @Override
    public void debug(String tag, String message) {
        NLog.print(NLog.Level.DEBUG, tag, message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        NLog.print(NLog.Level.DEBUG, tag, "%s. Exception: %s", message, exception.getStackTrace());
    }
}
