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

/**
 * A mine on the road
 */
public class Mine implements GameObject, Collidable {
    private static final float FRAME_DURATION = 0.2f;
    private static final float MINE_RADIUS = 0.8f;

    private Animation mAnimation;
    private Body mBody;

    private float mTime;
    private boolean mExploded;

    public void init(GameWorld gameWorld, Assets assets, float originX, float originY) {
        if (mAnimation == null) {
            firstInit(assets);
        }
        mTime = 0;
        mExploded = false;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(originX, originY);
        mBody = gameWorld.getBox2DWorld().createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(MINE_RADIUS);
        mBody.createFixture(shape, 1f);

        mBody.setAwake(false);
        mBody.setUserData(this);
    }

    private void firstInit(Assets assets) {
        mAnimation = new Animation(FRAME_DURATION,
            assets.atlas.findRegion("mine-1"),
            assets.atlas.findRegion("mine-2"));
        mAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public boolean act(float delta) {
        mTime += delta;
        if (mExploded) {
            mBody.getWorld().destroyBody(mBody);
        }
        return !mExploded;
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_GROUND) {
            Vector2 pos = mBody.getPosition();
            TextureRegion region = mAnimation.getKeyFrame(mTime);
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
        }
    }

    @Override
    public void endContact(Contact contact, Fixture otherFixture) {

    }
}
