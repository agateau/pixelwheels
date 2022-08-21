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
import com.agateau.pixelwheels.gameobjet.CellFrameBufferManager;
import com.agateau.pixelwheels.gameobjet.CellFrameBufferUser;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

/** Renders a vehicle */
public class VehicleRenderer implements CellFrameBufferUser {
    private static final int CELL_SIZE = 200;

    private final Assets mAssets;
    private final Vehicle mVehicle;
    private final Array<Renderer> mRenderers = new Array<>();
    private final SkidmarksRenderer mSkidmarksRenderer;
    private float mTime = 0;
    private final BodyRegionDrawer mBodyRegionDrawer = new BodyRegionDrawer();
    private CellFrameBufferManager mCellFrameBufferManager;
    private int mCellId = -1;

    public VehicleRenderer(Assets assets, Vehicle vehicle) {
        mAssets = assets;
        mVehicle = vehicle;
        mSkidmarksRenderer = new SkidmarksRenderer(mAssets);
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
    public void init(CellFrameBufferManager manager) {
        mCellFrameBufferManager = manager;
        mCellId = manager.reserveCell(CELL_SIZE, CELL_SIZE);
    }

    private void drawBodyToCell(Batch batch, Body body, TextureRegion region) {
        float angle = body.getAngle() * MathUtils.radiansToDegrees;
        float xOffset =
                (body.getPosition().x - mVehicle.getPosition().x) / Constants.UNIT_FOR_PIXEL;
        float yOffset =
                (body.getPosition().y - mVehicle.getPosition().y) / Constants.UNIT_FOR_PIXEL;
        float w = region.getRegionWidth();
        float h = region.getRegionHeight();
        float x = mCellFrameBufferManager.getCellCenterX(mCellId) + xOffset;
        float y = mCellFrameBufferManager.getCellCenterY(mCellId) + yOffset;
        batch.draw(
                region,
                // dst
                x - w / 2,
                y - h / 2,
                // origin
                w / 2,
                h / 2,
                // size
                w,
                h,
                // scale
                1,
                1,
                // angle
                angle);
    }

    @Override
    public void drawToCell(Batch batch, Rectangle viewBounds) {
        mTime += Gdx.app.getGraphics().getDeltaTime();

        // Wheels and body
        for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
            drawBodyToCell(batch, info.wheel.getBody(), info.wheel.getRegion());
        }

        TextureRegion region = mVehicle.getRegion(mTime);
        drawBodyToCell(batch, mVehicle.getBody(), region);

        float centerX = mCellFrameBufferManager.getCellCenterX(mCellId);
        float centerY = mCellFrameBufferManager.getCellCenterY(mCellId);
        for (Renderer renderer : mRenderers) {
            renderer.draw(batch, centerX, centerY);
        }
    }

    public void draw(Batch batch, ZLevel zLevel, Rectangle viewBounds) {
        mBodyRegionDrawer.setBatch(batch);
        float scale = mVehicle.getZ() + 1;

        // Ground: skidmarks, splash, shadow
        if (zLevel == ZLevel.GROUND) {
            for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
                mSkidmarksRenderer.draw(batch, info.wheel.getSkidmarks(), viewBounds);
            }

            // Only draw splash and shadow if we are not falling
            if (!mVehicle.isFalling()) {
                for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
                    if (info.wheel.getMaterial().isWater()) {
                        Animation<TextureRegion> splashAnimation = info.wheel.getSplashAnimation();
                        mBodyRegionDrawer.draw(
                                info.wheel.getBody(), splashAnimation.getKeyFrame(mTime, true));
                    }
                }

                // Shadows
                float offset = BodyRegionDrawer.computeShadowOffset(mVehicle.getZ(), 1);
                float old = batch.getPackedColor();
                batch.setColor(0, 0, 0, BodyRegionDrawer.SHADOW_ALPHA);
                mCellFrameBufferManager.drawCell(
                        batch, mVehicle.getX() + offset, mVehicle.getY() - offset, mCellId);
                batch.setPackedColor(old);
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
        }
        mCellFrameBufferManager.drawScaledCell(batch, mVehicle.getPosition(), mCellId, scale);
        if (mVehicle.isFalling()) {
            batch.setColor(Color.WHITE);
        }

        // Turbo (not in cell because it makes no sense for it to have a shadow)
        if (mVehicle.getTurboTime() >= 0) {
            drawTurbo(batch);
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
                // pos
                x - w / 2,
                y - h,
                // origin
                w / 2,
                h,
                // size
                w,
                h,
                // scale
                1,
                1,
                // angle
                angle - 90);
    }
}
