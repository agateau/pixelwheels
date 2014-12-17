package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * A mine on the road
 */
public class Mine implements GameObject, Collidable, Pool.Poolable {
    private static final ReflectionPool<Mine> sPool = new ReflectionPool<Mine>(Mine.class);

    private static final float MINE_RADIUS = 0.8f;

    private GameWorld mGameWorld;
    private Assets mAssets;
    private BodyDef mBodyDef;
    private CircleShape mShape;

    private Body mBody;
    private float mTime;
    private boolean mExploded;

    public static Mine create(GameWorld gameWorld, Assets assets, float originX, float originY) {
        Mine mine = sPool.obtain();
        if (mine.mBodyDef == null) {
            mine.firstInit(assets);
        }
        mine.mGameWorld = gameWorld;
        mine.mTime = 0;
        mine.mExploded = false;
        mine.mBodyDef.position.set(originX, originY);

        mine.mBody = gameWorld.getBox2DWorld().createBody(mine.mBodyDef);
        mine.mBody.createFixture(mine.mShape, 1f);
        mine.mBody.setUserData(mine);
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
    public boolean act(float delta) {
        mTime += delta;
        if (mExploded || mBody.getPosition().y < mGameWorld.getBottomVisibleY() - Constants.VIEWPORT_POOL_RECYCLE_HEIGHT) {
            sPool.free(this);
            return false;
        }
        return true;
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
    public void beginContact(Contact contact, Fixture otherFixture) {
        Object other = otherFixture.getBody().getUserData();
        if (other instanceof Vehicle) {
            mExploded = true;
            Vector2 pos = mBody.getPosition();
            mGameWorld.addGameObject(Explosion.create(mAssets, pos.x, pos.y));
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {

    }
}
