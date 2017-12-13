/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
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
package com.agateau.tinywheels;

import com.agateau.ui.MenuInputHandler;
import com.agateau.ui.StageScreen;
import com.agateau.ui.VirtualKey;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

/**
 * A stage screen using the correct size for Tiny Wheels
 */
public abstract class TwStageScreen extends StageScreen {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    private MenuInputHandler mMenuInputHandler = new MenuInputHandler();

    public TwStageScreen() {
        super(new ScalingViewport(Scaling.fit, WIDTH, HEIGHT));
    }

    public MenuInputHandler getMenuInputHandler() {
        return mMenuInputHandler;
    }

    @Override
    public void act(float delta) {
        mMenuInputHandler.act(delta);
        if (mMenuInputHandler.isJustPressed(VirtualKey.BACK)) {
            onBackPressed();
        }
    }
}
