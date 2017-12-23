package com.agateau.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Pools;

/**
 * Event fired when a MenuItem is triggered
 */
public class MenuItemTriggerEvent extends Event {
    /**
     * Helper method to fire a MenuItemTriggerEvent on an actor
     */
    public static void fire(Actor actor) {
        MenuItemTriggerEvent event = Pools.obtain(MenuItemTriggerEvent.class);
        actor.fire(event);
        Pools.free(event);
    }
}
