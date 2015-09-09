package com.greenyetilab.tinywheels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Make a vehicle spin on itself for a full circle
 */
public class SpinningComponent {
    private final Vehicle mVehicle;
    private boolean mActive = false;
    private float mDesiredAngle;
    private float mStartAngle;

    public SpinningComponent(Vehicle vehicle) {
        mVehicle = vehicle;
    }

    public boolean isActive() {
        return mActive;
    }

    public void start() {
        mActive = true;
        setGripEnabled(false);
        mStartAngle = mVehicle.getBody().getAngle();
        mDesiredAngle = mStartAngle + 2 * MathUtils.PI;
    }

    public void act(float delta) {
        if (!mActive) {
            return;
        }
        Body body = mVehicle.getBody();

        // Slow down
        body.applyLinearImpulse(body.getLinearVelocity().nor().scl(-body.getMass()), body.getWorldCenter(), true);

        // Spin
        float nextAngle = body.getAngle() + body.getAngularVelocity() * GameWorld.BOX2D_TIME_STEP;
        float totalRotation = mDesiredAngle - nextAngle;
        float desiredAngularVelocity = MathUtils.clamp(totalRotation / GameWorld.BOX2D_TIME_STEP, -15, 15);
        float impulse = body.getInertia() * (desiredAngularVelocity - body.getAngularVelocity());
        body.applyAngularImpulse(impulse, true);
        if (Math.abs(totalRotation) < 1 * MathUtils.degRad) {
            stopSpinning();
        }
    }

    public void onBeginContact() {
        // If we hit something, stop spinning: we may not be able to do a full circle at all if we
        // are blocked by a wall
        if (mActive) {
            stopSpinning();
        }
    }

    private void stopSpinning() {
        mActive = false;
        setGripEnabled(true);
    }

    private void setGripEnabled(boolean enabled) {
        for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
            info.wheel.setGripEnabled(enabled);
        }
    }
}
