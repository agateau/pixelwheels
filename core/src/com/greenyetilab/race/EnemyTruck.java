package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * A truck which drops gifts when destroyed
 */
public class EnemyTruck implements GameObject, Collidable, DisposableWhenOutOfSight {
    private final Assets mAssets;
    private final GameWorld mGameWorld;
    private final PendingVehicle mVehicle;
    private final VehicleRenderer mVehicleRenderer;
    private final HealthComponent mHealthComponent = new HealthComponent() {
        @Override
        protected void onHealthDecreased() {
            Gift.drop(mAssets, mGameWorld, getX(), getY(), MathUtils.random(60f, 120f));
        }
        @Override
        protected void onFullyDead() {
            final float U = Constants.UNIT_FOR_PIXEL;
            TextureRegion region = mVehicle.getRegion();
            AnimationObject.createMulti(mGameWorld, mAssets.iceExplosion,
                    mVehicle.getX(), mVehicle.getY(),
                    U * region.getRegionWidth(), U * region.getRegionHeight());
        }
    };
    private final CollisionHandlerComponent mCollisionHandlerComponent;
    private final Pilot mPilot;

    public EnemyTruck(Assets assets, GameWorld gameWorld, float originX, float originY) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mVehicle = new PendingVehicle(assets.findRegion("truck"), gameWorld, originX, originY);
        mVehicle.setUserData(this);
        mVehicleRenderer = new VehicleRenderer(mVehicle, mHealthComponent);
        mCollisionHandlerComponent = new CollisionHandlerComponent(mVehicle, mHealthComponent);

        mPilot = new BasicPilot(gameWorld.getMapInfo(), mVehicle, mHealthComponent);
        mHealthComponent.setInitialHealth(4);

        // Wheels
        TextureRegion wheelRegion = assets.wheel;
        final float U = Constants.UNIT_FOR_PIXEL;
        final float REAR_WHEEL_Y = U * 19f;
        final float WHEEL_BASE = U * 63f;

        float rightX = U * 19f;
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

        mVehicle.setCollisionInfo(CollisionCategories.ENEMY,
                CollisionCategories.WALL
                | CollisionCategories.PLAYER | CollisionCategories.PLAYER_BULLET
                | CollisionCategories.ENEMY | CollisionCategories.FLAT_ENEMY
                | CollisionCategories.GIFT);
    }

    public void setInitialAngle(float angle) {
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
        if (keep) {
            keep = mCollisionHandlerComponent.act(delta);
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
