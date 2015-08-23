package com.greenyetilab.tinywheels;

/**
 * Responsible for updating a GameInput according to player (or anything else) inputs
 */
public interface GameInputHandler {
    GameInput getGameInput();
    void createHudButtons(Assets assets, Hud hud);

    void setBonus(Bonus bonus);
}
