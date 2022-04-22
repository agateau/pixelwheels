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
public abstract class CommandLineApplication extends Lwjgl3Application {
    private static class AppAdapter extends ApplicationAdapter {
        CommandLineApplication mApp;
        String[] mArguments;

        @Override
        public void create() {
            System.exit(mApp.run(mArguments));
        }
    }

    public CommandLineApplication(String title, String[] arguments) {
        super(new AppAdapter(), createConfig(title));
        AppAdapter appAdapter = (AppAdapter) getApplicationListener();
        appAdapter.mApp = this;
        appAdapter.mArguments = arguments;
    }

    private static Lwjgl3ApplicationConfiguration createConfig(String title) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.disableAudio(true);
        config.setWindowedMode(100, 50);
        config.setTitle(title);
        return config;
    }

    abstract int run(String[] arguments);
}
