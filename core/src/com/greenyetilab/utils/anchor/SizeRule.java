package com.greenyetilab.utils.anchor;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A rule to adjust the size of an actor relative to another
 */
public class SizeRule implements AnchorRule {
    public static final float KEEP_RATIO = -1;

    public SizeRule(Actor target, Actor reference, float widthPercent, float heightPercent) {
        mTarget = target;
        mReference = reference;
        mWidthPercent = widthPercent;
        mHeightPercent = heightPercent;
    }

    @Override
    public Actor getTarget() {
        return mTarget;
    }

    @Override
    public void apply() {
        if (mTarget.getWidth() == 0) {
            return;
        }
        float hfw = mTarget.getHeight() / mTarget.getWidth();
        if (mWidthPercent > 0) {
            mTarget.setWidth(mReference.getWidth() * mWidthPercent);
        }
        if (mHeightPercent > 0) {
            mTarget.setHeight(mReference.getHeight() * mHeightPercent);
        }
        if (mWidthPercent == KEEP_RATIO) {
            mTarget.setWidth(mTarget.getHeight() / hfw);
        }
        if (mHeightPercent == KEEP_RATIO) {
            mTarget.setHeight(mTarget.getWidth() * hfw);
        }
    }

    private Actor mTarget;
    private Actor mReference;
    private float mWidthPercent;
    private float mHeightPercent;
}
