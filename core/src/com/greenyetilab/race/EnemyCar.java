package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * An enemy car
 */
public class EnemyCar extends Vehicle implements Collidable {
    private boolean mHasCollided = false;

    public EnemyCar(GameWorld world, Assets assets, float originX, float originY) {
        super(selectCarTextureRegion(assets), world, originX, originY);

        // Wheels
        TextureRegion wheelRegion = assets.wheel;
        final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
        final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 46f;

        float wheelW = Constants.UNIT_FOR_PIXEL * wheelRegion.getRegionWidth();
        float rightX = getWidth() / 2 - wheelW / 2 + 0.05f;
        float leftX = -rightX;
        float rearY = -getHeight() / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE;

        Vehicle.WheelInfo info;
        info = addWheel(wheelRegion, leftX, frontY);
        info.steeringFactor = 1;
        info = addWheel(wheelRegion, rightX, frontY);
        info.steeringFactor = 1;
        addWheel(wheelRegion, leftX, rearY);
        addWheel(wheelRegion, rightX, rearY);

        mBody.setAwake(false);
    }

    public void setDrivingAngle(float angle) {
        angle = (angle - 90) * MathUtils.degreesToRadians;
        mBody.setTransform(mBody.getPosition(), angle);
    }

    @Override
    public boolean act(float dt) {
        super.act(dt);
        boolean isVisible = mGameWorld.isVisible(getX(), getY());
        if (isVisible) {
            setAccelerating(!mHasCollided);
        } else {
            mBody.setAwake(false);
        }
        return true;
    }

    private static TextureRegion selectCarTextureRegion(Assets assets) {
        return assets.cars.get(MathUtils.random(assets.cars.size - 1));
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        mHasCollided = true;
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {

    }
}
