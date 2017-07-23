package com.agateau.tinywheels;

/**
 * A plain struct which can be updated by a GameInputHandler
 */
public class GameInput {
    boolean braking = false;
    boolean accelerating = false;
    boolean triggeringBonus = false;
    float direction = 0;
}
