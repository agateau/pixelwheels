package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * A generic game object
 */
public interface GameObject {
    void act(float delta);

    /**
     * Returns true if the object is done and should be removed from the game.
     * If the object implements Disposable, GameWorld will take care of calling dispose() on it.
     */
    boolean isFinished();
    void draw(Batch batch, int zIndex);
    float getX();
    float getY();
    HealthComponent getHealthComponent();
}
