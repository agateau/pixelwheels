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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class Packer {
    public static void main(String[] args) {
        String baseDir;
        if (args.length > 0) {
            baseDir = args[0];
        } else {
            baseDir = ".";
        }
        packTextures(baseDir);
    }

    private static void packTextures(String baseDir) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.filterMin = Texture.TextureFilter.Nearest;
        settings.filterMag = Texture.TextureFilter.Nearest;
        settings.pot = false;
        settings.combineSubdirectories = true;

        String inputDir = baseDir + "/core/assets/sprites";
        String outputDir = baseDir + "/android/assets/sprites";
        String packName = "sprites";
        TexturePacker.process(settings, inputDir, outputDir, packName);

        settings.filterMin = Texture.TextureFilter.Linear;
        settings.filterMag = Texture.TextureFilter.Linear;
        inputDir = baseDir + "/core/assets/ui";
        outputDir = baseDir + "/android/assets/ui";
        packName = "uiskin";
        TexturePacker.process(settings, inputDir, outputDir, packName);

        System.out.println("Done");
    }
}
