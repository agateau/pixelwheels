package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by aurelien on 27/11/14.
 */
public class DrawUtils {
    public static void drawBodySprite(Batch batch, Body body, Sprite sprite) {
        Vector2 center = body.getPosition();
        float x = center.x;
        float y = center.y;
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        sprite.draw(batch);
    }
}
