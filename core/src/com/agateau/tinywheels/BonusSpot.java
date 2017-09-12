package com.agateau.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * The bonus waiting to be hit by a the player
 */
public class BonusSpot extends GameObjectAdapter {
    private static final float DISABLED_TIMEOUT = 5;
    private final TextureRegion mRegion;
    private final float mX;
    private final float mY;
    private final Body mBody;
    private float mDisabledTimeout = 0;
    private BodyRegionDrawer mDrawer = new BodyRegionDrawer();

    public BonusSpot(Assets assets, GameWorld gameWorld, float x, float y) {
        final float U = Constants.UNIT_FOR_PIXEL;
        mX = x;
        mY = y;

        mRegion = assets.gift;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(U * mRegion.getRegionWidth() / 2, U * mRegion.getRegionHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(mX, mY);

        mBody = gameWorld.getBox2DWorld().createBody(bodyDef);
        Fixture fixture = mBody.createFixture(shape, 1f);
        fixture.setSensor(true);
        mBody.setUserData(this);

        mBody.setAngularVelocity(240 * MathUtils.degreesToRadians);

        shape.dispose();
    }

    @Override
    public void act(float delta) {
        if (mDisabledTimeout > 0) {
            // mBody is still active on the first call of act() after pickBonus()
            mBody.setActive(false);
            mDisabledTimeout -= delta;
            if (mDisabledTimeout <= 0) {
                mDisabledTimeout = 0;
                mBody.setActive(true);
            }
        }
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (!mBody.isActive()) {
            return;
        }
        if (zIndex == Constants.Z_GROUND) {
            mDrawer.setBatch(batch);
            mDrawer.drawShadow(mBody, mRegion);
        } else if (zIndex == Constants.Z_OBSTACLES) {
            mDrawer.setBatch(batch);
            mDrawer.draw(mBody, mRegion);
        }
    }

    @Override
    public float getX() {
        return mX;
    }

    @Override
    public float getY() {
        return mY;
    }

    public void pickBonus() {
        mDisabledTimeout = DISABLED_TIMEOUT;
    }
}
