/*
 * Copyright 2025 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.desktop;

import com.agateau.utils.PlatformUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DesktopPlatformUtilsImpl implements PlatformUtils.Impl {
    @Override
    public void openURI(String uri) {
        List<String> command = new ArrayList<>();
        if (SharedLibraryLoader.isLinux) {
            command.add("xdg-open");
        } else if (SharedLibraryLoader.isWindows) {
            command.add("cmd.exe");
            command.add("/c");
            command.add("start");
            command.add(""); // This is the window title
        } else if (SharedLibraryLoader.isMac) {
            command.add("open");
        }
        if (command.isEmpty()) {
            NLog.e("Don't know how to open URL %s on this OS", uri);
            return;
        }
        command.add(uri);
        NLog.i("Opening %s with '%s'", uri, command);
        try {
            new ProcessBuilder(command).start();
        } catch (IOException e) {
            NLog.e("Command failed: %s", e);
            return;
        }

        if (Gdx.graphics.isFullscreen()) {
            // Minimize the window in case the browser did not take the focus
            Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();
            window.iconifyWindow();
        }
    }
}
