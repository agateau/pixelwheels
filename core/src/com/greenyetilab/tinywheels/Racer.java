package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * A racer
 */
public class Racer extends GameObjectAdapter implements Collidable, Disposable {
    private final GameWorld mGameWorld;
    private final Vehicle mVehicle;
    private final VehicleRenderer mVehicleRenderer;
    private final HealthComponent mHealthComponent = new HealthComponent();
    private final GroundCollisionHandlerComponent mGroundCollisionHandlerComponent;

    private final LapPositionComponent mLapPositionComponent;

    private Pilot mPilot;

    // State
    private Bonus mBonus;
    private boolean mMustSelectBonus = false;

    public Racer(Assets assets, GameWorld gameWorld, Vehicle vehicle) {
        mGameWorld = gameWorld;
        mHealthComponent.setInitialHealth(Constants.PLAYER_HEALTH);
        mLapPositionComponent = new LapPositionComponent(gameWorld.getMapInfo(), vehicle);

        mVehicle = vehicle;
        mVehicle.setUserData(this);
        mVehicle.setCollisionInfo(CollisionCategories.RACER,
                CollisionCategories.WALL
                | CollisionCategories.RACER | CollisionCategories.RACER_BULLET
                | CollisionCategories.FLAT_OBJECT);

        mVehicleRenderer = new VehicleRenderer(assets, mVehicle);
        mGroundCollisionHandlerComponent = new GroundCollisionHandlerComponent(mVehicle, mHealthComponent);
    }

    public Pilot getPilot() {
        return mPilot;
    }

    public void setPilot(Pilot pilot) {
        mPilot = pilot;
    }

    public Vehicle getVehicle() {
        return mVehicle;
    }

    public Bonus getBonus() {
        return mBonus;
    }

    public LapPositionComponent getLapPositionComponent() {
        return mLapPositionComponent;
    }

    public void spin() {
        mVehicle.getBody().applyAngularImpulse(GamePlay.instance.spinImpulse, true);
        for (Vehicle.WheelInfo info : mVehicle.getWheelInfos()) {
            info.wheel.disableGripFor(GamePlay.instance.spinDuration / 10f);
        }
        if (mBonus != null) {
            mBonus.onOwnerHit();
        }
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (other instanceof BonusSpot) {
            BonusSpot spot = (BonusSpot)other;
            spot.pickBonus();
            if (mBonus == null) {
                // Do not call selectBonus() from here: it would make it harder for bonus code to
                // create Box2D bodies: since we are in the collision handling code, the physic
                // engine is locked so they would have to delay such creations.
                mMustSelectBonus = true;
            }
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {
    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {
        Object other = otherFixture.getBody().getUserData();
        if (other instanceof Racer) {
            contact.setEnabled(false);
            Racer racer2 = (Racer)other;
            Body body1 = getVehicle().getBody();
            Body body2 = racer2.getVehicle().getBody();
            float x1 = body1.getWorldCenter().x;
            float y1 = body1.getWorldCenter().y;
            float x2 = body2.getWorldCenter().x;
            float y2 = body2.getWorldCenter().y;
            final float k = 4;
            body1.applyLinearImpulse(k * (x1 - x2), k * (y1 - y2), x1, y1, true);
            body2.applyLinearImpulse(k * (x2 - x1), k * (y2 - y1), x2, y2, true);
        }
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {
    }

    @Override
    public void dispose() {
        mVehicle.dispose();
    }

    @Override
    public void act(float delta) {
        if (mMustSelectBonus) {
            mMustSelectBonus = false;
            selectBonus();
        }
        mLapPositionComponent.act(delta);
        mVehicle.act(delta);
        if (mLapPositionComponent.hasFinishedRace()) {
            mVehicle.setAccelerating(false);
        } else {
            mPilot.act(delta);
        }
        mGroundCollisionHandlerComponent.act(delta);
        if (!mHealthComponent.act(delta)) {
            setFinished(true);
        }
        if (mBonus != null) {
            mBonus.act(delta);
        }
    }

    private void selectBonus() {
        Array<BonusPool> pools = mGameWorld.getBonusPools();
        int idx = MathUtils.random(pools.size - 1);
        BonusPool pool = pools.get(idx);
        mBonus = pool.obtain();
        mBonus.onPicked(this);
    }

    public void triggerBonus() {
        if (mBonus == null) {
            return;
        }
        mBonus.trigger();
    }

    /**
     * Called by bonuses when they are done
     */
    public void resetBonus() {
        mBonus = null;
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

    public VehicleRenderer getVehicleRenderer() {
        return mVehicleRenderer;
    }
}
