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

import com.agateau.tinywheels.Assets;
import com.agateau.tinywheels.GameConfig;
import com.agateau.tinywheels.GameInfo;
import com.agateau.tinywheels.Maestro;
import com.agateau.tinywheels.TwGame;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Select your map
 */
public class SelectMapScreen extends TwStageScreen {
    private final TwGame mGame;
    private final GameInfo mGameInfo;
    private final Maestro mMaestro;
    private final GameConfig.GameModeConfig mGameModeConfig;
    private MapSelector mMapSelector;

    public SelectMapScreen(TwGame game, Maestro maestro, GameInfo gameInfo, GameConfig.GameModeConfig gameModeConfig) {
        super(game.getAssets().ui);
        mGame = game;
        mMaestro = maestro;
        mGameInfo = gameInfo;
        mGameModeConfig = gameModeConfig;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectMapScreen(mGame, mMaestro, mGameInfo, mGameModeConfig));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selectmap.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");

        mMapSelector = new MapSelector(menu);
        mMapSelector.init(assets);
        mMapSelector.setCurrent(assets.findMapInfoByID(mGameModeConfig.map));
        menu.addItem(mMapSelector);

        mMapSelector.addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                next();
            }
        });

        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        mMaestro.actionTriggered("back");
    }

    private void saveSelectedMap() {
        mGameModeConfig.map = mMapSelector.getSelected().getId();
        mGame.getConfig().flush();
    }

    private void next() {
        saveSelectedMap();
        mGameInfo.mapInfo = mMapSelector.getSelected();
        mMaestro.actionTriggered("next");
    }
}
