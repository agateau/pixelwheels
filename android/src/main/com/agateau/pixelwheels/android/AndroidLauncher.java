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

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.utils.FileUtils;
import com.agateau.utils.PlatformUtils;
import com.agateau.utils.log.LogFilePrinter;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StoreConfigurator.setup();

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        config.hideStatusBar = true;
        FileUtils.appName = "tinywheels";
        PwGame game = new PwGame();
        game.setExtraOsInformation(getAndroidInformation());
        setupLogging(game);
        initialize(game, config);
        Gdx.input.setCatchBackKey(true);

        PlatformUtils.setup(
                new PlatformUtils.Impl() {
                    @Override
                    public void openURI(String uri) {
                        Uri webpage = Uri.parse(uri);
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

                        // Check if there's an app that can handle this intent
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            NLog.e("Could not find an app to open %s", uri);
                        }
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // Work around for the status-bar coming back when switching between apps
        // on Android 11.
        // https://github.com/agateau/pixelwheels/issues/140
        // This code comes from:
        // https://github.com/libgdx/libgdx/issues/6006#issuecomment-619967289
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    private void setupLogging(PwGame game) {
        // Some log files end up with duplicated messages: looks like onCreate() can be called more
        // than once. To avoid this, remove any existing printers.
        NLog.clearPrinters();

        AndroidLogFileOpener opener = new AndroidLogFileOpener(this);
        LogFilePrinter printer =
                new LogFilePrinter(Constants.LOG_FILENAME, Constants.LOG_MAX_SIZE, opener);
        NLog.addPrinter(printer);

        NLog.addPrinter(new AndroidNLogPrinter());

        AndroidLogExporter exporter = new AndroidLogExporter(this, printer);
        game.setLogExporter(exporter);
    }

    private static String getAndroidInformation() {
        return StringUtils.format(
                "Android: version='%s' sdk='%d'", Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
    }
}
