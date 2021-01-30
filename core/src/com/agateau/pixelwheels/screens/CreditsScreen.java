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

import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.ui.CreditsScrollPane;
import com.agateau.ui.LimitedMarkdownParser;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

class CreditsScreen extends PwStageScreen {
    private final PwGame mGame;

    CreditsScreen(PwGame game) {
        super(game.getAssets().ui);
        mGame = game;
        setupUi();
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new CreditsScreen(mGame));
            }
        };
    }

    private void setupUi() {
        Skin skin = mGame.getAssets().ui.skin;
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, skin);
        builder.registerActorFactory(
                "CreditsScrollPane", (uiBuilder, element) -> new CreditsScrollPane());

        AnchorGroup root = (AnchorGroup) builder.build(FileUtils.assets("screens/credits.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        final CreditsScrollPane pane = builder.getActor("creditsScrollPane");
        float stageHeight = getStage().getHeight();
        VerticalGroup group = pane.getGroup();
        addSpacer(group, stageHeight);
        LimitedMarkdownParser.createActors(group, mGame.getAssets().ui.skin, loadCredits());
        addSpacer(group, stageHeight * 2 / 3);
        LimitedMarkdownParser.createActors(
                group, mGame.getAssets().ui.skin, "Thank you for playing!");
        addSpacer(group, stageHeight / 2);

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });
    }

    private void addSpacer(VerticalGroup group, float height) {
        Actor spacer = new Actor();
        spacer.setSize(1, height);
        group.addActor(spacer);
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }

    private String loadCredits() {
        FileHandle handle = FileUtils.assets("credits.md");
        return handle.readString("utf-8");
    }
}
