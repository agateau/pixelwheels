package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by aurelien on 27/11/14.
 */
public class DrawUtils {
    public static final float SHADOW_OFFSET_X = Constants.UNIT_FOR_PIXEL * 5;
    public static final float SHADOW_OFFSET_Y = -SHADOW_OFFSET_X;
    public static final float SHADOW_ALPHA = 0.35f;

    public static void drawBodyRegionShadow(Batch batch, Body body, TextureRegion region) {
        Vector2 center = body.getPosition();
        float angle = body.getAngle() * MathUtils.radiansToDegrees;
        float x = center.x + SHADOW_OFFSET_X;
        float y = center.y + SHADOW_OFFSET_Y;
        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        Color old = batch.getColor();
        batch.setColor(0, 0, 0, SHADOW_ALPHA);
        batch.draw(region,
                x - w / 2, y - h / 2, // pos
                w / 2, h / 2, // origin
                w, h, // size
                1, 1, // scale
                angle);
        batch.setColor(old);
    }

    public static void drawBodyRegion(Batch batch, Body body, TextureRegion region) {
        Vector2 center = body.getPosition();
        float angle = body.getAngle() * MathUtils.radiansToDegrees;
        float x = center.x;
        float y = center.y;
        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        batch.draw(region,
                x - w / 2, y - h / 2, // pos
                w / 2, h / 2, // origin
                w, h, // size
                1, 1, // scale
                angle);
    }
}
