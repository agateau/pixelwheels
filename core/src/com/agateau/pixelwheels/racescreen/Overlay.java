/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.racescreen;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/** A generic overlay display */
public class Overlay extends WidgetGroup {
    private static final float IN_DURATION = 0.5f;
    private Actor mContent;

    public Overlay(TextureRegion dot) {
        setFillParent(true);
        Image bg = new Image(dot);
        bg.setColor(0, 0, 0, 0);
        bg.setFillParent(true);
        addActor(bg);
        bg.addAction(Actions.alpha(0.6f, IN_DURATION));
    }

    public void setContent(Actor actor) {
        if (mContent != null && mContent.getParent() != null) {
            mContent.getParent().removeActor(mContent);
        }
        mContent = actor;
    }

    @Override
    public void layout() {
        super.layout();
        if (mContent == null) {
            return;
        }
        mContent.setSize(getWidth(), getHeight());

        if (mContent.getParent() == null) {
            // First time mContent is layouted, position on it above the screen and add an animation
            // to make it fall down (can't do it in setContent() because we don't know the screen
            // size at
            // this moment)
            mContent.setPosition(0, getHeight());
            mContent.addAction(Actions.moveTo(0, 0, IN_DURATION, Interpolation.swingOut));
            addActor(mContent);
        }
    }
}
