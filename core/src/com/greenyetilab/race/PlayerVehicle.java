package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by aurelien on 07/12/14.
 */
public class PlayerVehicle extends Vehicle {
    public PlayerVehicle(TextureRegion region, GameWorld gameWorld, Vector2 startPosition) {
        super(region, gameWorld, startPosition);
    }

    @Override
    public boolean act(float dt) {
        super.act(dt);
        int wheelsOnFatalGround = 0;
        for(WheelInfo info: mWheels) {
            Wheel wheel = info.wheel;
            wheel.act(dt);
            if (wheel.isOnFatalGround()) {
                ++wheelsOnFatalGround;
            }
            if (wheel.isOnFinished()) {
                mGameWorld.setState(GameWorld.State.FINISHED);
            }
        }
        if (wheelsOnFatalGround >= 2) {
            mGameWorld.setState(GameWorld.State.BROKEN);
        }
        return true;
    }
}
