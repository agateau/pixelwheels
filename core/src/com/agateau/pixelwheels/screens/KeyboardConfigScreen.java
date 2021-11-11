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

import static com.agateau.translations.Translator.tr;

import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.gameinput.KeyboardInputHandler;
import com.agateau.ui.KeyMapper;
import com.agateau.ui.VirtualKey;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.ButtonMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/** Configure a keyboard input device */
public class KeyboardConfigScreen extends PwStageScreen {
    private final PwGame mGame;
    private final int mPlayerIdx;
    private final KeyMapper mKeyMapper;
    private Menu mMenu;

    private ButtonMenuItem mEditedButton;
    private VirtualKey mEditedVirtualKey;

    KeyboardConfigScreen(PwGame game, int playerIdx) {
        super(game.getAssets().ui);
        mGame = game;
        mPlayerIdx = playerIdx;
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new KeyboardConfigScreen(mGame, mPlayerIdx));
            }
        };

        KeyboardInputHandler handler =
                (KeyboardInputHandler) mGame.getConfig().getPlayerInputHandler(mPlayerIdx);
        Assert.check(handler != null, "input handler is not a KeyboardInputHandler");
        mKeyMapper = (KeyMapper) handler.getInputMapper();
        Assert.check(mKeyMapper != null, "input mapper is not a KeyMapper");

        setupUi();
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);

        AnchorGroup root =
                (AnchorGroup) builder.build(FileUtils.assets("screens/keyboardconfig.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mMenu = builder.getActor("menu");

        if (mPlayerIdx == 0) {
            // First player only configure in-game keys, others also configure UI keys
            createKeyItem(mMenu, tr("Brake:"), VirtualKey.DOWN);
            createKeyItem(mMenu, tr("Steer left:"), VirtualKey.LEFT);
            createKeyItem(mMenu, tr("Steer right:"), VirtualKey.RIGHT);
            createKeyItem(mMenu, tr("Trigger:"), VirtualKey.TRIGGER);
        } else {
            createKeyItem(mMenu, tr("Up | (unused):"), VirtualKey.UP);
            createKeyItem(mMenu, tr("Down | Brake:"), VirtualKey.DOWN);
            createKeyItem(mMenu, tr("Left | Steer left:"), VirtualKey.LEFT);
            createKeyItem(mMenu, tr("Right | Steer right:"), VirtualKey.RIGHT);
            createKeyItem(mMenu, tr("Activate | Trigger:"), VirtualKey.TRIGGER);
            createKeyItem(mMenu, tr("Back | (unused):"), VirtualKey.BACK);
        }

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });
    }

    private void createKeyItem(Menu menu, String text, VirtualKey virtualKey) {
        ButtonMenuItem button = new ButtonMenuItem(menu, generateButtonText(virtualKey));
        button.addListener(
                new MenuItemListener() {
                    @Override
                    public void triggered() {
                        startEditing(button, virtualKey);
                    }
                });

        menu.addItemWithLabel(text, button);
    }

    private final InputListener mEditListener =
            new InputListener() {
                public boolean keyUp(InputEvent event, int keycode) {
                    Assert.check(mEditedVirtualKey != null, "mEditVirtualKey should be set");

                    if (keycode != Input.Keys.ESCAPE) {
                        mKeyMapper.setKey(mEditedVirtualKey, keycode);
                    }
                    mEditedButton.setText(generateButtonText(mEditedVirtualKey));

                    stopEditing();
                    return true;
                }
            };

    private void startEditing(ButtonMenuItem button, VirtualKey virtualKey) {
        mMenu.setDisabled(true);
        mEditedButton = button;
        mEditedVirtualKey = virtualKey;

        button.setText("...");
        getStage().getRoot().addListener(mEditListener);
    }

    private void stopEditing() {
        mMenu.setDisabled(false);
        getStage().getRoot().removeListener(mEditListener);
        mEditedVirtualKey = null;
    }

    private final StringBuilder mStringBuilder = new StringBuilder();

    private String generateButtonText(VirtualKey virtualKey) {
        Integer[] keys = mKeyMapper.getKeys(virtualKey);
        Assert.check(keys.length >= 1, "No keys defined");

        mStringBuilder.setLength(0);
        for (int idx = 0; idx < keys.length; ++idx) {
            if (idx > 0) {
                mStringBuilder.append(", ");
            }
            mStringBuilder.append(Input.Keys.toString(keys[idx]));
        }
        return mStringBuilder.toString();
    }

    @Override
    public void onBackPressed() {
        saveConfig();
        mGame.popScreen();
    }

    private void saveConfig() {
        GameConfig config = mGame.getConfig();
        config.savePlayerInputHandlerConfig(mPlayerIdx);
    }
}
