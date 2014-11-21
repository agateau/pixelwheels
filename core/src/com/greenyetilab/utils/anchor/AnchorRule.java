package com.greenyetilab.utils.anchor;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface AnchorRule {
    public Actor getTarget();
    public void apply();
}
