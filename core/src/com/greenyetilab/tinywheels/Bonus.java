package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A bonus. When the bonus is done, it must call Racer.resetBonus().
 */
public interface Bonus {
    TextureRegion getIconRegion();

    /**
     * Called when a Racer picked the bonus. Should store the racer parameter for future use, such
     * as when aiAct() is called.
     */
    void onPicked(Racer racer);

    /**
     * Called when a Racer is hit while carrying a bonus.
     */
    void onDropped();

    void trigger();

    /**
     * Called by the racer as long as it owns the bonus.
     */
    void act(float delta);

    /**
     * Implements behavior of the AI when it owns this bonus
     */
    void aiAct(float delta);
}
