package com.greenyetilab.utils.anchor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class AnchorGroup extends WidgetGroup {
    private float mSpacing = 1;

    private Array<AnchorRule> mRules = new Array<AnchorRule>();

    public void setSpacing(float spacing) {
        mSpacing = spacing;
    }

    public float getSpacing() {
        return mSpacing;
    }

    public void addPositionRule(Actor target, Anchor targetAnchor, Actor reference, Anchor referenceAnchor) {
        addPositionRule(target, targetAnchor, reference, referenceAnchor, 0, 0);
    }

    public void addPositionRule(Actor target, Anchor targetAnchor, Actor reference, Anchor referenceAnchor, float hSpace, float vSpace) {
        PositionRule rule = new PositionRule();
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
        Actor target = rule.getTarget();
        if (target.getParent() == null) {
            addActor(target);
        }
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
