package com.agateau.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * A player bullet
 */
public class Bullet extends GameObjectAdapter implements Collidable, Pool.Poolable, Disposable {
    private static final ReflectionPool<Bullet> sPool = new ReflectionPool<Bullet>(Bullet.class);

    private static final float BULLET_RADIUS = 0.8f;
    private static final float IMPULSE = 160;

    private Racer mShooter;
    private GameWorld mGameWorld;
    private Assets mAssets;
    private BodyDef mBodyDef;
    private CircleShape mShape;

    private Body mBody;

    private BodyRegionDrawer mDrawer = new BodyRegionDrawer();

    public static Bullet create(Assets assets, GameWorld gameWorld, Racer shooter, float originX, float originY, float angle) {
        Bullet object = sPool.obtain();
        if (object.mBodyDef == null) {
            object.firstInit(assets);
        }
        object.mShooter = shooter;
        object.mGameWorld = gameWorld;
        object.setFinished(false);
        object.mBodyDef.position.set(originX, originY);
        object.mBodyDef.angle = angle * MathUtils.degreesToRadians;

        object.mBody = gameWorld.getBox2DWorld().createBody(object.mBodyDef);
        object.mBody.createFixture(object.mShape, 0f);
        object.mBody.setUserData(object);
        object.mBody.applyLinearImpulse(IMPULSE * MathUtils.cosDeg(angle), IMPULSE * MathUtils.sinDeg(angle), originX, originY, true);

        Box2DUtils.setCollisionInfo(object.mBody, CollisionCategories.RACER_BULLET,
                CollisionCategories.WALL | CollisionCategories.RACER);
        return object;
    }

    private void firstInit(Assets assets) {
        mAssets = assets;
        mBodyDef = new BodyDef();
        mBodyDef.type = BodyDef.BodyType.DynamicBody;
        mBodyDef.bullet = true;

        mShape = new CircleShape();
        mShape.setRadius(BULLET_RADIUS);
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
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_GROUND) {
            mDrawer.setBatch(batch);
            mDrawer.draw(mBody, mAssets.bullet);
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

    private void explode() {
        Vector2 pos = mBody.getPosition();
        mGameWorld.addGameObject(AnimationObject.create(mAssets.impact, pos.x, pos.y));
        setFinished(true);
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {
    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {
        if (isFinished()) {
            return;
        }
        Object other = otherFixture.getBody().getUserData();
        if (other == mShooter) {
            contact.setEnabled(false);
            return;
        }

        explode();
        if (other instanceof Racer) {
            ((Racer)other).spin();
        }
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {

    }
}
