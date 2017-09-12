package com.agateau.tinywheels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Utilities to draw shapes
 */
public class DrawUtils {
    public static void drawCross(ShapeRenderer renderer, Vector2 pos, float radius) {
        drawCross(renderer, pos.x, pos.y, radius);
    }

    public static void drawCross(ShapeRenderer renderer, float x, float y, float radius) {
        renderer.line(x - radius, y, x + radius, y);
        renderer.line(x, y - radius, x, y + radius);
    }
}
