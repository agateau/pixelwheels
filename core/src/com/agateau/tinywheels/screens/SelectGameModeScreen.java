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
package com.agateau.tinywheels.screens;

import com.agateau.tinywheels.GameMode;
import com.agateau.tinywheels.TwGame;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.utils.FileUtils;

/**
 * Select between quick race, championship...
 */
public class SelectGameModeScreen extends TwStageScreen {
    private final TwGame mGame;
    private final GameMode mGameMode;

    public SelectGameModeScreen(TwGame game, GameMode gameMode) {
        super(game.getAssets().ui);
        mGame = game;
        mGameMode = gameMode;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectGameModeScreen(mGame, mGameMode));
            }
        };
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().ui.atlas, mGame.getAssets().ui.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selectgamemode.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");
        menu.addButton("QUICK RACE").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                mGame.showQuickRace(mGameMode);
            }
        });
        menu.addButton("CHAMPIONSHIP").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                mGame.showChampionship(mGameMode);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }
}
