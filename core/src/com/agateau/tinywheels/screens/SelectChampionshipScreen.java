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

import com.agateau.tinywheels.Assets;
import com.agateau.tinywheels.ChampionshipGameInfo;
import com.agateau.tinywheels.GameConfig;
import com.agateau.tinywheels.Maestro;
import com.agateau.tinywheels.TwGame;
import com.agateau.tinywheels.map.Championship;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Select the championship
 */
public class SelectChampionshipScreen extends TwStageScreen {
    public interface Listener {
        void onBackPressed();
        void onChampionshipSelected(Championship championship);
    }
    private final TwGame mGame;
    private final Listener mListener;
    private ChampionshipSelector mChampionshipSelector;

    public SelectChampionshipScreen(TwGame game, Listener listener, final String championshipId) {
        super(game.getAssets().ui);
        mGame = game;
        mListener = listener;
        setupUi(championshipId);
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectChampionshipScreen(mGame, mListener, championshipId));
            }
        };
    }

    private void setupUi(String championshipId) {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selectchampionship.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");

        mChampionshipSelector = new ChampionshipSelector(menu);
        mChampionshipSelector.setColumnCount(2);
        mChampionshipSelector.init(assets);
        mChampionshipSelector.setCurrent(assets.findChampionshipByID(championshipId));
        menu.addItem(mChampionshipSelector);

        mChampionshipSelector.addListener(new MenuItemListener() {
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
        mListener.onBackPressed();
    }

    private void next() {
        mListener.onChampionshipSelected(mChampionshipSelector.getSelected());
    }
}
