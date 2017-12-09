/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
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
package com.agateau.tinywheels.desktop;

import com.agateau.tinywheels.TwGame;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        //config.width = 800; config.height = 480;
        config.width = 1280; config.height = 720;
        //config.width = 1920; config.height = 1080;
        /*
        config.width = 1920;
        config.height = 1080;
        config.fullscreen = true;
        */
        config.title = "Tiny Wheels";
        FileUtils.appName = "tinywheels";
        new LwjglApplication(new TwGame(), config);
    }
}
