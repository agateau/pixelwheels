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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;

/** A MenuItem in a Menu */
public interface MenuItem {
    Actor getActor();

    /** Must forward events to getActor().addListener() */
    boolean addListener(EventListener eventListener);

    boolean isFocusable();

    void trigger();

    /**
     * Called when the user presses the Up virtual key
     *
     * @return true if handled
     */
    boolean goUp();
    /**
     * Called when the user presses the Down virtual key
     *
     * @return true if handled
     */
    boolean goDown();

    void goLeft();

    void goRight();

    /**
     * Returns the coordinates of the focus rectangle in the item actor coordinates
     *
     * @return the focus rectangle
     */
    Rectangle getFocusRectangle();

    /**
     * The ratio between the item width and its parent width
     *
     * <p>If ratio is greater than 0, item width will be set to parentWidth * ratio
     *
     * <p>If ratio is 0, item width won't be changed
     */
    float getParentWidthRatio();

    void setFocused(boolean focused);
}
