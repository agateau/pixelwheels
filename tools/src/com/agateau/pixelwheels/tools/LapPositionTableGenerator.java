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

import com.agateau.pixelwheels.map.LapPositionTable;
import com.agateau.pixelwheels.map.LapPositionTableIO;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/** Load a .tmx file and save its corresponding lap position table as a PNG file */
public class LapPositionTableGenerator {
    public static void main(String[] args) {
        new CommandLineApplication("LapPositionTableGenerator", args) {
            @Override
            int run(String[] arguments) {
                FileHandle tmxFile = Gdx.files.absolute(arguments[0]);
                FileHandle tableFile = Gdx.files.absolute(arguments[1]);
                LapPositionTableGenerator.generateTable(tmxFile, tableFile);
                return 0;
            }
        };
    }

    public static void generateTable(FileHandle tmxFile, FileHandle tableFile) {
        TiledMap map = new TmxMapLoader().load(tmxFile.path());
        LapPositionTable table = LapPositionTableIO.load(map);

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        int width = layer.getWidth() * ((int) layer.getTileWidth());
        int height = layer.getHeight() * ((int) layer.getTileHeight());

        Pixmap pixmap = LapPositionTableIO.createPixmap(table, width, height);
        PixmapIO.writePNG(tableFile, pixmap);
    }
}
