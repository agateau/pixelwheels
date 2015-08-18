package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.CircularArray;

/**
 * Renders a vehicle
 */
public class VehicleRenderer implements Renderer {
    private final static float SKIDMARK_WIDTH = 10 * Constants.UNIT_FOR_PIXEL;
    private final Assets mAssets;
    private final Vehicle mVehicle;
    private final Array<Renderer> mRenderers = new Array<Renderer>();

    public VehicleRenderer(Assets assets, Vehicle vehicle) {
        mAssets = assets;
        mVehicle = vehicle;
    }

    public void addRenderer(Renderer renderer) {
        mRenderers.add(renderer);
    }

    public void removeRenderer(Renderer renderer) {
        mRenderers.removeValue(renderer, true);
    }

    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_GROUND) {
            for(Vehicle.WheelInfo info: mVehicle.getWheelInfos()) {
                drawSkidmarks(batch, info.wheel.getSkidmarks());
            }
            DrawUtils.drawBodyRegionShadow(batch, mVehicle.getBody(), mVehicle.getRegion());
            return;
        }
        if (zIndex != Constants.Z_VEHICLES) {
            return;
        }

        for(Vehicle.WheelInfo info: mVehicle.getWheelInfos()) {
            DrawUtils.drawBodyRegion(batch, info.wheel.getBody(), info.wheel.getRegion());
        }
        DrawUtils.drawBodyRegion(batch, mVehicle.getBody(), mVehicle.getRegion());

        if (mVehicle.getTurboTime() >= 0) {
            drawTurbo(batch);
        }

        for (Renderer renderer : mRenderers) {
            renderer.draw(batch, zIndex);
        }
    }

    private void drawSkidmarks(Batch batch, CircularArray<Vector2> skidmarks) {
        int idx1 = skidmarks.getBeginIndex();
        if (idx1 == skidmarks.getEndIndex()) {
            return;
        }
        int idx2 = skidmarks.getNextIndex(idx1);
        for (; idx2 != skidmarks.getEndIndex(); idx1 = idx2, idx2 = skidmarks.getNextIndex(idx2)) {
            Vector2 pos1 = skidmarks.get(idx1);
            Vector2 pos2 = skidmarks.get(idx2);
            if (!pos1.equals(Wheel.END_DRIFT_POS) && !pos2.equals(Wheel.END_DRIFT_POS)) {
                drawSkidmark(batch, pos1, pos2);
            }
        }
    }

    private float[] mVertices = new float[4 * 5];
    private Vector2 mThick = new Vector2();
    private void drawSkidmark(Batch batch, Vector2 pos1, Vector2 pos2) {
        mThick.set(pos2).sub(pos1).nor();
        mThick.set(-mThick.y, mThick.x).scl(SKIDMARK_WIDTH / 2);
        TextureRegion region = mAssets.skidmark;
        float c = Color.WHITE.toFloatBits();
        float c2 = Color.RED.toFloatBits();
        float u = region.getU();
        float v = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        /*
            1            2
             x----------x        ^
             |          |        |
        pos1 x          x pos2   | mThick
             |          |
             x----------x
            4            3
         */
        float x1 = pos1.x + mThick.x;
        float y1 = pos1.y + mThick.y;

        float x2 = pos2.x + mThick.x;
        float y2 = pos2.y + mThick.y;

        float x3 = pos2.x - mThick.x;
        float y3 = pos2.y - mThick.y;

        float x4 = pos1.x - mThick.x;
        float y4 = pos1.y - mThick.y;

        initVertex(0, x1, y1, c, u, v);
        initVertex(1, x4, y4, c2, u2, v);
        initVertex(2, x3, y3, c2, u2, v2);
        initVertex(3, x2, y2, c, u, v2);

        batch.draw(region.getTexture(), mVertices, 0, mVertices.length);
    }

    private void initVertex(int index, float x, float y, float c, float u, float v) {
        int idx = index * 5;
        mVertices[idx++] = x;
        mVertices[idx++] = y;
        mVertices[idx++] = c;
        mVertices[idx++] = u;
        mVertices[idx  ] = v;
    }

    private void drawTurbo(Batch batch) {
        TextureRegion region = mAssets.turboFlame.getKeyFrame(mVehicle.getTurboTime(), true);
        Body body = mVehicle.getBody();
        Vector2 center = body.getPosition();
        float angle = body.getAngle() * MathUtils.radiansToDegrees;
        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        float refH = mVehicle.getHeight() / 2;
        float x = center.x + refH * MathUtils.cosDeg(angle - 90);
        float y = center.y + refH * MathUtils.sinDeg(angle - 90);
        batch.draw(region,
                x - w / 2, y - h, // pos
                w / 2, h, // origin
                w, h, // size
                1, 1, // scale
                angle);
    }
}
