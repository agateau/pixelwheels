package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

/**
 * An enemy car
 */
public class EnemyCar extends Vehicle implements DisposableWhenOutOfSight {
    private static final float ACTIVE_EXTRA_HEIGHT = Constants.VIEWPORT_WIDTH / 2;

    public EnemyCar(TextureRegion region, GameWorld world, float originX, float originY) {
        super(region, world, originX, originY);
        mBody.setAwake(false);
    }

    public void setDrivingAngle(float angle) {
        angle = (angle - 90) * MathUtils.degreesToRadians;
        mBody.setTransform(mBody.getPosition(), angle);
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
