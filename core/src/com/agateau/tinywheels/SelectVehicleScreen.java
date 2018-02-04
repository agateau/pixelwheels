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
package com.agateau.tinywheels;

import com.agateau.tinywheels.gameinput.GameInputHandlerFactories;
import com.agateau.tinywheels.gameinput.GameInputHandlerFactory;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Select your vehicle
 */
public class SelectVehicleScreen extends TwStageScreen {
    private final TwGame mGame;
    private final Maestro mMaestro;
    private final GameInfo mGameInfo;
    private VehicleSelector mVehicleSelector;

    public SelectVehicleScreen(TwGame game, Maestro maestro, GameInfo gameInfo) {
        super(game.getAssets().ui);
        mGame = game;
        mMaestro = maestro;
        mGameInfo = gameInfo;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectVehicleScreen(mGame, mMaestro, mGameInfo));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selectvehicle.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");

        mVehicleSelector = new VehicleSelector(menu);
        mVehicleSelector.init(assets);
        String id = mGame.getConfig().onePlayerVehicle;
        mVehicleSelector.setCurrent(assets.findVehicleDefByID(id));
        menu.addItem(mVehicleSelector);

        mVehicleSelector.addListener(new MenuItemListener() {
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

    private void next() {
        String id = mVehicleSelector.getSelectedId();
        GameConfig gameConfig = mGame.getConfig();

        gameConfig.onePlayerVehicle = id;
        gameConfig.flush();

        // If we came here from the map screen then a player has already been added, remove it
        mGameInfo.clearPlayers();

        String inputHandlerId = gameConfig.input;
        GameInputHandlerFactory factory = GameInputHandlerFactories.getFactoryById(inputHandlerId);
        mGameInfo.addPlayer(id, factory.create());
        mMaestro.actionTriggered("next");
    }
}
