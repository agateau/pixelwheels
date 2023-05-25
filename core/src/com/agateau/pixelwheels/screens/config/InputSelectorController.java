/*
 * Copyright 2023 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.screens.config;

import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactories;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactory;
import com.agateau.pixelwheels.gameinput.GamepadInputHandler;
import com.agateau.pixelwheels.gameinput.KeyboardInputHandler;
import com.agateau.ui.UiAssets;
import com.agateau.ui.menu.ButtonMenuItem;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.menu.SelectorMenuItem;
import com.agateau.utils.Assert;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Controller for the input selector in the config screen. Can open the configuration screen for the
 * selected input handler.
 */
public class InputSelectorController {
    private final PwGame mGame;
    private final SelectorMenuItem<GameInputHandlerFactory> mSelector;
    private final ButtonMenuItem mConfigureButton;
    private final int mPlayerIdx;

    public InputSelectorController(
            PwGame game,
            SelectorMenuItem<GameInputHandlerFactory> selector,
            ButtonMenuItem configureButton,
            int idx) {
        mGame = game;
        mSelector = selector;
        mConfigureButton = configureButton;
        mPlayerIdx = idx;

        UiAssets uiAssets = mGame.getAssets().ui;
        Array<GameInputHandlerFactory> inputFactories =
                GameInputHandlerFactories.getAvailableFactories();
        for (GameInputHandlerFactory factory : inputFactories) {
            String iconName = "input-icons/" + factory.getId();
            Drawable drawable = new TextureRegionDrawable(uiAssets.atlas.findRegion(iconName));
            selector.addEntry(drawable, factory.getName(), factory);
        }

        selector.getActor()
                .addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                onInputChanged();
                            }
                        });

        configureButton.addListener(
                new MenuItemListener() {
                    @Override
                    public void triggered() {
                        onConfigureClicked();
                    }
                });
    }

    private void onInputChanged() {
        GameInputHandlerFactory factory = mSelector.getCurrentData();
        mGame.getConfig().setPlayerInputHandlerFactory(mPlayerIdx, factory);
        mGame.getConfig().flush();
        updateConfigureButton();
    }

    private void onConfigureClicked() {
        GameInputHandlerFactory factory = mSelector.getCurrentData();
        GameInputHandlerConfigScreenFactory configScreenFactory =
                getInputConfigScreenFactory(factory);
        Assert.check(configScreenFactory != null, "No config screen for this game factory");
        mGame.pushScreen(configScreenFactory.createScreen(mGame, mPlayerIdx));
    }

    public void setStartupState() {
        GameInputHandlerFactory factory =
                mGame.getConfig().getPlayerInputHandlerFactory(mPlayerIdx);
        mSelector.setCurrentData(factory);
        updateConfigureButton();
    }

    private void updateConfigureButton() {
        GameInputHandlerFactory factory = mSelector.getCurrentData();
        boolean canBeConfigured = getInputConfigScreenFactory(factory) != null;
        mConfigureButton.setDisabled(!canBeConfigured);
    }

    private static GameInputHandlerConfigScreenFactory getInputConfigScreenFactory(
            GameInputHandlerFactory factory) {
        if (factory instanceof GamepadInputHandler.Factory) {
            return GamepadConfigScreen::new;
        } else if (factory instanceof KeyboardInputHandler.Factory) {
            return KeyboardConfigScreen::new;
        } else {
            return null;
        }
    }
}
