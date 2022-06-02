/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.tools.trackeditor;

import com.agateau.utils.FileUtils;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class TrackEditor extends Game {
    private static class Args {
        String tmxFilePath;

        boolean parse(String[] arguments) {
            for (String arg : arguments) {
                if (arg.equals("-h") || arg.equals("--help")) {
                    showHelp();
                    return false;
                }
                if (arg.startsWith("-")) {
                    showError("Unknown option " + arg);
                    return false;
                }
                if (tmxFilePath == null) {
                    tmxFilePath = arg;
                } else {
                    showError("Too many arguments");
                    return false;
                }
            }
            if (tmxFilePath == null) {
                showError("Too few arguments");
                return false;
            }
            return true;
        }

        private static void showError(String message) {
            System.out.println("ERROR: " + message);
            showHelp();
        }

        private static void showHelp() {
            System.out.println("Usage: trackeditor [-h|--help] <tmxfile>");
        }
    }

    private final Args mArgs;

    public TrackEditor(Args args) {
        mArgs = args;
    }

    public static void main(String[] arguments) {
        Args args = new Args();
        if (!args.parse(arguments)) {
            System.exit(1);
        }

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(960, 540);
        config.setTitle("Track Editor");
        new Lwjgl3Application(new TrackEditor(args), config);
    }

    @Override
    public void create() {
        BitmapFont font = loadFont();
        setScreen(new TrackEditorScreen(Gdx.files.absolute(mArgs.tmxFilePath), font));
    }

    private static BitmapFont loadFont() {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.borderWidth = 1f;

        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(FileUtils.assets("fonts/Xolonium-Regular.ttf"));
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
