package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Disposable;

/**
 * The player character
 */
public class PlayerVehicle implements GameObject, Collidable, Disposable {
    private final GameWorld mGameWorld;
    private final Vehicle mVehicle;
    private final VehicleRenderer mVehicleRenderer;
    private final HealthComponent mHealthComponent = new HealthComponent();
    private final CollisionHandlerComponent mCollisionHandlerComponent;
    private final Pilot mPilot;

    public PlayerVehicle(Assets assets, GameWorld gameWorld, float originX, float originY) {
        mGameWorld = gameWorld;
        mHealthComponent.setInitialHealth(Constants.PLAYER_HEALTH);

        // Car
        TextureRegion carRegion = assets.findRegion("car/player");
        TextureRegion wheelRegion = assets.wheel;
        mVehicle = new Vehicle(carRegion, mGameWorld, originX, originY);
        mVehicle.setUserData(this);
        //mVehicle.setLimitAngle(true);
        //mVehicle.setCorrectAngle(true);

        mVehicleRenderer = new VehicleRenderer(mVehicle, mHealthComponent);
        mCollisionHandlerComponent = new CollisionHandlerComponent(mVehicle, mHealthComponent);
        mPilot = new PlayerPilot(assets, gameWorld, this, mVehicle, mHealthComponent);

        // Wheels
        final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
        final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 42f;

        float wheelW = Constants.UNIT_FOR_PIXEL * wheelRegion.getRegionWidth();
        float rightX = Constants.UNIT_FOR_PIXEL * carRegion.getRegionWidth() / 2 - wheelW / 2 + 0.05f;
        float leftX = -rightX;
        float rearY = Constants.UNIT_FOR_PIXEL * -carRegion.getRegionHeight() / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE + 0.2f;

        Vehicle.WheelInfo info;
        info = mVehicle.addWheel(wheelRegion, leftX, frontY);
        info.steeringFactor = 1;
        info.wheel.setCanDrift(true);
        info = mVehicle.addWheel(wheelRegion, rightX, frontY);
        info.steeringFactor = 1;
        info.wheel.setCanDrift(true);
        info = mVehicle.addWheel(wheelRegion, leftX, rearY);
        info.wheel.setCanDrift(true);
        info = mVehicle.addWheel(wheelRegion, rightX, rearY);
        info.wheel.setCanDrift(true);

        mVehicle.setCollisionInfo(CollisionCategories.PLAYER,
                CollisionCategories.WALL
                | CollisionCategories.AI_VEHICLE | CollisionCategories.FLAT_AI_VEHICLE
                | CollisionCategories.GIFT);
    }

    public Vehicle getVehicle() {
        return mVehicle;
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
        boolean keep = mVehicle.act(delta);
        if (keep) {
            keep = mPilot.act(delta);
        }
        if (keep) {
            keep = mCollisionHandlerComponent.act(delta);
        }
        if (keep) {
            keep = mHealthComponent.act(delta);
        }
        if (!keep) {
            dispose();
            mGameWorld.setState(GameWorld.State.BROKEN);
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
