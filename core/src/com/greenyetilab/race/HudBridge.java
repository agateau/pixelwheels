package com.greenyetilab.race;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Let the game world add actors to the hud
 */
public interface HudBridge {
    public Vector2 toHudCoordinate(float x, float y);
    public Stage getStage();
}
