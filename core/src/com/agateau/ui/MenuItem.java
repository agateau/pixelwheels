package com.agateau.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A MenuItem in a Menu
 */
public interface MenuItem {
    Actor getActor();
    void trigger();

    Rectangle getFocusRectangle();
}
