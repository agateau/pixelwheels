/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.PwGame;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Configure an input device
 */
public class GamepadConfigScreen extends PwStageScreen {
    private final PwGame mGame;
    private final int mPlayerIdx;

    public GamepadConfigScreen(PwGame game, int playerIdx) {
        super(game.getAssets().ui);
        mGame = game;
        mPlayerIdx = playerIdx;
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new GamepadConfigScreen(mGame, mPlayerIdx));
            }
        };
        setupUi();
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/gamepadconfig.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");

        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        mGame.getDebugIntrospector().save();
        mGame.getGamePlayIntrospector().save();
        mGame.popScreen();
    }
}
