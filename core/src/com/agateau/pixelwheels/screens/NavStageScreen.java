/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.screens;

import com.agateau.ui.UiAssets;
import com.agateau.ui.UiInputActor;
import com.agateau.ui.VirtualKey;
import com.agateau.utils.Assert;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * A stage screen with navigation buttons.
 *
 * <p>At least a Next button in the bottom-right corner, optionally a Back button in the bottom-left
 * corner.
 */
public class NavStageScreen extends PwStageScreen {
    public interface NavListener {
        void onBackPressed();

        void onNextPressed();
    }

    public abstract static class NextListener implements NavListener {
        @Override
        public void onBackPressed() {
            onNextPressed();
        }
    }

    public NavStageScreen(UiAssets uiAssets) {
        super(uiAssets);
        getStage()
                .addActor(
                        new UiInputActor() {
                            public void onKeyJustPressed(VirtualKey key) {
                                if (key == VirtualKey.TRIGGER) {
                                    onNextPressed();
                                }
                            }
                        });
    }

    private NavListener mNavListener;

    public void setNavListener(NavListener navListener) {
        mNavListener = navListener;
    }

    public void setupNextButton(Button button) {
        button.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        onNextPressed();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Assert.check(mNavListener != null, "No listener set");
        mNavListener.onBackPressed();
    }

    public void onNextPressed() {
        Assert.check(mNavListener != null, "No listener set");
        mNavListener.onNextPressed();
    }
}
