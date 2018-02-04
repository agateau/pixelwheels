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
package com.agateau.tinywheels.racescreen;

import com.agateau.tinywheels.GameInfo;
import com.agateau.tinywheels.GameWorld;
import com.agateau.tinywheels.Maestro;
import com.agateau.tinywheels.TwGame;
import com.agateau.tinywheels.screens.TwStageScreen;
import com.agateau.tinywheels.debug.Debug;
import com.agateau.tinywheels.debug.DebugShapeMap;
import com.agateau.tinywheels.gameinput.GameInputHandlerFactories;
import com.agateau.tinywheels.racer.RacerDebugShape;
import com.agateau.tinywheels.gameobjet.GameObject;
import com.agateau.tinywheels.gameobjet.AudioClipper;
import com.agateau.tinywheels.racer.Pilot;
import com.agateau.tinywheels.racer.PlayerPilot;
import com.agateau.tinywheels.racer.Racer;
import com.agateau.tinywheels.racer.Vehicle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class RaceScreen extends ScreenAdapter {
    private final TwGame mGame;
    private final Maestro mMaestro;
    private final GameWorld mGameWorld;
    private final Color mBackgroundColor;

    private Array<GameRenderer> mGameRenderers = new Array<GameRenderer>();
    private final AudioClipper mAudioClipper;

    private Array<Hud> mHuds = new Array<Hud>();
    private Array<HudContent> mHudContents = new Array<HudContent>();
    private ScreenViewport mHudViewport = new ScreenViewport();
    private final Stage mHudStage;

    private final PerformanceCounters mPerformanceCounters = new PerformanceCounters();
    private PerformanceCounter mGameWorldPerformanceCounter;
    private PerformanceCounter mRendererPerformanceCounter;
    private PerformanceCounter mOverallPerformanceCounter;
    private PauseOverlay mPauseOverlay = null;

    public RaceScreen(TwGame game, Maestro maestro, GameInfo gameInfo) {
        mGame = game;
        mMaestro = maestro;
        SpriteBatch batch = new SpriteBatch();
        mOverallPerformanceCounter = mPerformanceCounters.add("All");
        mGameWorldPerformanceCounter = mPerformanceCounters.add("GameWorld.act");
        mGameWorld = new GameWorld(game, gameInfo, mPerformanceCounters);
        mBackgroundColor = gameInfo.mapInfo.getBackgroundColor();
        mRendererPerformanceCounter = mPerformanceCounters.add("Renderer");

        mHudStage = new Stage(mHudViewport, batch);
        mHudStage.setDebugAll(Debug.instance.showHudDebugLines);

        for (int idx = 0; idx < gameInfo.getPlayers().size; ++idx) {
            Vehicle vehicle = mGameWorld.getPlayerVehicle(idx);
            GameRenderer gameRenderer = new GameRenderer(mGameWorld, vehicle, batch, mPerformanceCounters);
            setupGameRenderer(gameRenderer);

            Hud hud = new Hud(game.getAssets(), mHudStage);
            HudContent hudContent = setupHudContent(hud, idx);
            Racer racer = mGameWorld.getPlayerRacer(idx);
            if (Debug.instance.showDebugLayer) {
                DebugShapeMap.getMap().put("racer" + String.valueOf(idx), new RacerDebugShape(racer, gameInfo.mapInfo));
            }
            Pilot pilot = racer.getPilot();
            if (pilot instanceof PlayerPilot) {
                ((PlayerPilot) pilot).createHudButtons(hud);
            }

            mGameRenderers.add(gameRenderer);
            mHuds.add(hud);
            mHudContents.add(hudContent);
        }

        mAudioClipper = new AudioClipper() {
            private final static float MAX_DISTANCE = 15;
            @Override
            public float clip(GameObject gameObject) {
                float distance2 = MAX_DISTANCE * MAX_DISTANCE;
                for (Racer racer : mGameWorld.getPlayerRacers()) {
                    float dx = racer.getX() - gameObject.getX();
                    float dy = racer.getY() - gameObject.getY();
                    float d2 = dx * dx + dy * dy;
                    distance2 = Math.min(d2, distance2);
                }
                return 1f - (float)Math.sqrt(distance2) / MAX_DISTANCE;
            }
        };
    }

    private void setupGameRenderer(GameRenderer gameRenderer) {
        gameRenderer.setConfig(mGame.getConfig());
    }

    private HudContent setupHudContent(Hud hud, int idx) {
        HudContent hudContent = new HudContent(mGame.getAssets(), mGameWorld, hud, idx);
        if (idx == 0) {
            if (Debug.instance.showDebugHud) {
                hudContent.setPerformanceCounters(mPerformanceCounters);
            }
            if (GameInputHandlerFactories.hasMultitouch()) {
                hudContent.createPauseButton(new ClickListener() {
                    public void clicked(InputEvent event, float x, float y) {
                        pauseRace();
                    }
                });
            }
        }
        return hudContent;
    }

    @Override
    public void render(float delta) {
        boolean paused = mPauseOverlay != null;

        mOverallPerformanceCounter.start();
        mGameWorldPerformanceCounter.start();
        if (!paused) {
            GameWorld.State oldState = mGameWorld.getState();
            mGameWorld.act(delta);
            GameWorld.State newState = mGameWorld.getState();
            if (oldState != newState) {
                onFinished();
            }
        }
        mGameWorldPerformanceCounter.stop();

        for (HudContent hudContent : mHudContents) {
            hudContent.act(delta);
        }
        mHudStage.act(delta);

        mRendererPerformanceCounter.start();
        Gdx.gl.glClearColor(mBackgroundColor.r, mBackgroundColor.g, mBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for (GameRenderer gameRenderer : mGameRenderers) {
            gameRenderer.render(delta);
        }

        for (GameObject gameObject : mGameWorld.getActiveGameObjects()) {
            gameObject.audioRender(mAudioClipper);
        }

        if (isPauseKeyPressed()) {
            if (paused) {
                resumeRace();
            } else {
                pauseRace();
            }
        }

        mHudViewport.apply(true);
        mHudStage.draw();

        mRendererPerformanceCounter.stop();

        mOverallPerformanceCounter.stop();
        if (!paused) {
            mPerformanceCounters.tick(delta);
        }
    }

    private boolean isPauseKeyPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.P) ||
                Gdx.input.isKeyJustPressed(Input.Keys.BACK);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        float upp = Math.max((float)(TwStageScreen.WIDTH) / width, (float)(TwStageScreen.HEIGHT) / height);
        mHudViewport.setUnitsPerPixel(upp);
        int viewportWidth = width / mGameRenderers.size;
        int x = 0;
        for (int idx = 0; idx < mGameRenderers.size; ++idx) {
            mGameRenderers.get(idx).setScreenRect(x, 0, viewportWidth, height);
            mHuds.get(idx).setScreenRect((int)(x * upp), 0, (int)(viewportWidth * upp), (int)(height * upp));
            x += viewportWidth;
        }
        mHudViewport.update(width, height, true);
    }

    private void onFinished() {
        for (Racer racer : mGameWorld.getRacers()) {
            racer.markRaceFinished();
        }
        FinishedOverlay overlay = new FinishedOverlay(mGame, mMaestro, mGameWorld.getRacers(), mGameWorld.getPlayerRacers());
        mHudStage.addActor(overlay);
    }

    private void pauseRace() {
        mPauseOverlay = new PauseOverlay(mGame, mMaestro, this);
        mHudStage.addActor(mPauseOverlay);
    }

    public void resumeRace() {
        mPauseOverlay.remove();
        mPauseOverlay = null;
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(mHudStage);
    }

    @Override
    public void dispose() {
        super.dispose();
        mGameWorld.dispose();
    }

    public void forgetMapInfo() {
        // HACK
        // This is a hack to work around a dispose() issue.
        // When the player restarts a race, the following events happen:
        //
        // 1. New RaceScreen is created,
        // 2. The new RaceScreen creates a GameWorld
        // 3. The new GameWorld calls MapInfo.init()
        // 4. RaceScreen is set to replace the current screen
        // 5. TwGame.replaceScreen() calls dispose() on the old screen
        // 6. The old screen calls dispose() on its GameWorld
        // 7. The old GameWorld  calls dispose() on its MapInfo
        // 8. Since the MapInfo of the old GameWorld is the same as the
        //    MapInfo of the new GameWorld, the MapInfo of the new
        //    GameWorld has now been disposed.
        //
        // Asking GameWorld to "forget" its MapINfo causes it to reset its
        // MapInfo pointer, so it won't dispose it on step #7
        //
        // This is only necessary when restarting a race because it is the
        // only case where MapInfo.dispose() is called *after* the same
        // MapInfo instance has been inited.
        mGameWorld.forgetMapInfo();
    }
}
