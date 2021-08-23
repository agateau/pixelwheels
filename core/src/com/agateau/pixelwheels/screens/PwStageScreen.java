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

import com.agateau.pixelwheels.debug.Debug;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.ui.StageScreen;
import com.agateau.ui.UiAssets;
import com.agateau.ui.UiInputMapper;
import com.agateau.ui.VirtualKey;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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

        if (Debug.instance.logUiActivities) {
            setupUiLogging();
        }
    }

    private void setupUiLogging() {
        prependInputProcessor(
                new InputAdapter() {
                    private final StringBuilder mStringBuilder = new StringBuilder(200);
                    private final Vector2 mTmp = new Vector2();

                    @Override
                    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                        Stage stage = getStage();
                        Viewport viewport = stage.getViewport();
                        NLog.d(
                                "viewport: x=%d y=%d w=%d h=%d\ngutter: left=%d right=%d top=%d bottom=%d",
                                viewport.getScreenX(),
                                viewport.getScreenY(),
                                viewport.getScreenWidth(),
                                viewport.getScreenHeight(),
                                viewport.getLeftGutterWidth(),
                                viewport.getRightGutterWidth(),
                                viewport.getTopGutterHeight(),
                                viewport.getBottomGutterHeight());

                        NLog.d(
                                "screenX=%d screenY=%d (pointer=%d button=%d)",
                                screenX, screenY, pointer, button);

                        mTmp.set(screenX, screenY);
                        stage.screenToStageCoordinates(mTmp);
                        NLog.d("stageX=%f stageY=%f", mTmp.x, mTmp.y);

                        Actor hitActor = stage.hit(mTmp.x, mTmp.y, false /* touchable */);
                        Actor touchableHitActor = stage.hit(mTmp.x, mTmp.y, true /* touchable */);
                        NLog.d("Actor at coord: %s (touchable: %s)", hitActor, touchableHitActor);
                        return false;
                    }

                    @Override
                    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                        NLog.d(
                                "x=%d y=%d (pointer=%d button=%d)",
                                screenX, screenY, pointer, button);
                        mStringBuilder.setLength(0);
                        mStringBuilder.append("\n# Scene dump start\n");
                        UiUtils.dumpStage(mStringBuilder, getStage());
                        mStringBuilder.append("# Scene dump stop");
                        NLog.d(mStringBuilder.toString());
                        return false;
                    }
                });
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
