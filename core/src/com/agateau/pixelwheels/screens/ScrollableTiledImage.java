/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

class ScrollableTiledImage extends Actor {
    private final float mPixelsPerSecond;
    private final Drawable mDrawable;
    private float mOffset = 0;

    ScrollableTiledImage(Drawable drawable, float pixelsPerSecond) {
        mDrawable = drawable;
        mPixelsPerSecond = pixelsPerSecond;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float tileHeight = mDrawable.getMinHeight();
        mOffset = AgcMathUtils.modulo(mOffset + delta * mPixelsPerSecond, tileHeight);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        float tileHeight = mDrawable.getMinHeight();
        float origY = MathUtils.floor(getY() + mOffset);
        mDrawable.draw(batch, getX(), origY - tileHeight, getWidth(), tileHeight);
        mDrawable.draw(batch, getX(), origY, getWidth(), getHeight() - mOffset);
    }
}
