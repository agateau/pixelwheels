package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A bonus
 */
public interface Bonus {
    TextureRegion getIconRegion();

    /**
     * Called when a Racer picked the bonus. Should store the racer parameter for future use, such
     * as when aiAct() is called.
     */
    void onPicked(Racer racer);
    void trigger();

    /**
     * Implements behavior of the AI when it owns this bonus
     */
    void aiAct(float delta);
}
