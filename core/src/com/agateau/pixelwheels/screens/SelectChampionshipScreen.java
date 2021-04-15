/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.map.Track;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/** Select the championship */
public class SelectChampionshipScreen extends PwStageScreen {
    public interface Listener {
        void onBackPressed();

        void onChampionshipSelected(Championship championship);
    }

    private final PwGame mGame;
    private final Listener mListener;
    private Label mChampionshipNameLabel;
    private Label mChampionshipDetailsLabel;
    private ChampionshipSelector mChampionshipSelector;
    private Button mNextButton;

    public SelectChampionshipScreen(PwGame game, Listener listener, final String championshipId) {
        super(game.getAssets().ui);
        mGame = game;
        mListener = listener;
        Championship championship = findChampionship(championshipId);
        setupUi(championship);
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectChampionshipScreen(mGame, mListener, championshipId));
            }
        };
    }

    private Championship findChampionship(String championshipId) {
        Championship championship = mGame.getAssets().findChampionshipById(championshipId);
        if (championship == null) {
            championship = mGame.getAssets().championships.get(0);
        }
        return championship;
    }

    private void setupUi(Championship championship) {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);

        AnchorGroup root =
                (AnchorGroup) builder.build(FileUtils.assets("screens/selectchampionship.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mChampionshipNameLabel = builder.getActor("championshipNameLabel");
        mChampionshipDetailsLabel = builder.getActor("championshipDetailsLabel");

        Menu menu = builder.getActor("menu");

        createChampionshipSelector(championship, menu);
        updateChampionshipDetails(championship);

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });

        mNextButton = builder.getActor("nextButton");
        mNextButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        next();
                    }
                });
        updateNextButton();
    }

    private void updateNextButton() {
        mNextButton.setDisabled(!mChampionshipSelector.isCurrentItemEnabled());
    }

    private void createChampionshipSelector(Championship championship, Menu menu) {
        Assets assets = mGame.getAssets();
        mChampionshipSelector = new ChampionshipSelector(menu);
        mChampionshipSelector.setColumnCount(2);
        mChampionshipSelector.init(assets, mGame.getRewardManager());
        mChampionshipSelector.setCurrent(championship);
        menu.addItem(mChampionshipSelector);

        mChampionshipSelector.setSelectionListener(
                new GridMenuItem.SelectionListener<Championship>() {
                    @Override
                    public void currentChanged(Championship championship, int index) {
                        updateChampionshipDetails(championship);
                        updateNextButton();
                    }

                    @Override
                    public void selectionConfirmed() {
                        next();
                    }
                });
    }

    private final StringBuilder mStringBuilder = new StringBuilder();

    private void updateChampionshipDetails(Championship championship) {
        if (mGame.getRewardManager().isChampionshipUnlocked(championship)) {
            mChampionshipNameLabel.setText(championship.getName());

            mStringBuilder.setLength(0);
            boolean first = true;
            for (Track track : championship.getTracks()) {
                if (first) {
                    first = false;
                } else {
                    mStringBuilder.append('\n');
                }
                mStringBuilder.append(track.getMapName());
            }
            mChampionshipDetailsLabel.setText(mStringBuilder.toString());
        } else {
            mChampionshipNameLabel.setText("[Locked]");
            mChampionshipDetailsLabel.setText(mGame.getRewardManager().getUnlockText(championship));
        }
        mChampionshipNameLabel.pack();
        mChampionshipDetailsLabel.pack();
    }

    @Override
    public void onBackPressed() {
        mListener.onBackPressed();
    }

    private void next() {
        if (!mChampionshipSelector.isCurrentItemEnabled()) {
            return;
        }
        mListener.onChampionshipSelected(mChampionshipSelector.getCurrent());
    }
}
