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

import com.agateau.pixelwheels.GamePlay;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.debug.Debug;
import com.agateau.pixelwheels.debug.DebugShapeMap;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactories;
import com.agateau.pixelwheels.gameobjet.AudioClipper;
import com.agateau.pixelwheels.gameobjet.GameObject;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.racer.Pilot;
import com.agateau.pixelwheels.racer.PlayerPilot;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.RacerDebugShape;
import com.agateau.pixelwheels.screens.ConfigScreen;
import com.agateau.pixelwheels.screens.PwStageScreen;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class RaceScreen extends ScreenAdapter {
    public interface Listener {
        void onRestartPressed();

        void onQuitPressed();

        void onNextTrackPressed();
    }

    private final PwGame mGame;
    private final Listener mListener;
    private final GameInfo mGameInfo;

    private final GameWorldImpl mGameWorld;
    private final Color mBackgroundColor;

    private final GameRenderer mGameRenderer;
    private final AudioClipper mAudioClipper;

    private Hud mHud;
    private HudContent mHudContent;
    private final ScreenViewport mHudViewport = new ScreenViewport();
    private final Stage mHudStage;

    private final PerformanceCounters mPerformanceCounters = new PerformanceCounters();
    private final PerformanceCounter mGameWorldPerformanceCounter;
    private final PerformanceCounter mRendererPerformanceCounter;
    private final PerformanceCounter mOverallPerformanceCounter;
    private PauseOverlay mPauseOverlay = null;

    private boolean mFirstRender = true;
    private boolean mConfigVisible = false;

    public RaceScreen(PwGame game, Listener listener, GameInfo gameInfo) {
        NLog.i("Starting race on %s", gameInfo.getTrack().getMapName());
        mGame = game;
        mListener = listener;
        mGameInfo = gameInfo;

        DebugShapeMap.clear();

        mOverallPerformanceCounter = mPerformanceCounters.add("All");
        mGameWorldPerformanceCounter = mPerformanceCounters.add("GameWorld.act");
        mGameWorld = new GameWorldImpl(game, gameInfo, mPerformanceCounters);
        mBackgroundColor = gameInfo.getTrack().getBackgroundColor();
        mRendererPerformanceCounter = mPerformanceCounters.add("Renderer");

        SpriteBatch batch = new SpriteBatch();
        mHudStage = new Stage(mHudViewport, batch);
        mHudStage.setDebugAll(Debug.instance.showHudDebugLines);

        mGameRenderer = new GameRenderer(mGameWorld, batch, mPerformanceCounters);
        setupHud(mGameWorld.getTrack());

        mAudioClipper = createAudioClipper();

        setupMineDropper();
    }

    private void startMusic() {
        String musicId = mGame.getAssets().getTrackMusicId(mGameInfo.getTrack());
        mGame.getAudioManager().playMusic(musicId);
    }

    private void setupMineDropper() {
        // Bind the mine dropper to the free camera for now
        if (GamePlay.instance.freeCamera) {
            mGameWorld.addGameObject(new MineDropper(mGame, mGameWorld, mGameRenderer));
        }
    }

    private void setupHud(Track track) {
        mHud = new Hud(mGame.getAssets(), mHudStage);
        mHudContent = new HudContent(mGame.getAssets(), mGameWorld, mHud);

        if (Debug.instance.showDebugLayer) {
            int idx = 0;
            for (Racer racer : mGameWorld.getRacers()) {
                DebugShapeMap.put("racer" + idx, new RacerDebugShape(racer, track));
                ++idx;
            }
        }

        if (Debug.instance.showDebugHud) {
            mHudContent.setPerformanceCounters(mPerformanceCounters);
        }

        if (GameInputHandlerFactories.hasMultitouch()) {
            mHudContent.createPauseButton(
                    new ClickListener() {
                        public void clicked(InputEvent event, float x, float y) {
                            pauseRace();
                        }
                    });
        }

        createInputUi();
    }

    private void createInputUi() {
        // Touch screen is single player only, so it's fine to only do this for the first player
        Racer racer = mGameWorld.getPlayerRacer(0);
        Pilot pilot = racer.getPilot();
        if (pilot instanceof PlayerPilot) {
            ((PlayerPilot) pilot).createHudButtons(mHud);
        }
    }

    private AudioClipper createAudioClipper() {
        return gameObject -> {
            float maxDistance = GamePlay.instance.viewportWidth;
            float distance2 = maxDistance * maxDistance;
            for (Racer racer : mGameWorld.getPlayerRacers()) {
                float dx = racer.getX() - gameObject.getX();
                float dy = racer.getY() - gameObject.getY();
                float d2 = dx * dx + dy * dy;
                distance2 = Math.min(d2, distance2);
            }
            return 1f - (float) Math.sqrt(distance2) / maxDistance;
        };
    }

    @Override
    public void render(float delta) {
        if (mFirstRender) {
            mGameRenderer.onAboutToStart();
            // Fadeout main music, we start the track music after the count down
            mGame.getAudioManager().fadeOutMusic();
            mFirstRender = false;
        }
        boolean paused = mPauseOverlay != null;

        mOverallPerformanceCounter.start();
        mGameWorldPerformanceCounter.start();
        if (!paused) {
            GameWorld.State oldState = mGameWorld.getState();
            mGameWorld.act(delta);
            GameWorld.State newState = mGameWorld.getState();
            if (oldState != newState) {
                if (newState == GameWorld.State.FINISHED) {
                    onFinished();
                }
                if (newState == GameWorld.State.RUNNING) {
                    // Count down just finished
                    startMusic();
                }
            }
        }
        mGameWorldPerformanceCounter.stop();

        mRendererPerformanceCounter.start();
        Gdx.gl.glClearColor(mBackgroundColor.r, mBackgroundColor.g, mBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mGameRenderer.render(delta);

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

        mRendererPerformanceCounter.stop();

        // Process hud *after* rendering game so that if an action on the hud (called from
        // mHudStage.act()) causes us to leave this screen (back to menu from pause, or leaving
        // the FinishedOverlay) then the game renderer does not alter the OpenGL viewport *after*
        // we have changed screens.
        mHudContent.act(delta);
        mHudViewport.apply(true);
        mHudStage.draw();
        mHudStage.act(delta);

        mOverallPerformanceCounter.stop();
        if (!paused) {
            mPerformanceCounters.tick(delta);
        }
    }

    private boolean isPauseKeyPressed() {
        for (Racer racer : mGameWorld.getPlayerRacers()) {
            PlayerPilot pilot = (PlayerPilot) racer.getPilot();
            if (pilot.isPauseKeyPressed()) {
                return true;
            }
        }
        return Gdx.input.isKeyJustPressed(Input.Keys.P)
                || Gdx.input.isKeyJustPressed(Input.Keys.BACK);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        float upp = PwStageScreen.getUnitsPerPixel();
        mHudViewport.setUnitsPerPixel(upp);
        mGameRenderer.setScreenRect(0, 0, width, height);
        mHud.setScreenRect(0, 0, (int) (width * upp), (int) (height * upp));
        mHudViewport.update(width, height, true);
    }

    private void onFinished() {
        FinishedOverlay overlay = new FinishedOverlay(mGame, this, mGameWorld.getRacers());
        mHudStage.addActor(overlay);
    }

    private void pauseRace() {
        if (mGameWorld.getState() == GameWorld.State.FINISHED) {
            return;
        }
        mGame.getAudioManager().setSoundFxMuted(true);
        mPauseOverlay = new PauseOverlay(mGame, this);
        mHudStage.addActor(mPauseOverlay);
    }

    public void resumeRace() {
        mPauseOverlay.remove();
        mPauseOverlay = null;
        unmuteIfNecessary();
    }

    void onRestartPressed() {
        unmuteIfNecessary();
        mListener.onRestartPressed();
    }

    void onQuitPressed() {
        mListener.onQuitPressed();
    }

    void onSettingsPressed() {
        mConfigVisible = true;
        mGame.pushScreen(new ConfigScreen(mGame));
    }

    private void unmuteIfNecessary() {
        mGame.getAudioManager().setSoundFxMuted(!mGame.getConfig().playSoundFx);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(mHudStage);
        if (mConfigVisible) {
            // We are back from the config screen
            mConfigVisible = false;
            createInputUi();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        mGameWorld.dispose();
    }

    public void forgetTrack() {
        // HACK
        // This is a hack to work around a dispose() issue.
        // When the player restarts a race, the following events happen:
        //
        // 1. New RaceScreen is created,
        // 2. The new RaceScreen creates a GameWorld
        // 3. The new GameWorld calls Track.init()
        // 4. RaceScreen is set to replace the current screen
        // 5. PwGame.replaceScreen() calls dispose() on the old screen
        // 6. The old screen calls dispose() on its GameWorld
        // 7. The old GameWorld  calls dispose() on its Track
        // 8. Since the Track of the old GameWorld is the same as the
        //    Track of the new GameWorld, the Track of the new
        //    GameWorld has now been disposed.
        //
        // Asking GameWorld to "forget" its track causes it to reset its
        // Track pointer, so it won't dispose it on step #7
        //
        // This is only necessary when restarting a race because it is the
        // only case where Track.dispose() is called *after* the same
        // Track instance has been inited.
        mGameWorld.forgetTrack();
    }

    GameInfo.GameType getGameType() {
        return mGameInfo.getGameType();
    }

    public Listener getListener() {
        return mListener;
    }
}
