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

import static com.agateau.translations.Translator.tr;

import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.ui.CreditsScrollPane;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader;

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
                "CreditsScrollPane",
                (uiBuilder, element) -> {
                    ScrollPane pane = new CreditsScrollPane();
                    Actor child = uiBuilder.buildChildren(element, null);
                    if (child != null) {
                        pane.setActor(child);
                    }
                    return pane;
                });

        AnchorGroup root = (AnchorGroup) builder.build(loadCreditsXml());
        root.setFillParent(true);
        getStage().addActor(root);

        final CreditsScrollPane pane = builder.getActor("creditsScrollPane");
        float stageHeight = getStage().getHeight();

        VerticalGroup group = pane.getGroup();
        group.addActorAt(0, createSpacer(stageHeight));
        addSpacer(group, stageHeight * 2 / 3);
        group.addActor(new Label(tr("Thank you for playing!"), mGame.getAssets().ui.skin));
        addSpacer(group, stageHeight / 2);

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });

        for (Actor actor : group.getChildren()) {
            if (actor instanceof Label) {
                Label label = (Label) actor;
                label.setAlignment(Align.center);
                label.setWrap(true);
                label.setWidth(group.getWidth());
                label.setHeight(label.getPrefHeight());
            }
        }
    }

    /**
     * Post-process the gdxui XML to add some fancy ascii-art decorations around the text of the
     * section headers.
     *
     * <p>Doing it this way reduces duplication and, more importantly, ensures translators do not
     * have to care about the decorations.
     */
    private XmlReader.Element loadCreditsXml() {
        FileHandle handle = FileUtils.assets("screens/credits.gdxui");
        XmlReader.Element root = FileUtils.parseXml(handle);
        for (XmlReader.Element element : root.getChildrenByNameRecursively("Label")) {
            if (element.getAttribute("style", "").equals("creditsSection")) {
                element.setText(String.format("-= %s =-", tr(element.getText())));
            }
        }
        return root;
    }

    private void addSpacer(VerticalGroup group, float height) {
        group.addActor(createSpacer(height));
    }

    private Actor createSpacer(float height) {
        Actor spacer = new Actor();
        spacer.setSize(1, height);
        return spacer;
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }
}
