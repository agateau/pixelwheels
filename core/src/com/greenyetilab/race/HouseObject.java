package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * A house, onto gifts can be dropped
 */
public class HouseObject implements GameObject {
    // Offsets from the top-right corner
    private static final float CHIMNEY_X_OFFSET = Constants.UNIT_FOR_PIXEL * -32;
    private static final float CHIMNEY_Y_OFFSET = Constants.UNIT_FOR_PIXEL * -31;
    private final NinePatch mHousePatch;
    private final Assets.Pads mHousePads = new Assets.Pads();
    private final NinePatch mShadowPatch;
    private final float mX;
    private final float mY;
    private final float mWidth;
    private final float mHeight;
    private final Body mBody;

    public HouseObject(GameWorld world, Assets assets, float originX, float originY, float width, float height) {
        mHousePatch = assets.createScaledPatch("house", mHousePads);
        mShadowPatch = assets.createScaledPatch("building-shadow");
        mX = originX;
        mY = originY;
        mWidth = width;
        mHeight = height;

        mBody = Box2DUtils.createStaticBox(world.getBox2DWorld(),
                mX + mHousePads.left,
                mY + mHousePads.top,
                mWidth - mHousePads.right - mHousePads.left,
                mHeight - mHousePads.top - mHousePads.bottom);

        mBody.setUserData(this);
        mBody.setAwake(false);

        world.addChimneyPos(mX + mWidth + CHIMNEY_X_OFFSET, mY + mHeight + CHIMNEY_Y_OFFSET);
    }

    @Override
    public boolean act(float delta) {
        return true;
    }

    @Override
    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_SHADOWS) {
            mShadowPatch.draw(batch,
                    mX + mHousePads.left,
                    mY + mHousePads.top - mShadowPatch.getBottomHeight(),
                    mWidth - mHousePads.right - mHousePads.left + mShadowPatch.getRightWidth(),
                    mHeight - mHousePads.top - mHousePads.bottom + mShadowPatch.getBottomHeight());
        }
        if (zIndex == Constants.Z_OBSTACLES) {
            mHousePatch.draw(batch, mX, mY, mWidth, mHeight);
        }
    }
}
