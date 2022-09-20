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
package com.agateau.pixelwheels.tools;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Languages;
import com.agateau.pixelwheels.screens.VehicleDrawer;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.utils.ScreenshotCreator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 * Command-line tool to generate PNGs of the game vehicles, complete with wheels and shadows
 *
 * <p>The PNGS are used in <https://agateau.com/projects/pixelwheels/vehicles/>.
 */
public class VehicleThumbnailGenerator {
    private static final int THUMB_WIDTH = 68;
    private static final int THUMB_HEIGHT = 102;

    public static void main(String[] args) {
        new CommandLineApplication(
                "VehicleThumbnailGenerator",
                THUMB_WIDTH * 2,
                THUMB_HEIGHT * 2,
                () -> {
                    FileHandle dstDir = Gdx.files.absolute(args[0]);
                    Assets assets = loadAssets();

                    VehicleDrawer drawer = new VehicleDrawer(assets);
                    drawer.setCenter(THUMB_WIDTH / 2f, THUMB_HEIGHT / 2f);

                    TextureRegion bg = assets.ui.atlas.findRegion("garage-ground");
                    TiledDrawable bgDrawable = new TiledDrawable(bg);

                    SpriteBatch batch = new SpriteBatch();

                    batch.setProjectionMatrix(
                            new Matrix4().setToOrtho2D(0, 0, THUMB_WIDTH, THUMB_HEIGHT));
                    batch.setTransformMatrix(
                            new Matrix4().scale(1, -1, 1).translate(0, -THUMB_HEIGHT, 0));

                    for (VehicleDef def : assets.vehicleDefs) {
                        // TiledDrawable leaves gaps between tiles :/. Workaround this by painting
                        // the
                        // background with the main color of the bgDrawable texture.
                        Gdx.gl.glClearColor(0.325f, 0.306f, 0.431f, 1);
                        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                        batch.begin();
                        bgDrawable.draw(batch, 0, 0, THUMB_WIDTH, THUMB_HEIGHT);

                        drawer.setVehicleDef(def);
                        drawer.draw(batch);
                        batch.end();

                        Pixmap pix = ScreenshotCreator.takeScreenshot();

                        FileHandle thumbFile = dstDir.child(def.id + ".png");
                        PixmapIO.writePNG(thumbFile, pix);
                    }
                });
    }

    private static Assets loadAssets() {
        Assets assets = new Assets();
        // We need to set the language, because this initializes assets.ui.
        assets.setLanguage(Languages.DEFAULT_ID);
        return assets;
    }
}
