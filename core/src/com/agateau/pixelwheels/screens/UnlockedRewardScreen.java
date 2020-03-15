/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.rewards.Reward;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

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
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new UnlockedRewardScreen(mGame, mReward, mNextListener));
            }
        };
    }

    public static UnlockedRewardScreen createDebugScreen(PwGame game) {
        String[] tokens = Constants.DEBUG_SCREEN.split(":");
        Assert.check(tokens.length == 2, "Expected one `:` in PW_DEBUG_SCREEN");
        Reward reward;
        String id = tokens[1];
        switch (tokens[0]) {
            case "UnlockedVehicle":
                reward = Reward.get(game.getAssets().findVehicleDefById(id));
                break;
            case "UnlockedChampionship":
                reward = Reward.get(game.getAssets().findChampionshipById(id));
                break;
            default:
                throw new RuntimeException("Invalid value for reward type");
        }
        return new UnlockedRewardScreen(
                game,
                reward,
                new NavStageScreen.NextListener() {
                    @Override
                    public void onNextPressed() {}
                });
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        boolean isVehicle = mReward.prize instanceof VehicleDef;
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);
        VehicleActor.register(builder, assets);
        if (isVehicle) {
            builder.defineVariable("vehicle");
        }

        AnchorGroup root =
                (AnchorGroup) builder.build(FileUtils.assets("screens/unlockedreward.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        setupNextButton(builder.getActor("nextButton"));
        setNavListener(mNextListener);

        if (isVehicle) {
            setupVehicleReward(builder, (VehicleDef) (mReward.prize));
        } else if (mReward.prize instanceof Championship) {
            setupChampionshipReward(builder, (Championship) (mReward.prize));
        } else {
            throw new RuntimeException("Don't know how to show reward for " + mReward.prize);
        }
    }

    private void setupVehicleReward(UiBuilder builder, VehicleDef vehicleDef) {
        VehicleActor vehicle = builder.getActor("vehicle");
        vehicle.setVehicleDef(vehicleDef);
        setupRewardDetails(builder, "New vehicle unlocked!", vehicleDef.name);
    }

    private void setupChampionshipReward(UiBuilder builder, Championship championship) {
        Image image = builder.getActor("championshipImage");
        image.setDrawable(
                new TextureRegionDrawable(mGame.getAssets().getChampionshipRegion(championship)));
        image.pack();
        image.setOrigin(Align.center);
        setupRewardDetails(builder, "New championship unlocked!", championship.getName());
    }

    private void setupRewardDetails(UiBuilder builder, String title, String rewardName) {
        Label titleLabel = builder.getActor("titleLabel");
        Label rewardLabel = builder.getActor("rewardLabel");

        titleLabel.setText(title);
        titleLabel.pack();

        rewardLabel.setText(rewardName);
        rewardLabel.pack();
    }
}
