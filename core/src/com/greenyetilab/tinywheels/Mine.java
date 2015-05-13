package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * A mine on the road
 */
public class Mine extends GameObjectAdapter implements Collidable, Pool.Poolable, Disposable {
    private static final ReflectionPool<Mine> sPool = new ReflectionPool<Mine>(Mine.class);

    private static final float MINE_RADIUS = 0.8f;

    private GameWorld mGameWorld;
    private Assets mAssets;
    private Racer mOwner;
    private BodyDef mBodyDef;
    private WeldJointDef mJointDef = new WeldJointDef();
    private CircleShape mShape;

    private Body mBody;
    private float mTime;
    private Joint mJoint;

    private static final Vector2 sTmp = new Vector2();
    public static Mine create(GameWorld gameWorld, Assets assets, Racer owner) {
        Mine mine = sPool.obtain();
        if (mine.mBodyDef == null) {
            mine.firstInit(assets);
        }

        mine.mGameWorld = gameWorld;
        mine.mOwner = owner;
        mine.mTime = 0;
        mine.setFinished(false);

        Vehicle vehicle = owner.getVehicle();
        sTmp.set(-vehicle.getHeight() * 2, 0);
        sTmp.rotate(vehicle.getAngle()).add(vehicle.getX(), vehicle.getY());
        mine.mBodyDef.position.set(sTmp.x, sTmp.y);

        mine.mBody = gameWorld.getBox2DWorld().createBody(mine.mBodyDef);
        mine.mBody.createFixture(mine.mShape, 0.00001f);
        mine.mBody.setUserData(mine);
        mine.mBody.setType(BodyDef.BodyType.DynamicBody);

        Box2DUtils.setCollisionInfo(mine.mBody, CollisionCategories.FLAT_OBJECT,
                CollisionCategories.WALL | CollisionCategories.RACER
                | CollisionCategories.FLAT_OBJECT);

        gameWorld.addGameObject(mine);

        mine.initJoint();
        return mine;
    }

    private void firstInit(Assets assets) {
        mAssets = assets;
        mBodyDef = new BodyDef();
        mBodyDef.type = BodyDef.BodyType.DynamicBody;

        mShape = new CircleShape();
        mShape.setRadius(MINE_RADIUS);
    }

    private void initJoint() {
        Body vehicleBody = mOwner.getVehicle().getBody();
        mJointDef.bodyA = mOwner.getVehicle().getBody();
        mJointDef.bodyB = mBody;
        mJointDef.localAnchorA.set(vehicleBody.getLocalCenter().add(0, -mOwner.getVehicle().getHeight()));
        mJointDef.localAnchorB.set(mBody.getLocalCenter());
        mJoint = mGameWorld.getBox2DWorld().createJoint(mJointDef);
    }

    @Override
    public void reset() {
        mGameWorld.getBox2DWorld().destroyBody(mBody);
        mBody = null;
    }

    @Override
    public void dispose() {
        sPool.free(this);
    }

    @Override
    public void act(float delta) {
        mTime += delta;
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_GROUND) {
            // Bigger shadow if the mine has not been dropped
            float shadowPercent = mJoint == null ? 0.5f : 1f;
            TextureRegion region = mAssets.mine.getKeyFrame(mTime);
            DrawUtils.drawBodyRegionShadow(batch, mBody, region, shadowPercent);
        }
        if (zIndex == Constants.Z_VEHICLES) {
            TextureRegion region = mAssets.mine.getKeyFrame(mTime);
            DrawUtils.drawBodyRegion(batch, mBody, region);
        }
    }

    @Override
    public float getX() {
        return mBody.getPosition().x;
    }

    @Override
    public float getY() {
        return mBody.getPosition().y;
    }

    @Override
    public HealthComponent getHealthComponent() {
        return null;
    }

    private void explode() {
        if (mJoint != null) {
            mOwner.resetBonus();
        }
        setFinished(true);
        Vector2 pos = mBody.getPosition();
        mGameWorld.addGameObject(AnimationObject.create(mAssets.explosion, pos.x, pos.y));
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (!(other instanceof Racer)) {
            return;
        }
        if (mJoint != null && other == mOwner) {
            return;
        }
        explode();
        ((Racer)other).spin();
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {

    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {

    }

    public void drop() {
        mGameWorld.getBox2DWorld().destroyJoint(mJoint);
        mJoint = null;
        mBody.setType(BodyDef.BodyType.StaticBody);
    }
}
