/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.ui.menu;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

class FocusIndicator {
    private static final int MARGIN = 5;
    private static final float ANIMATION_DURATION = 0.2f;
    private final Image mImage;
    private final MenuItem mItem;

    public FocusIndicator(MenuItem item, Menu menu) {
        mItem = item;
        mImage = new Image(menu.getMenuStyle().focus);
        mImage.setTouchable(Touchable.disabled);
        mImage.setColor(1, 1, 1, 0);
        menu.getStage().addActor(mImage);
    }

    public void setFocused(boolean focused) {
        if (focused) {
            updateBounds();
            mImage.addAction(Actions.alpha(1, ANIMATION_DURATION));
        } else {
            mImage.addAction(Actions.alpha(0, ANIMATION_DURATION));
        }
    }

    public void update() {
        updateBounds();
    }

    private final Vector2 mTmp = new Vector2();
    private void updateBounds() {
        Rectangle rect = mItem.getFocusRectangle();
        mTmp.set(rect.x, rect.y);
        Actor actor = mItem.getActor();
        actor.localToAscendantCoordinates(mImage.getParent(), mTmp);
        mImage.setBounds(mTmp.x - MARGIN, mTmp.y - MARGIN, rect.width + 2 * MARGIN, rect.height + 2 * MARGIN);
    }
}
