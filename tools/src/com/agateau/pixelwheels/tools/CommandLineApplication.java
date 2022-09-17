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
package com.agateau.pixelwheels.tools;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Convenience abstract class to create command line apps while getting access to all Gdx.* services
 */
public class CommandLineApplication extends Lwjgl3Application {
    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 50;

    private static class AppAdapter extends ApplicationAdapter {
        final Runnable mRunnable;

        AppAdapter(Runnable runnable) {
            mRunnable = runnable;
        }

        @Override
        public void create() {
            mRunnable.run();
            System.exit(0);
        }
    }

    public CommandLineApplication(String title, Runnable runnable) {
        this(title, DEFAULT_WIDTH, DEFAULT_HEIGHT, runnable);
    }

    public CommandLineApplication(String title, int width, int height, Runnable runnable) {
        // Code never returns from the constructor
        super(new AppAdapter(runnable), createConfig(title, width, height));
    }

    private static Lwjgl3ApplicationConfiguration createConfig(
            String title, int width, int height) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.disableAudio(true);
        config.setWindowedMode(width, height);
        config.setTitle(title);
        return config;
    }
}
