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
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.racer.Vehicle;
import com.agateau.pixelwheels.utils.UiUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.utils.FileUtils;
import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Select your vehicle
 */
public class SelectVehicleScreen extends PwStageScreen {
    public interface Listener {
        void onBackPressed();
        void onPlayerSelected(GameInfo.Player player);
    }
    private final PwGame mGame;
    private final Listener mListener;
    private VehicleSelector mVehicleSelector;
    private Label mVehicleNameLabel;

    public SelectVehicleScreen(PwGame game, Listener listener) {
        super(game.getAssets().ui);
        mGame = game;
        mListener = listener;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectVehicleScreen(mGame, mListener));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = UiUtils.createUiBuilder(assets);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selectvehicle.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");
        mVehicleNameLabel = builder.getActor("vehicleNameLabel");

        createVehicleSelector(menu);
        updateVehicleDetails(mVehicleSelector.getCurrent());

        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onBackPressed();
            }
        });

        builder.getActor("nextButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                next();
            }
        });
    }

    private void createVehicleSelector(Menu menu) {
        Assets assets = mGame.getAssets();
        mVehicleSelector = new VehicleSelector(menu);
        mVehicleSelector.init(assets);
        String id = mGame.getConfig().vehicles[0];
        mVehicleSelector.setCurrent(assets.findVehicleDefById(id));
        menu.addItem(mVehicleSelector);

        mVehicleSelector.setSelectionListener(new GridMenuItem.SelectionListener<VehicleDef>() {
            @Override
            public void selectedChanged(VehicleDef item, int index) {
                if (PlatformUtils.isButtonsUi()) {
                    next();
                }
            }

            @Override
            public void currentChanged(VehicleDef vehicle, int index) {
                updateVehicleDetails(vehicle);
            }
        });
    }

    private void updateVehicleDetails(VehicleDef vehicle) {
        mVehicleNameLabel.setText(vehicle.name);
        mVehicleNameLabel.pack();
    }

    @Override
    public void onBackPressed() {
        mListener.onBackPressed();
    }

    private void next() {
        String vehicleId = mVehicleSelector.getSelectedId();
        GameInfo.Player player = new GameInfo.Player(0, vehicleId);
        mListener.onPlayerSelected(player);
    }
}
