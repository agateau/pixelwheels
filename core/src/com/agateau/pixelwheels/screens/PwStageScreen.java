/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.screens;

import com.agateau.ui.StageScreen;
import com.agateau.ui.UiAssets;
import com.agateau.ui.UiInputMapper;
import com.agateau.ui.VirtualKey;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

/** A stage screen using the correct size for Pixel Wheels */
public abstract class PwStageScreen extends StageScreen {
    public static final int WIDTH = 960;
    public static final int HEIGHT = 540;

    public PwStageScreen(UiAssets uiAssets) {
        super(new ScalingViewport(Scaling.fit, WIDTH, HEIGHT));

        Image image = new Image();
        image.setDrawable(new TiledDrawable(uiAssets.background));
        image.setFillParent(true);
        getStage().addActor(image);
    }

    @Override
    public boolean isBackKeyPressed() {
        return UiInputMapper.getInstance().isKeyJustPressed(VirtualKey.BACK);
    }

    public static float getUnitsPerPixel() {
        return Math.max(
                (float) (PwStageScreen.WIDTH) / Gdx.graphics.getWidth(),
                (float) (PwStageScreen.HEIGHT) / Gdx.graphics.getHeight());
    }
}
