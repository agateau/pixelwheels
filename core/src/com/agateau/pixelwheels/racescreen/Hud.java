/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.pixelwheels.racescreen;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.screens.PwStageScreen;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Hud showing player info during race
 */
public class Hud {
    private final static float BUTTON_SIZE_INCH = 3f / 2.54f; // 3 cm
    // See Android doc for DisplayMetrics.density
    private final static float DIP_DPI = 160;

    private final float BUTTON_SIZE_PX;

    private AnchorGroup mRoot;
    private float mZoom;

    public Hud(Assets assets, Stage stage) {
        mRoot = new AnchorGroup();

        BUTTON_SIZE_PX = assets.findRegion("hud-right").getRegionWidth();
        stage.addActor(mRoot);
    }

    public AnchorGroup getRoot() {
        return mRoot;
    }

    @SuppressWarnings("SameParameterValue")
    public void setScreenRect(int x, int y, int width, int height) {
        mRoot.setBounds(x, y, width, height);
        updateZoom();
    }

    public float getZoom() {
        return mZoom;
    }

    private void updateZoom() {
        float pxSize = BUTTON_SIZE_INCH * DIP_DPI * Gdx.graphics.getDensity();
        float upp = PwStageScreen.getUnitsPerPixel();

        // Multiply by upp to compensate for the viewport scaling set in RaceScreen.resize()
        mZoom = Math.max(pxSize / BUTTON_SIZE_PX * upp, 1);
    }
}
