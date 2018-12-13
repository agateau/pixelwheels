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

class MenuItemFocusIndicator extends FocusIndicator {
    private final MenuItem mItem;

    public MenuItemFocusIndicator(MenuItem item, Menu menu) {
        super(menu);
        mItem = item;
    }

    private final Vector2 mTmp = new Vector2();
    @Override
    protected Rectangle getBoundsRectangle() {
        Rectangle rect = mItem.getFocusRectangle();
        mTmp.set(rect.x, rect.y);
        mItem.getActor().localToStageCoordinates(mTmp);
        rect.x = mTmp.x;
        rect.y = mTmp.y;
        return rect;
    }
}
