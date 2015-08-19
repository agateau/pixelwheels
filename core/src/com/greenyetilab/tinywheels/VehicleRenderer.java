package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.CircularArray;
import com.greenyetilab.utils.GylMathUtils;

/**
 * Renders a vehicle
 */
public class VehicleRenderer implements Renderer {
    private final static float SKIDMARK_WIDTH = 7 * Constants.UNIT_FOR_PIXEL;
    private final static float SKIDMARK_ALPHA_INC = 0.05f;
    private final static float SKIDMARK_ALPHA_MIN = 0.1f;
    private final static float SKIDMARK_ALPHA_MAX = 0.4f;
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
        float alpha = SKIDMARK_ALPHA_MIN;
        for (; idx2 != skidmarks.getEndIndex(); idx1 = idx2, idx2 = skidmarks.getNextIndex(idx2)) {
            Vector2 pos1 = skidmarks.get(idx1);
            Vector2 pos2 = skidmarks.get(idx2);
            if (!pos1.equals(Wheel.END_DRIFT_POS) && !pos2.equals(Wheel.END_DRIFT_POS)) {
                drawSkidmark(batch, pos1, pos2, alpha);
                alpha = Math.min(SKIDMARK_ALPHA_MAX, alpha + SKIDMARK_ALPHA_INC);
            } else {
                alpha = SKIDMARK_ALPHA_MIN;
            }
        }
    }

    private float[] mVertices = new float[4 * 5];
    private void drawSkidmark(Batch batch, Vector2 pos1, Vector2 pos2, float alpha) {
        Vector2 thickness = GylMathUtils.computeWidthVector(pos1, pos2, SKIDMARK_WIDTH / 2);
        TextureRegion region = mAssets.skidmark;
        float c = Color.toFloatBits(1, 1, 1, alpha);
        float c2 = Color.toFloatBits(1, 1, 1, alpha + SKIDMARK_ALPHA_INC);
        float u = region.getU();
        float v = region.getV();
        float u2 = region.getU2();
        float v2 = region.getV2();

        /*
            1            2
             x----------x        ^
             |          |        |
        pos1 x          x pos2   | thickness
             |          |
             x----------x
            4            3
         */
        float x1 = pos1.x + thickness.x;
        float y1 = pos1.y + thickness.y;

        float x2 = pos2.x + thickness.x;
        float y2 = pos2.y + thickness.y;

        float x3 = pos2.x - thickness.x;
        float y3 = pos2.y - thickness.y;

        float x4 = pos1.x - thickness.x;
        float y4 = pos1.y - thickness.y;

        initVertex(0, x1, y1, c, u, v);
        initVertex(1, x4, y4, c, u2, v);
        initVertex(2, x3, y3, c2, u2, v2);
        initVertex(3, x2, y2, c2, u, v2);

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
