package com.agateau.tinywheels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Helper class to draw a TextureRegion for a Box2D Body
 */
public class BodyRegionDrawer {
    private static final float SHADOW_ALPHA = 0.35f;
    private static final float SHADOW_OFFSET = Constants.UNIT_FOR_PIXEL * 6;
    private Batch mBatch;
    private float mZOffset = 0;
    private float mScale = 1;

    void setBatch(Batch batch) {
        mBatch = batch;
    }

    /**
     * Defines the default Z value for a body.
     * 0 for a ground object
     * 1 for flying object
     */
    void setZ(float z) {
        mZOffset = z * Constants.UNIT_FOR_PIXEL * 80;
    }

    void setScale(float scale) {
        mScale = scale;
    }

    void draw(Body body, TextureRegion region) {
        Vector2 center = body.getPosition();
        float angle = body.getAngle() * MathUtils.radiansToDegrees;
        float x = center.x;
        float y = center.y;
        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        mBatch.draw(region,
                x - w / 2, y - h / 2, // pos
                w / 2, h / 2, // origin
                w, h, // size
                mScale, mScale,
                angle);
    }

    void drawShadow(Body body, TextureRegion region) {
        Vector2 center = body.getPosition();
        float angle = body.getAngle() * MathUtils.radiansToDegrees;
        float shadowOffset = SHADOW_OFFSET + mZOffset + (mScale - 1) * 30 * Constants.UNIT_FOR_PIXEL;
        float x = center.x + shadowOffset;
        float y = center.y + shadowOffset;
        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        Color old = mBatch.getColor();
        mBatch.setColor(0, 0, 0, SHADOW_ALPHA);
        mBatch.draw(region,
                x - w / 2, y - h / 2, // pos
                w / 2, h / 2, // origin
                w, h, // size
                1, 1, // scale
                angle);
        mBatch.setColor(old);
    }
}
