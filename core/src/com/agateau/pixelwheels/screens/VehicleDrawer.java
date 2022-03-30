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

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.agateau.pixelwheels.utils.DrawUtils;
import com.agateau.pixelwheels.vehicledef.AxleDef;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** Helper class to draw a vehicle in the UI. This is *not* used to draw a vehicle in the game */
class VehicleDrawer {
    private final Assets mAssets;
    public VehicleDef vehicleDef;
    public final Vector2 center = new Vector2(0, 0);
    private final Rectangle mRectangle = new Rectangle();

    public float scale = 1;
    public float angle = 0;

    VehicleDrawer(Assets assets) {
        mAssets = assets;
    }

    private final Vector2 sWheelPos = new Vector2();

    /** Get bounding rectangle, centered on this.center */
    public Rectangle getRectangle() {
        TextureRegion region = vehicleDef.getImage(mAssets);

        float halfHeight = region.getRegionWidth() / 2f;
        float halfWidth = region.getRegionHeight() / 2f;

        float axleYOrigin = -halfHeight;
        float bottom = -halfHeight;
        float top = halfHeight;

        for (AxleDef axle : vehicleDef.axles) {
            float axleY = axleYOrigin + axle.y;
            TextureRegion wheelRegion = axle.getTexture(mAssets);
            float thickness = wheelRegion.getRegionHeight();
            float diameter = wheelRegion.getRegionWidth();

            float wheelRight = axle.width / 2 + thickness / 2;
            float wheelBottom = axleY - diameter / 2;
            float wheelTop = axleY + diameter / 2;

            halfWidth = Math.max(halfWidth, wheelRight);
            bottom = Math.min(bottom, wheelBottom);
            top = Math.max(top, wheelTop);
        }

        mRectangle.width = halfWidth * 2;
        mRectangle.height = top - bottom;
        mRectangle.x = center.x - halfWidth;
        mRectangle.y = center.y + bottom;
        return mRectangle;
    }

    public void draw(Batch batch) {
        TextureRegion region = vehicleDef.getImage(mAssets);
        float axleXOrigin = -region.getRegionWidth() / 2f;

        drawShadow(batch, region);

        for (AxleDef axle : vehicleDef.axles) {
            // axleX is based on axle.y because the vehicle texture faces right, but the axle
            // definition faces top
            // See VehicleCreator for more details
            float axleX = axleXOrigin + axle.y;
            TextureRegion wheelRegion = axle.getTexture(mAssets);
            drawWheel(batch, wheelRegion, axleX, -axle.width / 2);
            drawWheel(batch, wheelRegion, axleX, axle.width / 2);
        }

        DrawUtils.drawCentered(batch, region, center, scale, angle);
    }

    private void drawWheel(Batch batch, TextureRegion region, float wx, float wy) {
        sWheelPos.set(wx, wy).scl(scale).rotate(angle).add(center);
        DrawUtils.drawCentered(batch, region, sWheelPos, scale, angle);
    }

    private void drawShadow(Batch batch, TextureRegion region) {
        float old = batch.getPackedColor();
        float offset = BodyRegionDrawer.SHADOW_OFFSET_PX;
        batch.setColor(0, 0, 0, BodyRegionDrawer.SHADOW_ALPHA);
        DrawUtils.drawCentered(batch, region, center.x + offset, center.y - offset, scale, angle);
        batch.setPackedColor(old);
    }
}
