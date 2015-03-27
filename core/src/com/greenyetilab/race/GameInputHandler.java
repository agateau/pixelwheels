package com.greenyetilab.race;

/**
 * Responsible for updating a GameInput according to player (or anything else) inputs
 */
public interface GameInputHandler {
    GameInput getGameInput();
    void createHud(Assets assets, HudBridge hudBridge);
    BonusIndicator getBonusIndicator();
}
