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
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactories;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactory;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.ui.InputMapper;
import com.agateau.ui.KeyMapper;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.VirtualKey;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * Select player vehicles
 */
public class MultiPlayerScreen extends PwStageScreen {
    public interface Listener {
        void onBackPressed();
        void onPlayersSelected(Array<GameInfo.Player> players);
    }
    private final PwGame mGame;
    private final Listener mListener;
    private VehicleSelector[] mVehicleSelectors = new VehicleSelector[2];
    private InputMapper[] mInputMappers = new InputMapper[2];

    public MultiPlayerScreen(PwGame game, Listener listener) {
        super(game.getAssets().ui);
        mGame = game;
        mListener = listener;

        mInputMappers[0] = KeyMapper.getDefaultInstance();
        KeyMapper secondKeyMapper = new KeyMapper();
        mInputMappers[1] = secondKeyMapper;

        secondKeyMapper.setKey(VirtualKey.LEFT, Input.Keys.X);
        secondKeyMapper.setKey(VirtualKey.RIGHT, Input.Keys.V);
        secondKeyMapper.setKey(VirtualKey.UP, Input.Keys.D);
        secondKeyMapper.setKey(VirtualKey.DOWN, Input.Keys.C);
        secondKeyMapper.setKey(VirtualKey.TRIGGER, Input.Keys.CONTROL_LEFT);

        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new MultiPlayerScreen(mGame, mListener));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/multiplayer.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        createVehicleSelector(builder, assets, 0);
        createVehicleSelector(builder, assets, 1);

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

    private void createVehicleSelector(UiBuilder builder, Assets assets, int idx) {
        GameConfig gameConfig = mGame.getConfig();
        String vehicleId = gameConfig.vehicles[idx];

        Menu menu = builder.getActor("menu" + String.valueOf(idx + 1));

        final Label readyLabel = builder.getActor("ready" + String.valueOf(idx + 1));

        VehicleSelector selector = new VehicleSelector(menu);
        mVehicleSelectors[idx] = selector;
        selector.init(assets);
        selector.setCurrent(assets.findVehicleDefById(vehicleId));
        selector.addListener(new MenuItemListener() {
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
        Array<GameInfo.Player> players = new Array<GameInfo.Player>();
        for (int idx = 0; idx < Constants.MAX_PLAYERS; ++idx) {
            String id = mVehicleSelectors[idx].getSelectedId();
            String inputHandlerId = mGame.getConfig().inputs[idx];
            GameInputHandlerFactory factory = GameInputHandlerFactories.getFactoryById(inputHandlerId);
            players.add(new GameInfo.Player(idx, id, factory.create()));
        }

        mListener.onPlayersSelected(players);
    }
}
