package com.agateau.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

/**
 * Renders a vehicle
 */
public class VehicleRenderer implements Renderer {
    private static final Color IMMERSED_COLOR = new Color(0, 0.5f, 1, 0.2f);
    private final Assets mAssets;
    private final Vehicle mVehicle;
    private final Array<Renderer> mRenderers = new Array<Renderer>();
    private final SkidmarksRenderer mSkidmarksRenderer;
    private float mTime = 0;
    private final BodyRegionDrawer mBodyRegionDrawer = new BodyRegionDrawer();

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

    @Override
    public void draw(Batch batch, int zIndex) {
        mBodyRegionDrawer.setBatch(batch);
        mBodyRegionDrawer.setScale(mVehicle.getZ() + 1);
        mTime += Gdx.app.getGraphics().getDeltaTime();
        if (zIndex == Constants.Z_GROUND) {
            for(Vehicle.WheelInfo info: mVehicle.getWheelInfos()) {
                mSkidmarksRenderer.draw(batch, info.wheel.getSkidmarks());
            }

            // Only draw splash and shadow if we are not falling
            if (!mVehicle.isFalling()) {
                for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
                    if (info.wheel.getMaterial().isWater()) {
                        mBodyRegionDrawer.draw(info.wheel.getBody(), mAssets.splash.getKeyFrame(mTime, true));
                    }
                }
                mBodyRegionDrawer.drawShadow(mVehicle.getBody(), mVehicle.getRegion());
            }
            return;
        }

        int wantedZIndex = mVehicle.isFlying() ? Constants.Z_FLYING : Constants.Z_VEHICLES;
        if (zIndex != wantedZIndex) {
            return;
        }

        if (mVehicle.isFalling()) {
            float k = MathUtils.clamp(1 + mVehicle.getZ() * 10, 0, 1);
            // k = 0 fully immersed, k = 1 not immersed
            mBatchColor.set(IMMERSED_COLOR);
            mBatchColor.lerp(Color.WHITE, k);
            batch.setColor(mBatchColor);
        } else {
            // Do not draw the wheels when falling: when the body is painted with alpha < 1 the wheels are visible
            // through it and it looks ugly
            for(Vehicle.WheelInfo info: mVehicle.getWheelInfos()) {
                mBodyRegionDrawer.draw(info.wheel.getBody(), info.wheel.getRegion());
            }
        }
        mBodyRegionDrawer.draw(mVehicle.getBody(), mVehicle.getRegion());

        if (mVehicle.getTurboTime() >= 0) {
            drawTurbo(batch);
        }

        for (Renderer renderer : mRenderers) {
            renderer.draw(batch, zIndex);
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
        batch.draw(region,
                x - w / 2, y - h, // pos
                w / 2, h, // origin
                w, h, // size
                1, 1, // scale
                angle - 90);
    }
}
