/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.rewards.Reward;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UnlockedRewardScreen extends NavStageScreen {
    private final PwGame mGame;
    private final Reward mReward;
    private final NextListener mNextListener;

    public UnlockedRewardScreen(PwGame game, Reward reward, NextListener nextListener) {
        super(game.getAssets().ui);
        mGame = game;
        mReward = reward;
        mNextListener = nextListener;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new UnlockedRewardScreen(mGame, mReward, mNextListener));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);

        AnchorGroup root = (AnchorGroup) builder.build(FileUtils.assets("screens/unlockedreward.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        setupNextButton((Button)builder.getActor("nextButton"));
        setNavListener(mNextListener);

        switch (mReward.category) {
            case VEHICLE:
                setupVehicleReward(builder, mReward.id);
                break;
            case CHAMPIONSHIP:
                setupChampionshipReward(builder, mReward.id);
                break;
        }
    }

    private void setupVehicleReward(UiBuilder builder, String id) {
        VehicleDef vehicleDef = mGame.getAssets().findVehicleDefById(id);

        setupRewardDetails(builder, "New vehicle unlocked!",
                mGame.getAssets().getVehicleRegion(vehicleDef),
                vehicleDef.name);
    }

    private void setupChampionshipReward(UiBuilder builder, String id) {
        Championship championship = mGame.getAssets().findChampionshipById(id);

        setupRewardDetails(builder, "New championship unlocked!",
                mGame.getAssets().getChampionshipRegion(championship),
                championship.getName());
    }

    private void setupRewardDetails(UiBuilder builder, String title, TextureRegion rewardRegion, String rewardName) {
        Label titleLabel = builder.getActor("titleLabel");
        Image rewardImage = builder.getActor("rewardImage");
        Label rewardLabel = builder.getActor("rewardLabel");

        titleLabel.setText(title);
        titleLabel.pack();

        rewardImage.setDrawable(new TextureRegionDrawable(rewardRegion));
        rewardImage.pack();

        rewardLabel.setText(rewardName);
        rewardLabel.pack();
    }
}
