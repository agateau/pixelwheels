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

import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/** Loads a TMX file and creates a screenshot of it as a PNG file */
public class MapScreenshotGenerator {
    private static final int SHOT_SIZE = 150;

    public static void main(String[] args) {
        new CommandLineApplication("MapScreenshotGenerator", args) {
            @Override
            int run(String[] arguments) {
                if (arguments.length == 2) {
                    String shotFileName = arguments[0];
                    String tmxFileName = arguments[1];
                    processFile(shotFileName, tmxFileName);
                } else {
                    FileHandle tmxDir = Gdx.files.absolute("android/assets/maps");
                    FileHandle shotDir = Gdx.files.absolute("core/assets/ui/map-screenshots");
                    for (FileHandle tmxFile : tmxDir.list(".tmx")) {
                        String shotFileName =
                                shotDir.path()
                                        + "/"
                                        + tmxFile.nameWithoutExtension()
                                        + "-generated.png";
                        processFile(shotFileName, tmxFile.path());
                    }
                }
                return 0;
            }
        };
    }

    private static void processFile(String shotFileName, String tmxFileName) {
        FileHandle tmxFile = Gdx.files.absolute(tmxFileName);
        FileHandle shotFile = Gdx.files.absolute(shotFileName);
        if (isOutdated(shotFile, tmxFile)) {
            NLog.i("%s: updating", shotFile.path());
            Pixmap pix1 = generateScreenshot(tmxFile);
            Pixmap pix2 = scaleScreenshot(pix1);
            pix1.dispose();
            PixmapIO.writePNG(shotFile, pix2);
            pix2.dispose();
        } else {
            NLog.i("%s: up to date", shotFile.path());
        }
    }

    private static boolean isOutdated(FileHandle dst, FileHandle src) {
        return dst.lastModified() < src.lastModified();
    }

    private static Pixmap generateScreenshot(FileHandle tmxFile) {
        TiledMap map = new TmxMapLoader().load(tmxFile.path());
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        int mapWidth = (int) (layer.getWidth() * layer.getTileWidth());
        int mapHeight = (int) (layer.getHeight() * layer.getTileHeight());

        FrameBuffer fbo =
                new FrameBuffer(Pixmap.Format.RGB888, mapWidth, mapHeight, false /* hasDepth */);
        OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map);

        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(true /* yDown */, mapWidth, mapHeight);
        renderer.setView(camera);

        fbo.begin();
        renderer.render(new int[] {0, 1});

        return ScreenUtils.getFrameBufferPixmap(0, 0, mapWidth, mapHeight);
    }

    private static Pixmap scaleScreenshot(Pixmap src) {
        int srcW = src.getWidth();
        int srcH = src.getHeight();

        float ratio = (float) SHOT_SIZE / Math.max(srcW, srcH);
        int dstW = (int) (srcW * ratio);
        int dstH = (int) (srcH * ratio);

        Pixmap dst = new Pixmap(SHOT_SIZE, SHOT_SIZE, src.getFormat());
        dst.setFilter(Pixmap.Filter.BiLinear);
        dst.drawPixmap(
                src, 0, 0, srcW, srcH, (SHOT_SIZE - dstW) / 2, (SHOT_SIZE - dstH) / 2, dstW, dstH);
        return dst;
    }
}
