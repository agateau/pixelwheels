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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Indicate an input zone on the hud
 */
public class PieButton extends Actor {
    private final static float BUTTON_OPACITY = 0.6f;
    private final static float DISABLED_BUTTON_OPACITY = 0.3f;

    private final TextureRegion[] mRegions = new TextureRegion[2];
    private final Hud mHud;

    private ClickListener mClickListener = new ClickListener();

    private float mFrom = 0f;
    private float mTo = 90f;
    private float mRadius = 100;
    private boolean mEnabled = true;

    /**
     * name is a string like "left" or "right"
     */
    public PieButton(Assets assets, Hud hud, String name) {
        mHud = hud;
        mRegions[0] = assets.findRegion("hud-" + name);
        mRegions[1] = assets.findRegion("hud-" + name + "-down");
        setTouchable(Touchable.enabled);
        addListener(mClickListener);
    }

    public void setSector(int from, int to) {
        mFrom = from;
        mTo = to;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    @Override
    public void act(float dt) {
        updateSize();
    }

    boolean isPressed() {
        return mClickListener.isVisualPressed();
    }

    void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        Color color = batch.getColor();
        float oldA = color.a;
        color.a = alpha * (mEnabled ? BUTTON_OPACITY : DISABLED_BUTTON_OPACITY);
        batch.setColor(color);

        TextureRegion region = mRegions[isPressed() ? 1 : 0];
        batch.draw(
                region,
                MathUtils.round(getX()),
                MathUtils.round(getY()),
                region.getRegionWidth() * mHud.getZoom(),
                region.getRegionHeight() * mHud.getZoom());

        color.a = oldA;
        batch.setColor(color);
    }

    private void updateSize() {
        setWidth(mRegions[0].getRegionWidth() * mHud.getZoom());
        setHeight(mRegions[0].getRegionHeight() * mHud.getZoom());

        if (mFrom >= 90) {
            setOrigin(getWidth(), 0);
        } else {
            setOrigin(0, 0);
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        // Let the base implementation perform AABB collision detection
        Actor result = super.hit(x, y, touchable);
        if (result == null) {
            return null;
        }

        // Check if we are outside the radius
        x -= getOriginX();
        y -= getOriginY();
        float radius = mRadius * mHud.getZoom();
        if (x * x + y * y > radius * radius) {
            return null;
        }

        // Now check if we hit the right sector
        float angle = MathUtils.atan2(y, x) * MathUtils.radDeg;
        if (mFrom <= angle && angle <= mTo) {
            return this;
        } else {
            return null;
        }
    }
}
