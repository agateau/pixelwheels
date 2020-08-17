/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.utils;

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import java.io.IOException;

/** Utility methods to deal with the platform */
public class PlatformUtils {
    private enum UiType {
        BUTTONS,
        TOUCH
    }

    private static UiType sUiType;

    public static boolean isTouchUi() {
        init();
        return sUiType == UiType.TOUCH;
    }

    public static boolean isButtonsUi() {
        return !isTouchUi();
    }

    public static boolean isDesktop() {
        switch (Gdx.app.getType()) {
            case Desktop:
            case HeadlessDesktop:
            case Applet:
            case WebGL:
                return true;
            default:
                return false;
        }
    }

    /** An implementation of Gdx.net.openURI which works on Linux */
    public static void openURI(String uri) {
        if (Gdx.net.openURI(uri)) {
            return;
        }
        NLog.i("Gdx.net.openURI() failed");
        String command = null;
        if (SharedLibraryLoader.isLinux) {
            command = "xdg-open";
        } else if (SharedLibraryLoader.isWindows) {
            command = "start";
        } else if (SharedLibraryLoader.isMac) {
            command = "open";
        }
        if (command == null) {
            NLog.e("Don't know how to open url %s on this OS", uri);
            return;
        }
        try {
            NLog.i("Trying with '%s %s'", command, uri);
            new ProcessBuilder().command(command, uri).start();
        } catch (IOException e) {
            NLog.e("Command failed: %s", e);
        }
    }

    private static void init() {
        if (sUiType != null) {
            return;
        }
        String envValue = System.getenv("AGC_UI_TYPE");
        if (envValue == null) {
            sUiType = isDesktop() ? UiType.BUTTONS : UiType.TOUCH;
        } else {
            sUiType = UiType.valueOf(envValue);
            NLog.d("Forcing UI type to %s", sUiType);
        }
        NLog.i("UI type: %s", sUiType);
    }
}
