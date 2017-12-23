package com.agateau.ui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

/**
 * Listener for a MenuItem
 */
public abstract class MenuItemListener implements EventListener {
    abstract public void triggered();

    public boolean handle(Event e) {
        if (!(e instanceof MenuItemTriggerEvent)) {
            return false;
        }
        e.handle();
        triggered();
        return true;
    }
}
