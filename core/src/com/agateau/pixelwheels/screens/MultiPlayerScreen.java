/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.gameinput.GameInputHandler;
import com.agateau.pixelwheels.gameinput.KeyboardInputHandler;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.InputMapper;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/** Select player vehicles */
public class MultiPlayerScreen extends PwStageScreen {
    public interface Listener {
        void onBackPressed();

        void onPlayersSelected(Array<GameInfo.Player> players);
    }

    private final PwGame mGame;
    private final int mPlayerCount = Constants.MAX_PLAYERS; // Hardcoded for now
    private final Listener mListener;
    private final VehicleSelector[] mVehicleSelectors;
    private final InputMapper[] mInputMappers;

    public MultiPlayerScreen(PwGame game, Listener listener) {
        super(game.getAssets().ui);
        mGame = game;
        mListener = listener;

        mVehicleSelectors = new VehicleSelector[mPlayerCount];
        mInputMappers = new InputMapper[mPlayerCount];

        for (int idx = 0; idx < mPlayerCount; ++idx) {
            GameInputHandler inputHandler = mGame.getConfig().getPlayerInputHandler(idx);
            KeyboardInputHandler keyboardInputHandler = (KeyboardInputHandler) inputHandler;
            mInputMappers[idx] = keyboardInputHandler.getInputMapper();
        }

        setupUi();
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new MultiPlayerScreen(mGame, mListener));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = UiUtils.createUiBuilder(assets);

        AnchorGroup root =
                (AnchorGroup) builder.build(FileUtils.assets("screens/multiplayer.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        createVehicleSelector(builder, assets, 0);
        createVehicleSelector(builder, assets, 1);

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
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

    private void createVehicleSelector(UiBuilder builder, Assets assets, int idx) {
        GameConfig gameConfig = mGame.getConfig();
        String vehicleId = gameConfig.vehicles[idx];

        Menu menu = builder.getActor("menu" + (idx + 1));

        final Label readyLabel = builder.getActor("ready" + (idx + 1));

        VehicleSelector selector = new VehicleSelector(menu);
        selector.init(assets, mGame.getRewardManager());
        selector.setColumnCount(builder.getIntConfigValue("columnCount"));
        selector.setItemSize(
                builder.getIntConfigValue("itemWidth"), builder.getIntConfigValue("itemHeight"));
        mVehicleSelectors[idx] = selector;
        selector.setCurrent(assets.findVehicleDefById(vehicleId));
        selector.addListener(
                new MenuItemListener() {
                    @Override
                    public void triggered() {
                        readyLabel.setVisible(true);
                        nextIfPossible();
                    }
                });

        menu.setInputMapper(mInputMappers[idx]);
        menu.addItem(selector);
    }

    private void nextIfPossible() {
        for (VehicleSelector selector : mVehicleSelectors) {
            if (selector.getSelected() == null) {
                return;
            }
        }
        next();
    }

    private void next() {
        Array<GameInfo.Player> players = new Array<>();
        for (int idx = 0; idx < mPlayerCount; ++idx) {
            VehicleDef vehicleDef = mVehicleSelectors[idx].getSelected();
            if (vehicleDef == null) {
                return;
            }
            players.add(new GameInfo.Player(idx, vehicleDef.id));
        }

        mListener.onPlayersSelected(players);
    }
}
