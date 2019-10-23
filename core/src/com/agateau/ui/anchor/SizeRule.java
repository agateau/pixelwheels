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

/** A rule to adjust the size of an actor relative to another */
public class SizeRule implements AnchorRule {
    public static final float KEEP_RATIO = -1;
    public static final float IGNORE = -2;

    private final Actor mTarget;
    private final Actor mReference;
    private final float mWidthPercent;
    private final float mHeightPercent;

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
