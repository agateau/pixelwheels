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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/** A rule to position an actor relative to another */
public class PositionRule implements AnchorRule {
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
        Vector2 referencePos =
                new Vector2(
                        reference.getWidth() * referenceAnchor.hPercent,
                        reference.getHeight() * referenceAnchor.vPercent);

        Vector2 stagePos = reference.localToStageCoordinates(referencePos);

        // Apply space
        stagePos.add(hSpace, vSpace);

        // Position target (use target parent because setPosition() works in parent coordinates)
        Actor targetParent = target.getParent();
        if (targetParent == null) {
            return;
        }
        Vector2 targetPos = targetParent.stageToLocalCoordinates(stagePos);

        // Apply target offset (If right-aligned, hPercent is 100% => -width * scale.
        // If centered, hPercent is 50% => -width * scale / 2)
        targetPos.add(
                -target.getWidth() * target.getScaleX() * targetAnchor.hPercent,
                -target.getHeight() * target.getScaleY() * targetAnchor.vPercent);

        target.setPosition(MathUtils.floor(targetPos.x), MathUtils.floor(targetPos.y));
    }
}
