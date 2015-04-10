package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
 * A mine on the road
 */
public class Mine extends GameObjectAdapter implements Collidable, Pool.Poolable, Disposable {
    private static final ReflectionPool<Mine> sPool = new ReflectionPool<Mine>(Mine.class);

    private static final float MINE_RADIUS = 0.8f;

    private GameWorld mGameWorld;
    private Assets mAssets;
    private BodyDef mBodyDef;
    private CircleShape mShape;

    private Body mBody;
    private float mTime;

    public static Mine create(GameWorld gameWorld, Assets assets, float originX, float originY) {
        Mine mine = sPool.obtain();
        if (mine.mBodyDef == null) {
            mine.firstInit(assets);
        }
        mine.mGameWorld = gameWorld;
        mine.mTime = 0;
        mine.setFinished(false);
        mine.mBodyDef.position.set(originX, originY);

        mine.mBody = gameWorld.getBox2DWorld().createBody(mine.mBodyDef);
        mine.mBody.createFixture(mine.mShape, 1f);
        mine.mBody.setUserData(mine);

        Box2DUtils.setCollisionInfo(mine.mBody, CollisionCategories.FLAT_AI_VEHICLE,
                CollisionCategories.WALL | CollisionCategories.RACER
                | CollisionCategories.AI_VEHICLE | CollisionCategories.FLAT_AI_VEHICLE
                | CollisionCategories.GIFT);
        return mine;
    }

    private void firstInit(Assets assets) {
        mAssets = assets;
        mBodyDef = new BodyDef();
        mBodyDef.type = BodyDef.BodyType.StaticBody;

        mShape = new CircleShape();
        mShape.setRadius(MINE_RADIUS);
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
            Vector2 pos = mBody.getPosition();
            TextureRegion region = mAssets.mine.getKeyFrame(mTime);
            float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
            float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
            batch.draw(region, pos.x - w / 2, pos.y - h / 2 , w, h);
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
        setFinished(true);
        Vector2 pos = mBody.getPosition();
        mGameWorld.addGameObject(AnimationObject.create(mAssets.explosion, pos.x, pos.y));
    }

    @Override
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (!(other instanceof GameObject)) {
            return;
        }
        HealthComponent healthComponent = ((GameObject)other).getHealthComponent();
        if (healthComponent != null) {
            explode();
            healthComponent.decreaseHealth();
        }
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
}
