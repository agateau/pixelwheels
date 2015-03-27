package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * An AI-controlled car
 */
public class CivilianCar implements GameObject, DisposableWhenOutOfSight {
    private final PendingVehicle mVehicle;
    private final VehicleRenderer mVehicleRenderer;
    private final HealthComponent mHealthComponent;
    private final GroundCollisionHandlerComponent mGroundCollisionHandlerComponent;
    private final Pilot mPilot;

    public CivilianCar(final Assets assets, final TextureRegion region, final GameWorld gameWorld, float originX, float originY, float angle) {
        mVehicle = new PendingVehicle(region, gameWorld, originX, originY);
        mVehicle.setUserData(this);
        mHealthComponent = new HealthComponent() {
            @Override
            protected void onFullyDead() {
                final float U = Constants.UNIT_FOR_PIXEL;
                AnimationObject.createMulti(gameWorld, assets.iceExplosion,
                        mVehicle.getX(), mVehicle.getY(),
                        U * region.getRegionWidth(), U * region.getRegionHeight());
            }

        };
        mVehicleRenderer = new VehicleRenderer(mVehicle, mHealthComponent);
        mGroundCollisionHandlerComponent = new GroundCollisionHandlerComponent(mVehicle, mHealthComponent);

        mPilot = new BasicPilot(gameWorld.getMapInfo(), mVehicle, mHealthComponent);
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
        mVehicle.setCollisionInfo(CollisionCategories.AI_VEHICLE,
                CollisionCategories.WALL
                | CollisionCategories.RACER | CollisionCategories.RACER_BULLET
                | CollisionCategories.AI_VEHICLE | CollisionCategories.FLAT_AI_VEHICLE
                | CollisionCategories.GIFT);
    }

    @Override
    public void dispose() {
        mVehicle.dispose();
    }

    @Override
    public boolean act(float delta) {
        boolean keep = mVehicle.act(delta);
        if (keep) {
            keep = mPilot.act(delta);
        }
        if (keep) {
            keep = mGroundCollisionHandlerComponent.act(delta);
        }
        if (keep) {
            keep = mHealthComponent.act(delta);
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
        return mHealthComponent;
    }
}
