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
package com.agateau.pixelwheels.racescreen;

import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/** Appears on top of RaceScreen when paused */
public class PauseOverlay extends Overlay {
    private final PwGame mGame;
    private final RaceScreen mRaceScreen;

    public PauseOverlay(PwGame game, RaceScreen raceScreen) {
        super(game.getAssets().dot);
        mGame = game;
        mRaceScreen = raceScreen;
        new PwRefreshHelper(mGame, this) {
            @Override
            protected void refresh() {
                setContent(createContent());
            }
        };
        setContent(createContent());
    }

    private Actor createContent() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);
        if (mRaceScreen.getPauseButtons() == RaceScreen.PauseButtons.ALL) {
            builder.defineVariable("showRestartButton");
        }
        Actor content = builder.build(FileUtils.assets("screens/pauseoverlay.gdxui"));

        builder.getActor("resumeButton")
                .addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                mRaceScreen.resumeRace();
                            }
                        });
        if (mRaceScreen.getPauseButtons() == RaceScreen.PauseButtons.ALL) {
            builder.getActor("restartButton")
                    .addListener(
                            new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    mRaceScreen.onRestartPressed();
                                }
                            });
        }
        builder.getActor("quitButton")
                .addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                mRaceScreen.onQuitPressed();
                            }
                        });
        builder.getActor("settingsButton")
                .addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                mRaceScreen.onSettingsPressed();
                            }
                        });
        return content;
    }
}
