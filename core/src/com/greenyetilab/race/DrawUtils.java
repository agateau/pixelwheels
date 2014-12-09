package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

    public static void drawBodySprite(Batch batch, Body body, Sprite sprite) {
        Vector2 center = body.getPosition();
        float x = center.x;
        float y = center.y;
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        sprite.draw(batch);
    }

    public static void drawBodySpriteShadow(Batch batch, Body body, Sprite sprite) {
        Vector2 center = body.getPosition();
        float x = center.x + SHADOW_OFFSET_X;
        float y = center.y + SHADOW_OFFSET_Y;
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        Color old = sprite.getColor();
        sprite.setColor(0, 0, 0, SHADOW_ALPHA);
        sprite.draw(batch);
        sprite.setColor(old);
    }
}
