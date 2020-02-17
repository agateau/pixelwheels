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
import com.agateau.pixelwheels.utils.DrawUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/** Indicate an input zone on the hud */
public class HudButton extends Widget {
    private static final float BUTTON_OPACITY = 0.5f;
    private static final float DISABLED_BUTTON_OPACITY = 0.2f;

    private final TextureRegion[] mRegions = new TextureRegion[2];
    private final Hud mHud;
    private final ClickListener mClickListener;
    private boolean mEnabled = true;

    /** name is a string like "left" or "right" */
    public HudButton(Assets assets, Hud hud, String name) {
        mHud = hud;
        mRegions[0] = assets.findRegion("hud-" + name);
        mRegions[1] = assets.findRegion("hud-" + name + "-down");
        setTouchable(Touchable.enabled);
        mClickListener = new ClickListener();
        addListener(mClickListener);
    }

    public boolean isPressed() {
        return mClickListener.isVisualPressed();
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public Hud getHud() {
        return mHud;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        float oldA =
                DrawUtils.multiplyBatchAlphaBy(
                        batch,
                        mEnabled ? HudButton.BUTTON_OPACITY : HudButton.DISABLED_BUTTON_OPACITY);

        batch.draw(
                mRegions[isPressed() ? 1 : 0],
                MathUtils.round(getX()),
                MathUtils.round(getY()),
                getWidth(),
                getHeight());

        DrawUtils.setBatchAlpha(batch, oldA);
    }

    @Override
    public void validate() {
        setSize(
                mRegions[0].getRegionWidth() * mHud.getZoom(),
                mRegions[0].getRegionHeight() * mHud.getZoom());
    }
}
