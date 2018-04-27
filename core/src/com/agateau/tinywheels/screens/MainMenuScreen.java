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

import com.agateau.tinywheels.GameMode;
import com.agateau.tinywheels.TwGame;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.utils.FileUtils;
import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.Gdx;

/**
 * Main menu, shown at startup
 */
public class MainMenuScreen extends TwStageScreen {
    private final TwGame mGame;

    public MainMenuScreen(TwGame game) {
        super(game.getAssets().ui);
        mGame = game;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.showMainMenu();
            }
        };
    }

    private void setupUi() {
        boolean desktop = PlatformUtils.isDesktop();
        UiBuilder builder = new UiBuilder(mGame.getAssets().ui.atlas, mGame.getAssets().ui.skin);
        if (desktop) {
            builder.defineVariable("desktop");
        }

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/mainmenu.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");
        if (desktop) {
            menu.addButton("ONE PLAYER").addListener(new MenuItemListener() {
                @Override
                public void triggered() {
                    mGame.pushScreen(new SelectGameModeScreen(mGame, GameMode.ONE_PLAYER));
                }
            });
            menu.addButton("MULTI PLAYER").addListener(new MenuItemListener() {
                @Override
                public void triggered() {
                    mGame.pushScreen(new SelectGameModeScreen(mGame, GameMode.MULTI_PLAYER));
                }
            });
        } else {
            menu.addButton("QUICK RACE").addListener(new MenuItemListener() {
                @Override
                public void triggered() {
                    mGame.showQuickRace(GameMode.ONE_PLAYER);
                }
            });
            menu.addButton("CHAMPIONSHIP").addListener(new MenuItemListener() {
                @Override
                public void triggered() {
                    mGame.showChampionship(GameMode.ONE_PLAYER);
                }
            });
        }
        menu.addButton("SETTINGS").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                mGame.pushScreen(new ConfigScreen(mGame));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Gdx.app.exit();
    }
}
