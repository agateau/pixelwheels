package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A vehicle which is asleep until it becomes visible
 */
public class PendingVehicle extends Vehicle implements DisposableWhenOutOfSight {
    private static final float ACTIVE_EXTRA_HEIGHT = Constants.VIEWPORT_WIDTH / 2;

    public PendingVehicle(TextureRegion region, GameWorld world, float originX, float originY) {
        super(region, world, originX, originY);
        mBody.setAwake(false);
    }

    @Override
    public boolean act(float dt) {
        float bottomY = mGameWorld.getBottomVisibleY() - ACTIVE_EXTRA_HEIGHT;
        float topY = mGameWorld.getTopVisibleY() + ACTIVE_EXTRA_HEIGHT;
        boolean isActive = bottomY <= getY() && getY() <= topY;
        if (isActive) {
            mBody.setAwake(true);
        } else {
            mBody.setAwake(false);
        }
        return super.act(dt);
    }
}
