/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.ui.anchor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class AnchorGroup extends WidgetGroup {
    private float mGridSize = 1;

    private final Array<AnchorRule> mRules = new Array<>();

    public void setGridSize(float gridSize) {
        mGridSize = gridSize;
    }

    public float getGridSize() {
        return mGridSize;
    }

    public void addPositionRule(
            Actor target, Anchor targetAnchor, Actor reference, Anchor referenceAnchor) {
        addPositionRule(target, targetAnchor, reference, referenceAnchor, 0, 0);
    }

    public void addPositionRule(
            Actor target,
            Anchor targetAnchor,
            Actor reference,
            Anchor referenceAnchor,
            float hSpace,
            float vSpace) {
        PositionRule rule = new PositionRule();
        rule.target = target;
        rule.targetAnchor = targetAnchor;
        rule.reference = reference;
        rule.referenceAnchor = referenceAnchor;
        rule.hSpace = hSpace * mGridSize;
        rule.vSpace = vSpace * mGridSize;
        addRule(rule);
    }

    public void addSizeRule(Actor target, Actor reference, float hPercent, float vPercent) {
        addSizeRule(target, reference, hPercent, vPercent, 0, 0);
    }

    public void addSizeRule(
            Actor target,
            Actor reference,
            float hPercent,
            float vPercent,
            float hSpace,
            float vSpace) {
        SizeRule rule = new SizeRule(target, reference, hPercent, vPercent);
        rule.setPadding(hSpace * mGridSize, vSpace * mGridSize);
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
        for (; it.hasNext(); ) {
            AnchorRule rule = it.next();
            if (rule.getTarget() == actor) {
                it.remove();
            }
        }
    }

    @Override
    public void layout() {
        for (Actor actor : getChildren()) {
            if (actor instanceof Layout) {
                ((Layout) actor).invalidate();
                ((Layout) actor).validate();
            }
        }
        for (AnchorRule rule : mRules) {
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
