package com.greenyetilab.race;

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
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * A player bullet
 */
public class Bullet implements GameObject, Collidable, Pool.Poolable, DisposableWhenOutOfSight {
    private static final ReflectionPool<Bullet> sPool = new ReflectionPool<Bullet>(Bullet.class);

    private static final float BULLET_RADIUS = 0.8f;
    private static final float OFFSET = 5;
    private static final float IMPULSE = 80;

    private GameWorld mGameWorld;
    private Assets mAssets;
    private BodyDef mBodyDef;
    private CircleShape mShape;

    private GameObject mShooter;
    private Body mBody;
    private boolean mExploded;

    public static Bullet create(Assets assets, GameWorld gameWorld, GameObject shooter, float originX, float originY, float angle) {
        Bullet object = sPool.obtain();
        if (object.mBodyDef == null) {
            object.firstInit(assets);
        }
        object.mShooter = shooter;
        float posX = originX + OFFSET * MathUtils.cosDeg(angle);
        float posY = originY + OFFSET * MathUtils.sinDeg(angle);
        object.mGameWorld = gameWorld;
        object.mExploded = false;
        object.mBodyDef.position.set(posX, posY);
        object.mBodyDef.angle = angle * MathUtils.degreesToRadians;

        object.mBody = gameWorld.getBox2DWorld().createBody(object.mBodyDef);
        object.mBody.createFixture(object.mShape, 0f);
        object.mBody.setUserData(object);
        object.mBody.applyLinearImpulse(IMPULSE * MathUtils.cosDeg(angle), IMPULSE * MathUtils.sinDeg(angle), posX, posY, true);
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
    public boolean act(float delta) {
        if (mExploded || getY() > mGameWorld.getTopVisibleY()) {
            dispose();
            return false;
        }
        return true;
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_GROUND) {
            DrawUtils.drawBodyRegion(batch, mBody, mAssets.bullet);
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

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (!(other instanceof GameObject)) {
            mExploded = true;
            return;
        }
        GameObject gameObject = (GameObject)other;
        if (gameObject == mShooter) {
            // Do not shoot ourselves
            contact.setEnabled(false);
            return;
        }
        HealthComponent healthComponent = gameObject.getHealthComponent();
        if (healthComponent != null) {
            healthComponent.decreaseHealth();
            // This object can take damage, let's hit it and explode
            mExploded = true;
            Vector2 pos = mBody.getPosition();
            mGameWorld.addGameObject(AnimationObject.create(mAssets.explosion, pos.x, pos.y));
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {

    }

    @Override
    public void preSolve(Contact contact, Fixture otherFixture, Manifold oldManifold) {
        Object other = otherFixture.getBody().getUserData();
        if (!(other instanceof GameObject)) {
            return;
        }
        GameObject gameObject = (GameObject)other;
        // Do not shoot ourselves, do not collide with other bullets
        if (gameObject == mShooter || gameObject instanceof Bullet) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void postSolve(Contact contact, Fixture otherFixture, ContactImpulse impulse) {

    }
}
