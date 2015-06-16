package com.greenyetilab.tinywheels;

import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Responsible for updating a GameInput according to player (or anything else) inputs
 */
public interface GameInputHandler {
    GameInput getGameInput();
    void createHud(Assets assets, Group root);
    BonusIndicator getBonusIndicator();
}
