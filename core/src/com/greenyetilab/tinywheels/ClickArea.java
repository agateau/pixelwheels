package com.greenyetilab.tinywheels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * An actor which forwards its clicks to another one
 */
public class ClickArea extends Actor {
    public ClickArea(final Actor target) {

        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return target.fire(event);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                target.fire(event);
            }
        });
    }
}
