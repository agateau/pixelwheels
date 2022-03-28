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
package com.agateau.pixelwheels.racer;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.Renderer;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

/** Renders a vehicle */
public class VehicleRenderer implements Renderer {
    private final Assets mAssets;
    private final Vehicle mVehicle;
    private final Array<Renderer> mRenderers = new Array<>();
    private final SkidmarksRenderer mSkidmarksRenderer;
    private float mTime = 0;
    private final BodyRegionDrawer mBodyRegionDrawer = new BodyRegionDrawer();

    public VehicleRenderer(Assets assets, Vehicle vehicle) {
        mAssets = assets;
        mVehicle = vehicle;
        mSkidmarksRenderer = new SkidmarksRenderer(mAssets);
        // Draw fully opaque shadows: vehicle shadows are drawn to a FrameBuffer. The
        // BodyRegionDrawer.SHADOW_ALPHA is applied when drawing the buffer to the screen.
        mBodyRegionDrawer.setShadowAlpha(1);
    }

    public void addRenderer(Renderer renderer) {
        mRenderers.add(renderer);
    }

    public void removeRenderer(Renderer renderer) {
        mRenderers.removeValue(renderer, true);
    }

    private final Color mBatchColor = new Color();

    public Color getBatchColor() {
        if (mVehicle.isFalling()) {
            float k = MathUtils.clamp(1 + mVehicle.getZ() * 10, 0, 1);
            // k = 0 fully immersed, k = 1 not immersed
            mBatchColor.set(Constants.FULLY_IMMERSED_COLOR);
            mBatchColor.lerp(Color.WHITE, k);
        } else {
            mBatchColor.set(Color.WHITE);
        }
        return mBatchColor;
    }

    @Override
    public void draw(Batch batch, ZLevel zLevel, Rectangle viewBounds) {
        mBodyRegionDrawer.setBatch(batch);
        mBodyRegionDrawer.setScale(mVehicle.getZ() + 1);
        mTime += Gdx.app.getGraphics().getDeltaTime();
        TextureRegion bodyRegion = mVehicle.getRegion(mTime);

        // Ground: skidmarks, splash
        if (zLevel == ZLevel.GROUND) {
            for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
                mSkidmarksRenderer.draw(batch, info.wheel.getSkidmarks(), viewBounds);
            }

            // Only draw splash if we are not falling
            if (!mVehicle.isFalling()) {
                for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
                    if (info.wheel.getMaterial().isWater()) {
                        mBodyRegionDrawer.draw(
                                info.wheel.getBody(), mAssets.splash.getKeyFrame(mTime, true));
                    }
                }
            }
            return;
        }

        // Shadows
        if (zLevel == ZLevel.VEHICLE_SHADOWS) {
            if (!mVehicle.isFalling()) {
                for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
                    mBodyRegionDrawer.drawShadow(info.wheel.getBody(), info.wheel.getRegion());
                }
                mBodyRegionDrawer.drawShadow(mVehicle.getBody(), bodyRegion);
            }
            return;
        }

        // Vehicle level: wheels and body
        ZLevel wantedZIndex = mVehicle.isFlying() ? ZLevel.FLYING : ZLevel.VEHICLES;
        if (zLevel != wantedZIndex) {
            return;
        }

        if (mVehicle.isFalling()) {
            batch.setColor(getBatchColor());
        } else {
            // Draw wheels. Do not draw them when falling: when the body is painted with alpha < 1
            // the wheels are visible through it and it looks ugly.
            for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
                mBodyRegionDrawer.draw(info.wheel.getBody(), info.wheel.getRegion());
            }
        }
        mBodyRegionDrawer.draw(mVehicle.getBody(), bodyRegion);

        if (mVehicle.getTurboTime() >= 0) {
            drawTurbo(batch);
        }

        for (Renderer renderer : mRenderers) {
            renderer.draw(batch, zLevel, viewBounds);
        }

        if (mVehicle.isFalling()) {
            batch.setColor(Color.WHITE);
        }
    }

    private void drawTurbo(Batch batch) {
        TextureRegion region = mAssets.turboFlame.getKeyFrame(mVehicle.getTurboTime(), true);
        Body body = mVehicle.getBody();
        Vector2 center = body.getPosition();
        float angle = body.getAngle() * MathUtils.radiansToDegrees;
        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        float refH = -mVehicle.getWidth() / 2;
        float x = center.x + refH * MathUtils.cosDeg(angle);
        float y = center.y + refH * MathUtils.sinDeg(angle);
        batch.draw(
                region,
                x - w / 2,
                y - h, // pos
                w / 2,
                h, // origin
                w,
                h, // size
                1,
                1, // scale
                angle - 90);
    }
}
