package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * A generic game object
 */
public interface GameObject {
    boolean act(float delta);
    void draw(Batch batch, int zIndex);
}
