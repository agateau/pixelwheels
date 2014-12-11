package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

/**
 * A tree
 */
public class TreeObject implements GameObject {
    private static final float TRUNK_RADIUS_RATIO = 0.4f;
    private final Sprite mSprite;
    private final Sprite mShadowSprite;
    private final GameWorld mWorld;
    private final float mPosX;
    private final float mPosY;

    public TreeObject(GameWorld world, Assets assets, float posX, float posY) {
        mWorld = world;
        mPosX = posX;
        mPosY = posY;

        TextureRegion region = assets.atlas.findRegion("tree");
        mSprite = new Sprite(region);

        float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
        float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
        mSprite.setSize(w, h);
        mSprite.setOriginCenter();
        mSprite.setPosition(posX - w / 2, posY - h / 2);
        mSprite.setRotation(MathUtils.random(360));

        mShadowSprite = new Sprite(region);
        mShadowSprite.setSize(w, h);
        mShadowSprite.setOriginCenter();
        mShadowSprite.setColor(0, 0, 0, DrawUtils.SHADOW_ALPHA);
        mShadowSprite.setPosition(mSprite.getX() + DrawUtils.SHADOW_OFFSET_X, mSprite.getY() + DrawUtils.SHADOW_OFFSET_Y);
        mShadowSprite.setRotation(mSprite.getRotation());
    }

    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(mPosX, mPosY);
        Body body = mWorld.getBox2DWorld().createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(Math.min(mSprite.getWidth(), mSprite.getHeight()) / 2 * TRUNK_RADIUS_RATIO);
        body.createFixture(shape, 1f);

        body.setAwake(false);
    }

    @Override
    public boolean act(float delta) {
        return true;
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_SHADOWS) {
            mShadowSprite.draw(batch);
        } else if (zIndex == Constants.Z_OBSTACLES) {
            mSprite.draw(batch);
        }
    }
}
