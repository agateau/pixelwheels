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
package com.agateau.pixelwheels.racescreen;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.screens.PwStageScreen;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/** Hud showing player info during race */
public class Hud {
    private static final float BUTTON_SIZE_INCH = 3f / 2.54f; // 3 cm
    // See Android doc for DisplayMetrics.density
    private static final float DIP_DPI = 160;

    private final float BUTTON_SIZE_PX;

    private final AnchorGroup mRoot;
    private AnchorGroup mInputUiContainer;
    private float mZoom;

    public Hud(Assets assets, Stage stage) {
        mRoot = new AnchorGroup();

        BUTTON_SIZE_PX = assets.findRegion("hud-pie-right").getRegionWidth();
        stage.addActor(mRoot);
    }

    public AnchorGroup getRoot() {
        return mRoot;
    }

    public void deleteInputUiContainer() {
        if (mInputUiContainer == null) {
            return;
        }
        mInputUiContainer.remove();
        mInputUiContainer = null;
    }

    /**
     * Returns an AnchorGroup into which input code should create its UI if it has any.
     *
     * <p>It is important to create the UI in this container rather than in getRoot(), because it
     * makes it possible to remove all the UI when switching between input modes by calling
     * deleteInputUiContainer()
     */
    public AnchorGroup getInputUiContainer() {
        if (mInputUiContainer == null) {
            mInputUiContainer = new AnchorGroup();
            // Make sure touches can reach mRoot
            mInputUiContainer.setTouchable(Touchable.childrenOnly);
            mRoot.addActor(mInputUiContainer);
            mInputUiContainer.setFillParent(true);
        }
        return mInputUiContainer;
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
        if (PlatformUtils.isDesktop()) {
            // Make sure we get sharp screenshots
            // TODO Check if rounding mZoom to an integer is acceptable on mobile so that we can
            // remove that hack
            mZoom = 1;
            return;
        }
        float pxSize = BUTTON_SIZE_INCH * DIP_DPI * Gdx.graphics.getDensity();
        float upp = PwStageScreen.getUnitsPerPixel();

        // Multiply by upp to compensate for the viewport scaling set in RaceScreen.resize()
        mZoom = Math.max(pxSize / BUTTON_SIZE_PX * upp, 1);
    }
}
