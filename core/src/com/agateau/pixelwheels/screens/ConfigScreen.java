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
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactories;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactory;
import com.agateau.pixelwheels.gameinput.GamepadInputHandler;
import com.agateau.pixelwheels.gameinput.KeyboardInputHandler;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.ButtonMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemGroup;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.menu.SelectorMenuItem;
import com.agateau.ui.menu.SwitchMenuItem;
import com.agateau.ui.menu.TabMenuItem;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * The config screen
 */
public class ConfigScreen extends PwStageScreen {
    private final PwGame mGame;

    interface GameInputHandlerConfigScreenFactory {
        Screen createScreen(PwGame game, int playerIdx);
    }

    private static final GameInputHandlerConfigScreenFactory sGamepadConfigScreenFactory = (game, playerIdx) -> new GamepadConfigScreen(game, playerIdx);

    private static final GameInputHandlerConfigScreenFactory sKeyboardConfigScreenFactory = (game, playerIdx) -> new KeyboardConfigScreen(game, playerIdx);

    public ConfigScreen(PwGame game) {
        super(game.getAssets().ui);
        mGame = game;
        setupUi();
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new ConfigScreen(mGame));
            }
        };
    }

    private void setupUi() {
        final GameConfig gameConfig = mGame.getConfig();

        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/config.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");
        menu.setLabelColumnWidth(250);

        TabMenuItem tab = new TabMenuItem(menu);
        menu.addItem(tab);
        {
            MenuItemGroup group = tab.addPage("Input");

            if (PlatformUtils.isDesktop()) {
                for (int idx = 0; idx < Constants.MAX_PLAYERS; ++idx) {
                    setupInputSelector(menu, group, "Input " + (idx + 1), idx);
                }
            } else {
                setupInputSelector(menu, group, "Input", 0);
            }
        }

        {
            MenuItemGroup group = tab.addPage("Audio & Video");

            final SwitchMenuItem audioSwitch = new SwitchMenuItem(menu);
            audioSwitch.setChecked(gameConfig.audio);
            audioSwitch.getActor().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    gameConfig.audio = audioSwitch.isChecked();
                    gameConfig.flush();
                }
            });
            group.addItemWithLabel("Audio:", audioSwitch);

            if (PlatformUtils.isDesktop()) {
                final SwitchMenuItem fullscreenSwitch = new SwitchMenuItem(menu);
                fullscreenSwitch.setChecked(gameConfig.fullscreen);
                fullscreenSwitch.getActor().addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        gameConfig.fullscreen = fullscreenSwitch.isChecked();
                        mGame.setFullscreen(gameConfig.fullscreen);
                        gameConfig.flush();
                    }
                });
                group.addItemWithLabel("Fullscreen:", fullscreenSwitch);
            }
        }

        {
            MenuItemGroup group = tab.addPage("Misc");
            ButtonMenuItem developerButton = new ButtonMenuItem(menu, "Developer Options");
            developerButton.getActor().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    mGame.pushScreen(new DebugScreen(mGame));
                }
            });
            group.addItemWithLabel("Internal:", developerButton);
        }

        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onBackPressed();
            }
        });
    }

    class InputSelectorController {
        private final SelectorMenuItem<GameInputHandlerFactory> mSelector;
        private final ButtonMenuItem mConfigureButton;
        private final int mPlayerIdx;

        InputSelectorController(SelectorMenuItem<GameInputHandlerFactory> selector, ButtonMenuItem configureButton, int idx) {
            mSelector = selector;
            mConfigureButton = configureButton;
            mPlayerIdx = idx;

            Array<GameInputHandlerFactory> inputFactories = GameInputHandlerFactories.getAvailableFactories();
            for (GameInputHandlerFactory factory : inputFactories) {
                selector.addEntry(factory.getName(), factory);
            }

            selector.getActor().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    onInputChanged();
                }
            });

            configureButton.addListener(new MenuItemListener() {
                @Override
                public void triggered() {
                    onConfigureClicked();
                }
            });
        }

        private void onInputChanged() {
            GameInputHandlerFactory factory = mSelector.getData();
            mGame.getConfig().setPlayerInputHandlerFactory(mPlayerIdx, factory);
            mGame.getConfig().flush();
            updateConfigureButton();
        }

        private void onConfigureClicked() {
            GameInputHandlerFactory factory = mSelector.getData();
            GameInputHandlerConfigScreenFactory configScreenFactory = getInputConfigScreenFactory(factory);
            Assert.check(configScreenFactory != null, "No config screen for this game factory");
            mGame.pushScreen(configScreenFactory.createScreen(mGame, mPlayerIdx));
        }

        private void setStartupState() {
            GameInputHandlerFactory factory = mGame.getConfig().getPlayerInputHandlerFactory(mPlayerIdx);
            mSelector.setData(factory);
            updateConfigureButton();
        }

        private void updateConfigureButton() {
            GameInputHandlerFactory factory = mSelector.getData();
            boolean canBeConfigured = getInputConfigScreenFactory(factory) != null;
            mConfigureButton.setDisabled(!canBeConfigured);
        }
    }

    private void setupInputSelector(Menu menu, MenuItemGroup group, String label, final int idx) {
        SelectorMenuItem<GameInputHandlerFactory> selector = new SelectorMenuItem<>(menu);
        group.addItemWithLabel(label + ":", selector);

        ButtonMenuItem configureButton = new ButtonMenuItem(menu, "Configure");
        group.addItemWithLabel("", configureButton);

        InputSelectorController controller = new InputSelectorController(selector, configureButton, idx);
        controller.setStartupState();
    }

    private GameInputHandlerConfigScreenFactory getInputConfigScreenFactory(GameInputHandlerFactory factory) {
        if (factory instanceof GamepadInputHandler.Factory) {
            return sGamepadConfigScreenFactory;
        } else if (factory instanceof KeyboardInputHandler.Factory) {
            return sKeyboardConfigScreenFactory;
        } else {
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }
}
