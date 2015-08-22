package com.greenyetilab.tinywheels;

import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Responsible for updating a GameInput according to player (or anything else) inputs
 */
public interface GameInputHandler {
    GameInput getGameInput();
    void createHud(Assets assets, AnchorGroup root);

    void setBonus(Bonus bonus);
}
