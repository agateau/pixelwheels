package com.agateau.pixelwheels.racescreen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class StarField {
    private Star[] mStars;

    public StarField(float mapWidth, float mapHeight, float midSize) {
        mStars = new Star[5000];
        for (int i = 0; i < mStars.length; i++) {
            mStars[i] =
                    new Star(
                            MathUtils.random(-mapWidth, mapWidth),
                            MathUtils.random(-mapHeight, mapHeight),
                            MathUtils.random(midSize * 0.5f, midSize * 2),
                            MathUtils.random());
        }
    }

    public void updateAndRender(ShapeRenderer renderer, OrthographicCamera camera, float delta) {
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Star star : mStars) {
            star.update(delta);
            star.draw(renderer, camera.position);
        }
        renderer.end();
    }

    private static class Star {
        float x, y, size;
        float phase;

        Star(float x, float y, float size, float phase) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.phase = phase;
        }

        void update(float delta) {
            phase += delta;
            if (phase > 60) phase -= 60;
        }

        public void draw(ShapeRenderer renderer, Vector3 c) {
            final float brightness = 0.5f + 0.5f * MathUtils.sin(phase * MathUtils.PI);
            renderer.setColor(brightness, brightness, brightness, 1);

            final float x = this.x + c.x;
            final float y = this.y + c.y;

            // Top point
            renderer.triangle(x, y + size, x - size / 3, y, x + size / 3, y);

            // Right point
            renderer.triangle(x + size, y, x, y + size / 3, x, y - size / 3);

            // Bottom point
            renderer.triangle(x, y - size, x - size / 3, y, x + size / 3, y);

            // Left point
            renderer.triangle(x - size, y, x, y + size / 3, x, y - size / 3);
        }
    }
}