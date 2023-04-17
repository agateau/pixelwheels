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

import static com.agateau.translations.Translator.trc;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.gameinput.GameInputHandler;
import com.agateau.pixelwheels.gameinput.InputMapperInputHandler;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.InputMapper;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/** Select player vehicles */
public class MultiPlayerScreen extends PwStageScreen {
    public interface Listener {
        void onBackPressed();

        void onPlayersSelected(Array<GameInfo.Player> players);
    }

    private final PwGame mGame;
    private final int mPlayerCount = 2; // Hardcoded for now
    private final Listener mListener;
    private final InputMapper[] mInputMappers;
    private VehicleSelector mVehicleSelector;
    private final Array<Label> mReadyLabels = new Array<>();

    public MultiPlayerScreen(PwGame game, Listener listener) {
        super(game.getAssets().ui);
        mGame = game;
        mListener = listener;

        mInputMappers = new InputMapper[mPlayerCount];

        for (int idx = 0; idx < mPlayerCount; ++idx) {
            GameInputHandler inputHandler = mGame.getConfig().getPlayerInputHandler(idx);
            InputMapper inputMapper = ((InputMapperInputHandler) inputHandler).getInputMapper();
            mInputMappers[idx] = inputMapper;
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

        mVehicleSelector = createVehicleSelector(builder, assets);
        createReadyLabels(builder, assets);

        for (int idx = 0; idx < mPlayerCount; ++idx) {
            setupCursor(assets, idx);
        }

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });
    }

    private void createReadyLabels(UiBuilder builder, Assets assets) {
        Skin skin = assets.ui.skin;
        HorizontalGroup group = builder.getActor("readyGroup");

        mReadyLabels.clear();
        for (int idx = 0; idx < mPlayerCount; ++idx) {
            String style = StringUtils.format("player%d", idx + 1);
            Label label = new Label("", skin, style);
            group.addActor(label);
            mReadyLabels.add(label);
            setReadyLabelText(idx, null);
        }
    }

    private void setReadyLabelText(int idx, String name) {
        Label label = mReadyLabels.get(idx);
        String textTemplate = trc("P%d: %s", "The 'P' is for 'Player'");
        String text = StringUtils.format(textTemplate, idx + 1, name == null ? "..." : name);
        label.setText(text);
        WidgetGroup group = (WidgetGroup) label.getParent();
        group.pack();
    }

    @Override
    public void onBackPressed() {
        mListener.onBackPressed();
    }

    private VehicleSelector createVehicleSelector(UiBuilder builder, Assets assets) {
        Menu menu = builder.getActor("menu");

        VehicleSelector selector = new VehicleSelector(menu);
        selector.init(assets, mGame.getRewardManager());
        selector.setColumnCount(builder.getIntConfigValue("columnCount"));
        selector.setItemSize(
                builder.getIntConfigValue("itemWidth"), builder.getIntConfigValue("itemHeight"));
        menu.addItem(selector);

        return selector;
    }

    private void setupCursor(Assets assets, int idx) {
        if (idx > 0) {
            // For idx 0, we use the existing cursor
            mVehicleSelector.addCursor();
            mVehicleSelector.setInputMapper(idx, mInputMappers[idx]);
        }

        Menu.MenuStyle menuStyle = assets.ui.skin.get("player" + (idx + 1), Menu.MenuStyle.class);
        mVehicleSelector.setMenuStyle(idx, menuStyle);

        String vehicleId = mGame.getConfig().vehicles[idx];
        VehicleDef vehicleDef = assets.findVehicleDefById(vehicleId);
        mVehicleSelector.setCurrent(idx, vehicleDef);

        mVehicleSelector.setListener(
                idx,
                new MenuItemListener() {
                    @Override
                    public void triggered() {
                        VehicleDef vehicleDef = mVehicleSelector.getSelected(idx);
                        setReadyLabelText(idx, vehicleDef.getName());
                        nextIfPossible();
                    }
                });
    }

    private void nextIfPossible() {
        for (int idx = 0; idx < mPlayerCount; ++idx) {
            if (mVehicleSelector.getSelected(idx) == null) {
                return;
            }
        }
        next();
    }

    private void next() {
        Array<GameInfo.Player> players = new Array<>();
        for (int idx = 0; idx < mPlayerCount; ++idx) {
            VehicleDef vehicleDef = mVehicleSelector.getSelected(idx);
            if (vehicleDef == null) {
                return;
            }
            players.add(new GameInfo.Player(idx, vehicleDef.id));
        }
        mListener.onPlayersSelected(players);
    }
}
