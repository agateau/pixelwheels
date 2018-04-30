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

import com.agateau.tinywheels.PwGame;
import com.agateau.tinywheels.gamesetup.GameMode;
import com.agateau.tinywheels.gamesetup.PlayerCount;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItem;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.utils.FileUtils;

/**
 * Select between quick race, championship...
 */
public class SelectGameModeScreen extends TwStageScreen {
    private final PwGame mGame;
    private final PlayerCount mPlayerCount;

    public SelectGameModeScreen(PwGame game, PlayerCount playerCount) {
        super(game.getAssets().ui);
        mGame = game;
        mPlayerCount = playerCount;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectGameModeScreen(mGame, mPlayerCount));
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
                mGame.getConfig().gameMode = GameMode.QUICK_RACE;
                mGame.getConfig().flush();
                mGame.showQuickRace(mPlayerCount);
            }
        });
        MenuItem championshipItem = menu.addButton("CHAMPIONSHIP");
        championshipItem.addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                mGame.getConfig().gameMode = GameMode.CHAMPIONSHIP;
                mGame.getConfig().flush();
                mGame.showChampionship(mPlayerCount);
            }
        });
        if (mGame.getConfig().gameMode == GameMode.CHAMPIONSHIP) {
            menu.setCurrentItem(championshipItem);
        }
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }
}
