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
package com.agateau.ui.menu;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/** A ScrollPane which can track the current item of a menu */
public class MenuScrollPane extends ScrollPane {
    private Menu mMenu;
    private final Vector2 mTmp = new Vector2();

    private final ChangeListener mListener =
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    layout();
                    ensureItemVisible();
                }
            };

    public MenuScrollPane(Menu menu) {
        super(null);
        // Do not steal touch events from items like sliders
        setCancelTouchFocus(false);
        setMenu(menu);
    }

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu menu) {
        setActor(menu);
        mMenu = menu;
        mMenu.addListener(mListener);
    }

    public float getPrefWidth() {
        return mMenu.getWidth();
    }

    private void ensureItemVisible() {
        Menu.MenuStyle style = mMenu.getMenuStyle();

        MenuItem item = mMenu.getCurrentItem();
        Rectangle rect = item.getFocusRectangle();
        mTmp.set(rect.x, rect.y);
        item.getActor().localToAscendantCoordinates(mMenu, mTmp);
        scrollTo(
                mTmp.x - style.focusPadding,
                mTmp.y - style.focusPadding,
                rect.width + 2 * style.focusPadding,
                rect.height + 2 * style.focusPadding);
    }
}
