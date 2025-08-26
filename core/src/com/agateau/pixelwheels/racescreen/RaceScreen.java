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
import com.agateau.pixelwheels.gameobject.AudioClipper;
import com.agateau.pixelwheels.gameobject.GameObject;
import com.agateau.pixelwheels.gamesetup.GameInfo;
import com.agateau.pixelwheels.map.Track;
import com.agateau.pixelwheels.racer.Pilot;
import com.agateau.pixelwheels.racer.PlayerPilot;
import com.agateau.pixelwheels.racer.Racer;
import com.agateau.pixelwheels.racer.RacerDebugShape;
import com.agateau.pixelwheels.racescreen.debug.DropLocationDebugObject;
import com.agateau.pixelwheels.racescreen.debug.MineDropper;
import com.agateau.pixelwheels.screens.ConfigScreen;
import com.agateau.pixelwheels.screens.PwStageScreen;
import com.agateau.utils.Assert;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
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
    public interface Listener {
        void onRestartPressed();

        void onQuitPressed();

        void onNextTrackPressed();
    }

    private final PwGame mGame;
    private final Listener mListener;
    private final GameInfo mGameInfo;

    private final GameWorldImpl mGameWorld;

    private final Array<GameRenderer> mGameRenderers = new Array<>();
    private final AudioClipper mAudioClipper;

    private final Array<RacerHudController> mRacerHudControllers = new Array<>();
    private final ScreenViewport mHudViewport = new ScreenViewport();
    private CountDownHudController mCountDownHudController;

    private final Stage mHudStage;

    private final PerformanceCounters mPerformanceCounters = new PerformanceCounters();
    private final PerformanceCounter mGameWorldPerformanceCounter;
    private final PerformanceCounter mRendererPerformanceCounter;
    private final PerformanceCounter mOverallPerformanceCounter;
    private final PerformanceCounter mHudPerformanceCounter;
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
        mRendererPerformanceCounter = mPerformanceCounters.add("Renderer");

        SpriteBatch batch = new SpriteBatch();
        mHudStage = new Stage(mHudViewport, batch);
        mHudStage.setDebugAll(Debug.instance.showHudDebugLines);

        // Create the count-down controller *before* the racer controller, otherwise the touch UI
        // won't receive input because the racer hud stage would be below the count-down hud stage
        createCountDownHudController();
        for (Racer racer : mGameWorld.getPlayerRacers()) {
            GameRenderer renderer =
                    new GameRenderer(
                            mGameWorld,
                            racer,
                            batch,
                            mGame.getConfig().headingUpCamera,
                            mPerformanceCounters);
            mGameRenderers.add(renderer);
            mRacerHudControllers.add(createRacerHudController(mGameWorld.getTrack(), racer));
        }
        createInputUi();
        mHudPerformanceCounter = mPerformanceCounters.add("Hud");

        mAudioClipper = createAudioClipper();

        setupDebugTools();
    }

    private void startMusic() {
        String musicId = mGame.getAssets().getTrackMusicId(mGameInfo.getTrack());
        mGame.getAudioManager().playMusic(musicId);
    }

    private void setupDebugTools() {
        if (Debug.instance.showDebugHud) {
            GameRenderer gameRenderer = mGameRenderers.first();
            RacerHudController controller = mRacerHudControllers.first();
            controller.initDebugHud(mPerformanceCounters);

            MineDropper dropper = new MineDropper(mGame, mGameWorld, gameRenderer);
            mGameWorld.addGameObject(dropper);
            controller.addDebugActor(dropper.createDebugButton());

            DropLocationDebugObject dropLocationDebugObject =
                    new DropLocationDebugObject(
                            mGame.getAssets(), gameRenderer, mGameWorld.getTrack());
            mGameWorld.addGameObject(dropLocationDebugObject);
            controller.addDebugActor(
                    dropLocationDebugObject.createDebugButton(mGame.getAssets().ui.skin));
        }
    }

    private RacerHudController createRacerHudController(Track track, Racer playerRacer) {
        Hud hud = new Hud(mGame, mHudStage);
        RacerHudController controller =
                new RacerHudController(mGame.getAssets(), mGameWorld, hud, playerRacer);

        if (Debug.instance.showDebugLayer) {
            int idx = 0;
            for (Racer racer : mGameWorld.getRacers()) {
                DebugShapeMap.put("racer" + idx, new RacerDebugShape(racer, track));
                ++idx;
            }
        }

        return controller;
    }

    private void createCountDownHudController() {
        Hud hud = new Hud(mGame, mHudStage);
        mCountDownHudController = new CountDownHudController(mGame.getAssets(), mGameWorld, hud);
    }

    private void createInputUi() {
        if (!GameInputHandlerFactories.hasMultitouch()) {
            return;
        }

        // Touch screen is single player only, so it's fine to only do this for the first player
        RacerHudController racerHudController = mRacerHudControllers.first();

        racerHudController.createPauseButton(
                new ClickListener() {
                    public void clicked(InputEvent event, float x, float y) {
                        pauseRace();
                    }
                });

        Pilot pilot = mGameWorld.getPlayerRacer(0).getPilot();
        if (pilot instanceof PlayerPilot) {
            ((PlayerPilot) pilot).createHudButtons(racerHudController.getHud());
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
            for (GameRenderer gameRenderer : mGameRenderers) {
                gameRenderer.onAboutToStart();
            }
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
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

        mRendererPerformanceCounter.stop();

        mHudPerformanceCounter.start();
        // Process hud *after* rendering game so that if an action on the hud (called from
        // mHudStage.act()) causes us to leave this screen (back to menu from pause, or leaving
        // the FinishedOverlay) then the game renderer does not alter the OpenGL viewport *after*
        // we have changed screens.
        for (RacerHudController controller : mRacerHudControllers) {
            controller.act(delta);
        }
        mCountDownHudController.act(delta);
        mHudViewport.apply(true);
        mHudStage.draw();
        mHudStage.act(delta);
        mHudPerformanceCounter.stop();

        mOverallPerformanceCounter.stop();
        if (!paused) {
            // This for loop replaces `mPerformanceCounters.tick(delta);` except it does not log an
            // error if the counter has not been used for the frame. This can happen in
            // GameWorldImpl.act(delta) if delta is shorter than Box2D timestep.
            for (PerformanceCounter counter : mPerformanceCounters.counters) {
                if (counter.valid) {
                    counter.tick(delta);
                }
            }
        }
    }

    private boolean isPauseKeyPressed() {
        for (Racer racer : mGameWorld.getPlayerRacers()) {
            PlayerPilot pilot = (PlayerPilot) racer.getPilot();
            if (pilot.isPauseKeyPressed()) {
                return true;
            }
        }
        return Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                || Gdx.input.isKeyJustPressed(Input.Keys.BACK);
    }

    @Override
    public void resize(int screenW, int screenH) {
        super.resize(screenW, screenH);
        float upp = PwStageScreen.getUnitsPerPixel();
        mHudViewport.setUnitsPerPixel(upp);
        Assert.check(mGameRenderers.size <= 4, "Unsupported number of renderers");

        int width = mGameRenderers.size == 1 ? screenW : (screenW / 2);
        int height = mGameRenderers.size < 3 ? screenH : (screenH / 2);

        boolean singlePlayer = mGameRenderers.size == 1;

        for (int idx = 0; idx < mGameRenderers.size; ++idx) {
            int x = (idx % 2) * width;
            int y = idx < 2 ? (screenH - height) : 0;

            // In multiplayer, we want 2 pixels between renderers. To do this we pad each renderer
            // 1 pixel on sides close to the center of the screen.
            int padL = x > 0 ? 1 : 0;
            int padR = singlePlayer ? 0 : (x == 0 ? 1 : 0);
            int padB = y > 0 ? 1 : 0;
            int padT = singlePlayer ? 0 : (y == 0 ? 1 : 0);
            mGameRenderers
                    .get(idx)
                    .setScreenRect(x + padL, y + padB, width - padL - padR, height - padT - padB);

            Hud hud = mRacerHudControllers.get(idx).getHud();
            hud.setScreenRect(
                    (int) (x * upp), (int) (y * upp), (int) (width * upp), (int) (height * upp));
        }
        mCountDownHudController
                .getHud()
                .setScreenRect(0, 0, (int) (screenW * upp), (int) (screenH * upp));

        mHudViewport.update(screenW, screenH, true);
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
        mGame.pushScreen(new ConfigScreen(mGame, ConfigScreen.Origin.PAUSE_OVERLAY));
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
