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
package com.agateau.pixelwheels.android;

import android.content.Context;
import android.os.Bundle;
import com.agateau.pixelwheels.PwGame;
import com.agateau.utils.FileUtils;
import com.agateau.utils.log.LogFilePrinter;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class AndroidLauncher extends AndroidApplication {
    private static final String LOG_FILENAME = "pixelwheels.log";

    private static class AndroidLogFileOpener implements LogFilePrinter.LogFileOpener {
        private final Context mContext;

        AndroidLogFileOpener(Context context) {
            mContext = context;
        }

        @Override
        public FileOutputStream openLogFile(String filename) {
            try {
                return mContext.openFileOutput(
                        LOG_FILENAME, Context.MODE_PRIVATE | Context.MODE_APPEND);
            } catch (FileNotFoundException e) {
                NLog.e("Failed to open %s for writing: %s", LOG_FILENAME, e);
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StoreConfigurator.setup();

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        config.hideStatusBar = true;
        FileUtils.appName = "tinywheels";
        setupLogging();
        initialize(new PwGame(), config);
        Gdx.input.setCatchBackKey(true);
    }

    private void setupLogging() {
        AndroidLogFileOpener opener = new AndroidLogFileOpener(this);
        NLog.addPrinter(new LogFilePrinter(LOG_FILENAME, opener));
    }
}
