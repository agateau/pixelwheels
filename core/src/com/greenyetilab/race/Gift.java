package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * A gift to collect
 */
public class Gift implements GameObject, Pool.Poolable, DisposableWhenOutOfSight {
    private static final ReflectionPool<Gift> sPool = new ReflectionPool<Gift>(Gift.class);

    private static final float INITIAL_DISTANCE = 3f;
    private static final float DROP_FORCE = 100f;
    private static final float DRAG_FACTOR = 2f;

    private GameWorld mGameWorld;
    private TextureRegion mRegion;
    private BodyDef mBodyDef;
    private PolygonShape mShape;

    private float mTime;
    private Body mBody;
    private boolean mPicked;

    public static Gift create(Assets assets, GameWorld gameWorld, float originX, float originY) {
        Gift obj = sPool.obtain();
        if (obj.mBodyDef == null) {
            obj.firstInit(assets);
        }
        obj.mGameWorld = gameWorld;
        obj.mTime = 0;
        obj.mPicked = false;
        obj.mBodyDef.position.set(originX, originY);

        obj.mBody = gameWorld.getBox2DWorld().createBody(obj.mBodyDef);
        obj.mBody.createFixture(obj.mShape, 1f);
        obj.mBody.setUserData(obj);

        Box2DUtils.setCollisionInfo(obj.mBody, CollisionCategories.GIFT,
                CollisionCategories.WALL | CollisionCategories.PLAYER
                | CollisionCategories.ENEMY | CollisionCategories.FLAT_ENEMY
                | CollisionCategories.GIFT);
        return obj;
    }

    public static void drop(Assets assets, GameWorld gameWorld, float srcX, float srcY, float dropAngle) {
        srcX += INITIAL_DISTANCE * MathUtils.cosDeg(dropAngle);
        srcY += INITIAL_DISTANCE * MathUtils.sinDeg(dropAngle);
        Gift gift = create(assets, gameWorld, srcX, srcY);

        float forceX = DROP_FORCE * MathUtils.cosDeg(dropAngle);
        float forceY = DROP_FORCE * MathUtils.sinDeg(dropAngle);
        gift.mBody.applyForceToCenter(forceX, forceY, true);

        gameWorld.addGameObject(gift);
    }

    private void firstInit(Assets assets) {
        mRegion = assets.gift;
        mBodyDef = new BodyDef();
        mBodyDef.type = BodyDef.BodyType.DynamicBody;

        final float U = Constants.UNIT_FOR_PIXEL;
        mShape = new PolygonShape();
        mShape.setAsBox(U * mRegion.getRegionWidth() / 2, U * mRegion.getRegionHeight() / 2);
    }

    public void pick() {
        mPicked = true;
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
        mTime += delta;
        if (mPicked) {
            dispose();
            return false;
        }
        Box2DUtils.applyDrag(mBody, DRAG_FACTOR);
        return true;
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_GROUND) {
            DrawUtils.drawBodyRegion(batch, mBody, mRegion);
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
}
