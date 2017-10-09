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
public class HudButton extends Actor {
    private final static float BUTTON_OPACITY = 0.6f;
    private final static float ICON_VOFFSET = 2;
    private final static float ICON_VOFFSET_DOWN = 1;

    private final TextureRegion[] mRegions = new TextureRegion[2];
    private final Hud mHud;
    private final ClickListener mClickListener;

    private TextureRegion mIcon = null;

    /**
     * name is a string like "left" or "right"
     */
    public HudButton(Assets assets, Hud hud, String name) {
        mHud = hud;
        mRegions[0] = assets.findRegion("hud-" + name);
        mRegions[1] = assets.findRegion("hud-" + name + "-down");
        setTouchable(Touchable.enabled);
        mClickListener = new ClickListener();
        addListener(mClickListener);
    }

    @Override
    public void act(float dt) {
        updateSize();
    }

    void setIcon(TextureRegion icon) {
        mIcon = icon;
    }

    boolean isPressed() {
        return mClickListener.isVisualPressed();
    }

    @Override
    public void draw(Batch batch, float alpha) {
        Color color = batch.getColor();
        float oldA = color.a;
        color.a = alpha * BUTTON_OPACITY;
        batch.setColor(color);

        drawScaledRegion(batch, mRegions[isPressed() ? 1 : 0], mHud.getZoom(), 0);
        if (mIcon != null) {
            drawScaledRegion(batch, mIcon, mHud.getZoom(), isPressed() ? ICON_VOFFSET_DOWN : ICON_VOFFSET);
        }

        color.a = oldA;
        batch.setColor(color);
    }

    private void drawScaledRegion(Batch batch, TextureRegion region, float zoom, float vMargin) {
        float w = region.getRegionWidth() * zoom;
        float h = region.getRegionHeight() * zoom;
        batch.draw(
                region,
                MathUtils.round(getX() + (getWidth() - w) / 2),
                MathUtils.round(getY() + (getHeight() - h) / 2) + vMargin * zoom,
                w, h
        );
    }

    private void updateSize() {
        setWidth(mRegions[0].getRegionWidth() * mHud.getZoom());
        setHeight(mRegions[0].getRegionHeight() * mHud.getZoom());
    }
}
