/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.racescreen.debug;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.gameobject.GameObjectAdapter;
import com.agateau.pixelwheels.map.LapPosition;
import com.agateau.pixelwheels.map.LapPositionTable;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.map.WaypointStore;
import com.agateau.pixelwheels.racescreen.GameRenderer;
import com.agateau.pixelwheels.utils.DrawUtils;
import com.agateau.pixelwheels.utils.OrientedPoint;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * A debug object. When enabled, clicking on the screen places a target on the clicked position and
 * another target at the matching position on the waypoint polygon, if any.
 */
public class DropLocationDebugObject extends GameObjectAdapter {
    private final Assets mAssets;
    private final GameRenderer mGameRenderer;
    private final Track mTrack;
    private final Vector2 mCoord = new Vector2();
    private final Vector2 mProjectedCoord = new Vector2();

    private boolean mValid = false;
    private boolean mActive = false;
    private boolean mTouched = false;

    public DropLocationDebugObject(Assets assets, GameRenderer gameRenderer, Track track) {
        mAssets = assets;
        mGameRenderer = gameRenderer;
        mTrack = track;
    }

    @Override
    public void act(float delta) {
        if (!mActive) {
            return;
        }
        mTouched = Gdx.input.isTouched();
        if (mTouched) {
            mCoord.set(Gdx.input.getX(), Gdx.input.getY());
            mGameRenderer.mapFromScreen(mCoord);

            float PFU = 1 / Constants.UNIT_FOR_PIXEL;

            LapPositionTable table = mTrack.getLapPositionTable();
            LapPosition lapPosition = table.get((int) (mCoord.x * PFU), (int) (mCoord.y * PFU));
            if (lapPosition == null) {
                mValid = false;
            } else {
                mValid = true;
                WaypointStore store = mTrack.getWaypointStore();
                OrientedPoint point = store.getValidPosition(mCoord, lapPosition.getLapDistance());
                mProjectedCoord.set(point.x, point.y);
            }
        }
    }

    @Override
    public void draw(Batch batch, ZLevel zLevel, Rectangle viewBounds) {
        if (!mActive || zLevel != ZLevel.FLYING_HIGH) {
            return;
        }
        TextureRegion region = mAssets.target;
        if (mTouched) {
            DrawUtils.drawCentered(batch, region, mCoord, Constants.UNIT_FOR_PIXEL, 0);
        }
        if (mValid) {
            DrawUtils.drawCentered(batch, region, mProjectedCoord, Constants.UNIT_FOR_PIXEL, 45);
        }
    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    public Button createDebugButton(Skin skin) {
        TextButton button = new TextButton("Waypoints", skin, "tiny");
        button.setProgrammaticChangeEvents(false);
        button.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        mActive = button.isChecked();
                    }
                });
        return button;
    }
}
