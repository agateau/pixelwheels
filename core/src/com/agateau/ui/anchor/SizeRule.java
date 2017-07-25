package com.agateau.ui.anchor;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A rule to adjust the size of an actor relative to another
 */
public class SizeRule implements AnchorRule {
    public static final float KEEP_RATIO = -1;
    public static final float IGNORE = -2;

    private Actor mTarget;
    private Actor mReference;
    private float mWidthPercent;
    private float mHeightPercent;

    private float mWidthPadding = 0;
    private float mHeightPadding = 0;

    public SizeRule(Actor target, Actor reference, float widthPercent, float heightPercent) {
        mTarget = target;
        mReference = reference;
        mWidthPercent = widthPercent;
        mHeightPercent = heightPercent;
    }

    public SizeRule setPadding(float width, float height) {
        mWidthPadding = width;
        mHeightPadding = height;
        return this;
    }

    @Override
    public Actor getTarget() {
        return mTarget;
    }

    @Override
    public void apply() {
        if (mWidthPercent > 0) {
            mTarget.setWidth(mReference.getWidth() * mWidthPercent + mWidthPadding);
        }
        if (mHeightPercent > 0) {
            mTarget.setHeight(mReference.getHeight() * mHeightPercent + mHeightPadding);
        }
        if (mWidthPercent == KEEP_RATIO) {
            if (mTarget.getHeight() == 0) {
                return;
            }
            float wfh = mTarget.getWidth() / mTarget.getHeight();
            mTarget.setWidth(mTarget.getHeight() * wfh + mWidthPadding);
        }
        if (mHeightPercent == KEEP_RATIO) {
            if (mTarget.getWidth() == 0) {
                return;
            }
            float hfw = mTarget.getHeight() / mTarget.getWidth();
            mTarget.setHeight(mTarget.getWidth() * hfw + mHeightPadding);
        }
    }
}
