package com.greenyetilab.utils.anchor;

import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

public class AnchorGroup extends WidgetGroup {
    private float mSpacing = 1;

    private Array<AnchorRule> mRules = new Array<AnchorRule>();

    // A version of Actor.localToStageCoordinates which works with scaled actors
    static private Vector2 localToStageCoordinates(Actor actor, Vector2 pos) {
        while (actor != null) {
            pos.x = actor.getX() + pos.x * actor.getScaleX();
            pos.y = actor.getY() + pos.y * actor.getScaleY();
            actor = actor.getParent();
        }
        return pos;
    }

    static public class Rule implements AnchorRule {
        public Actor target;
        public Anchor targetAnchor;
        public Actor reference;
        public Anchor referenceAnchor;
        public float hSpace;
        public float vSpace;

        @Override
        public Actor getTarget() {
            return target;
        }

        @Override
        public void apply() {
            // Compute reference position
            Vector2 referencePos = new Vector2(
                reference.getWidth() * referenceAnchor.hPercent,
                reference.getHeight() * referenceAnchor.vPercent);

            Vector2 stagePos = localToStageCoordinates(reference, referencePos);

            // Apply space
            stagePos.add(hSpace, vSpace);

            // Position target (use target parent because setPosition() works in parent coordinates)
            Actor targetParent = target.getParent();
            if (targetParent == null) {
                return;
            }
            Vector2 targetPos = targetParent.stageToLocalCoordinates(stagePos);

            // Apply target offset
            targetPos.add(
                -target.getWidth() * target.getScaleX() * targetAnchor.hPercent,
                -target.getHeight() * target.getScaleY() * targetAnchor.vPercent);

            target.setPosition(targetPos.x, targetPos.y);
        }
    }

    static public class SizeRule implements AnchorRule {
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

    public void setSpacing(float spacing) {
        mSpacing = spacing;
    }

    public float getSpacing() {
        return mSpacing;
    }

    public void addRule(Actor target, Anchor targetAnchor, Actor reference, Anchor referenceAnchor) {
        addRule(target, targetAnchor, reference, referenceAnchor, 0, 0);
    }

    public void addRule(Actor target, Anchor targetAnchor, Actor reference, Anchor referenceAnchor, float hSpace, float vSpace) {
        Rule rule = new Rule();
        rule.target = target;
        rule.targetAnchor = targetAnchor;
        rule.reference = reference;
        rule.referenceAnchor = referenceAnchor;
        rule.hSpace = hSpace * mSpacing;
        rule.vSpace = vSpace * mSpacing;
        addRule(rule);
    }

    public void addRule(AnchorRule rule) {
        mRules.add(rule);
        addActor(rule.getTarget());
    }

    public void removeRulesForActor(Actor actor) {
        Iterator<AnchorRule> it = mRules.iterator();
        for (; it.hasNext();) {
            AnchorRule rule = it.next();
            if (rule.getTarget() == actor) {
                it.remove();
            }
        }
    }

    public void layout() {
        for (AnchorRule rule: mRules) {
            rule.apply();
        }
    }

    public float getPrefWidth() {
        return getWidth();
    }

    public float getPrefHeight() {
        return getHeight();
    }
}
