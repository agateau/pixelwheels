package com.agateau.tinywheels;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

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
