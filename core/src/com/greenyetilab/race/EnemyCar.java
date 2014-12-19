package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * A basic enemy car
 */
public class EnemyCar implements GameObject, Collidable, DisposableWhenOutOfSight {
    private final GameWorld mGameWorld;
    private final PendingVehicle mVehicle;
    private final VehicleRenderer mVehicleRenderer;
    private final HealthComponent mHealthComponent = new HealthComponent();
    private final Pilot mPilot;

    public EnemyCar(Assets assets, TextureRegion region, GameWorld gameWorld, float originX, float originY, float angle) {
        mGameWorld = gameWorld;
        mVehicle = new PendingVehicle(region, gameWorld, originX, originY);
        mVehicle.setHealthComponent(mHealthComponent);
        mVehicleRenderer = new VehicleRenderer(mVehicle, mHealthComponent);

        mPilot = new BasicPilot(gameWorld.getMapInfo(), mVehicle);
        mHealthComponent.setInitialHealth(1);

        // Wheels
        TextureRegion wheelRegion = assets.wheel;
        final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
        final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 46f;

        float wheelW = Constants.UNIT_FOR_PIXEL * wheelRegion.getRegionWidth();
        float rightX = mVehicle.getWidth() / 2 - wheelW / 2 + 0.05f;
        float leftX = -rightX;
        float rearY = -mVehicle.getHeight() / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE;

        Vehicle.WheelInfo info;
        info = mVehicle.addWheel(wheelRegion, leftX, frontY);
        info.steeringFactor = 1;
        info = mVehicle.addWheel(wheelRegion, rightX, frontY);
        info.steeringFactor = 1;
        info = mVehicle.addWheel(wheelRegion, leftX, rearY);
        info.wheel.setCanDrift(true);
        info = mVehicle.addWheel(wheelRegion, rightX, rearY);
        info.wheel.setCanDrift(true);

        // Set angle *after* adding the wheels!
        mVehicle.setInitialAngle(angle);
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        mPilot.beginContact(contact, otherFixture);
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {
        mPilot.endContact(contact, otherFixture);
    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {
        mPilot.preSolve(contact, otherFixture, oldManifold);
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {
        mPilot.postSolve(contact, otherFixture, impulse);
    }

    @Override
    public void dispose() {
        mVehicle.dispose();
    }

    @Override
    public boolean act(float delta) {
        boolean keep = true;
        keep = keep && mVehicle.act(delta);
        if (keep) {
            keep = keep && mPilot.act(delta);
        }
        if (!keep) {
            dispose();
        }
        return keep;
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        mVehicleRenderer.draw(batch, zIndex);
    }

    @Override
    public float getX() {
        return mVehicle.getX();
    }

    @Override
    public float getY() {
        return mVehicle.getY();
    }

    @Override
    public HealthComponent getHealthComponent() {
        return mVehicle.getHealthComponent();
    }
}
