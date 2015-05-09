package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Things which can be rendered
 */
public interface Renderer {
    public void draw(Batch batch, int zIndex);
}
