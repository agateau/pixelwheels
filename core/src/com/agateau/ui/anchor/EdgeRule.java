package com.agateau.ui.anchor;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A rule to adjust the position of the edge of an actor, altering the size but not the position of
 * the opposite edge
 */
public class EdgeRule implements AnchorRule {
    private final Actor mTarget;
    private final Actor mReference;
    private final Edge mTargetEdge;
    private final Edge mReferenceEdge;

    enum Edge {
        TOP,
        RIGHT,
        BOTTOM,
        LEFT
    }

    public EdgeRule(Actor target, Edge targetEdge, Actor reference, Edge referenceEdge) {
        mTarget = target;
        mReference = reference;
        mTargetEdge = targetEdge;
        mReferenceEdge = referenceEdge;
    }

    @Override
    public Actor getTarget() {
        return mTarget;
    }

    @Override
    public void apply() {
        float value = 0;
        switch (mReferenceEdge) {
            case TOP:
                value = mReference.getTop();
                break;
            case RIGHT:
                value = mReference.getRight();
                break;
            case BOTTOM:
                value = mReference.getY();
                break;
            case LEFT:
                value = mReference.getX();
                break;
        }
        switch (mTargetEdge) {
            case TOP:
                mTarget.setHeight(value - mTarget.getY());
                break;
            case RIGHT:
                mTarget.setWidth(value - mTarget.getX());
                break;
            case BOTTOM:
                float top = mTarget.getTop();
                mTarget.setY(value);
                mTarget.setHeight(top - value);
                break;
            case LEFT:
                float right = mTarget.getRight();
                mTarget.setX(value);
                mTarget.setWidth(right - value);
                break;
        }
    }
}
