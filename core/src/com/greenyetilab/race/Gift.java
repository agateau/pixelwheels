package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Represents a Gift flying to the chimney
 */
public class Gift implements Pool.Poolable, GameObject {
    private static final float ANIMATION_DURATION = 0.5f;
    private Sprite mSprite;
    private Vector2 mSrcPos = new Vector2();
    private Vector2 mDstPos = new Vector2();
    private float mTime;
    private Vehicle mVehicle;

    @Override
    public void reset() {
    }

    @Override
    public boolean act(float delta) {
        mTime += delta;
        if (mTime >= 0) {
            updateSprite();
        }
        return mTime < ANIMATION_DURATION;
    }

    @Override
    public void draw(Batch batch, int z) {
        if (z != Constants.Z_FLYING) {
            return;
        }
        if (mTime >= 0) {
            mSprite.draw(batch);
        }
    }

    public void init(RaceGame game, Vehicle vehicle, Vector2 dstPos, float delay) {
        if (mSprite == null) {
            firstInit(game);
        }
        mVehicle = vehicle;
        mTime = -delay;
        mSrcPos.set(-1, -1);
        mDstPos.set(dstPos);
    }

    private void firstInit(RaceGame game) {
        TextureRegion region = game.getAssets().atlas.findRegion("gift");
        mSprite = new Sprite(region);
        mSprite.setSize(Constants.UNIT_FOR_PIXEL * region.getRegionWidth(), Constants.UNIT_FOR_PIXEL * region.getRegionHeight());
        mSprite.setOriginCenter();
    }

    private void updateSprite() {
        if (mSrcPos.x < 0) {
            mSrcPos.set(mVehicle.getPosition());
        }
        float t = mTime / ANIMATION_DURATION;
        // scale = 1 - (2 * t - 1)^2 / 2
        // bell goes from 0 to 1 to 0
        float bell = 1f - (float)Math.pow(2 * t - 1, 2);
        mSprite.setScale(0.5f + 2 * bell);
        mSprite.setRotation(180 * t);
        mSprite.setX(MathUtils.lerp(mSrcPos.x, mDstPos.x - mSprite.getWidth() / 2, t));
        mSprite.setY(MathUtils.lerp(mSrcPos.y, mDstPos.y - mSprite.getHeight() / 2, t));
    }
}
