/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.tinywheels.screens;

import com.agateau.tinywheels.GameConfig;
import com.agateau.tinywheels.PwGame;
import com.agateau.tinywheels.gameinput.GameInputHandlerFactories;
import com.agateau.tinywheels.gameinput.GameInputHandlerFactory;
import com.agateau.ui.menu.ButtonMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.menu.SelectorMenuItem;
import com.agateau.ui.menu.SwitchMenuItem;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.FileUtils;
import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * The config screen
 */
public class ConfigScreen extends TwStageScreen {
    private final PwGame mGame;
    private SelectorMenuItem<GameInputHandlerFactory> mInputSelector;
    private Label mInputDescriptionLabel;

    public ConfigScreen(PwGame game) {
        super(game.getAssets().ui);
        mGame = game;
        setupUi();
        new RefreshHelper(getStage()) {
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

        setupInputSelector(menu);

        final SwitchMenuItem rotateScreenSwitch = new SwitchMenuItem(menu);
        rotateScreenSwitch.setChecked(gameConfig.rotateCamera);
        rotateScreenSwitch.getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameConfig.rotateCamera = rotateScreenSwitch.isChecked();
                gameConfig.flush();
            }
        });
        menu.addItemWithLabel("Rotate screen:", rotateScreenSwitch);

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
            menu.addItemWithLabel("Fullscreen:", fullscreenSwitch);
        }

        final SwitchMenuItem audioSwitch = new SwitchMenuItem(menu);
        audioSwitch.setChecked(gameConfig.audio);
        audioSwitch.getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameConfig.audio = audioSwitch.isChecked();
                mGame.getAudioManager().setMuted(!gameConfig.audio);
                gameConfig.flush();
            }
        });
        menu.addItemWithLabel("Audio:", audioSwitch);

        ButtonMenuItem developerButton = new ButtonMenuItem(menu, "Developer Options");
        developerButton.getActor().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.pushScreen(new DebugScreen(mGame));
            }
        });
        menu.addItemWithLabel("Internal:", developerButton);

        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onBackPressed();
            }
        });
    }

    private void setupInputSelector(Menu menu) {
        mInputSelector = new SelectorMenuItem<GameInputHandlerFactory>(menu);
        Array<GameInputHandlerFactory> inputFactories = GameInputHandlerFactories.getAvailableFactories();

        for (GameInputHandlerFactory factory : inputFactories) {
            mInputSelector.addEntry(factory.getName(), factory);
        }
        menu.addItemWithLabel("Input:", mInputSelector);
        mInputDescriptionLabel = menu.addLabel("");
        mInputDescriptionLabel.setWrap(true);

        mInputSelector.getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameInputHandlerFactory factory = mInputSelector.getData();
                mGame.getConfig().input = factory.getId();
                updateInputDescriptionLabel();
            }
        });

        // Select current value
        String factoryId = mGame.getConfig().input;
        GameInputHandlerFactory factory = GameInputHandlerFactories.getFactoryById(factoryId);
        mInputSelector.setData(factory);

        updateInputDescriptionLabel();
    }

    private void updateInputDescriptionLabel() {
        GameInputHandlerFactory factory = mInputSelector.getData();
        mInputDescriptionLabel.setText(factory.getDescription());
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }
}
