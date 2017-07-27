package com.agateau.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A MenuItem in a Menu
 */
public interface MenuItem {
    Actor getActor();

    void trigger();

    /**
     * Called when the user presses the Up virtual key
     * @return true if handled
     */
    boolean goUp();
    /**
     * Called when the user presses the Down virtual key
     * @return true if handled
     */
    boolean goDown();
    void goLeft();
    void goRight();

    Rectangle getFocusRectangle();
}
