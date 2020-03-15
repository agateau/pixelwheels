/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.gameinput.GamepadInputHandler;
import com.agateau.ui.GamepadInputMapper;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.ButtonMenuItem;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import java.util.Locale;

/** Configure an input device */
public class GamepadConfigScreen extends PwStageScreen {
    private final PwGame mGame;
    private final int mPlayerIdx;
    private final GamepadInputMapper mInputMapper;
    private final Array<GamepadButtonItemController> mButtonControllers = new Array<>();

    private GamepadButtonItemController mEditingController;

    private class GamepadButtonItemController implements GamepadInputMapper.Listener {
        private final ButtonMenuItem mMenuItem;
        private final GamepadInputMapper.GamepadButton mButtonId;
        private boolean mEditing = false;

        GamepadButtonItemController(
                ButtonMenuItem menuItem, GamepadInputMapper.GamepadButton buttonId) {
            mMenuItem = menuItem;
            mButtonId = buttonId;
            mMenuItem.addListener(
                    new MenuItemListener() {
                        @Override
                        public void triggered() {
                            startEditing(GamepadButtonItemController.this);
                        }
                    });
            updateText();
        }

        void setEditing(boolean editing) {
            mEditing = editing;
            updateText();
        }

        private void updateText() {
            String text;
            if (mEditing) {
                text = "Press the gamepad key...";
            } else {
                text = String.format(Locale.US, "%d", mInputMapper.getButtonCode(mButtonId));
            }
            mMenuItem.setText(text);
        }

        @Override
        public boolean onButtonPressed(int buttonCode, boolean pressed) {
            mInputMapper.setButtonCode(mButtonId, buttonCode);
            stopEditing();
            return true;
        }
    }

    GamepadConfigScreen(PwGame game, int playerIdx) {
        super(game.getAssets().ui);
        mGame = game;
        mPlayerIdx = playerIdx;
        GamepadInputHandler handler =
                (GamepadInputHandler) mGame.getConfig().getPlayerInputHandler(mPlayerIdx);
        mInputMapper = (GamepadInputMapper) handler.getInputMapper();
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new GamepadConfigScreen(mGame, mPlayerIdx));
            }
        };
        setupUi();
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);

        AnchorGroup root =
                (AnchorGroup) builder.build(FileUtils.assets("screens/gamepadconfig.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        createButton(
                builder.getMenuItem("triggerPadButton"), GamepadInputMapper.GamepadButton.TRIGGER);
        createButton(builder.getMenuItem("backPadButton"), GamepadInputMapper.GamepadButton.BACK);

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });
    }

    private void createButton(
            ButtonMenuItem buttonItem, GamepadInputMapper.GamepadButton buttonId) {
        GamepadButtonItemController controller =
                new GamepadButtonItemController(buttonItem, buttonId);
        mButtonControllers.add(controller);
    }

    private void startEditing(GamepadButtonItemController controller) {
        stopEditing();
        mEditingController = controller;
        mEditingController.setEditing(true);
        mInputMapper.setListener(mEditingController);
    }

    private void stopEditing() {
        if (mEditingController != null) {
            mEditingController.setEditing(false);
            mInputMapper.setListener(null);
        }
    }

    @Override
    public void onBackPressed() {
        stopEditing();
        saveConfig();
        mGame.popScreen();
    }

    private void saveConfig() {
        GameConfig config = mGame.getConfig();
        config.savePlayerInputHandlerConfig(mPlayerIdx);
    }
}
