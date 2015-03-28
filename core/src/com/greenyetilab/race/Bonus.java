package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A bonus
 */
public interface Bonus {
    TextureRegion getIconRegion();

    void onPicked(Racer racer);
    void trigger();
}
