package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class RaceScreen extends ScreenAdapter {
    private final TheGame mGame;
    private final GameWorld mGameWorld;
    private final Color mBackgroundColor;
    private Batch mBatch;

    private Array<GameRenderer> mGameRenderers = new Array<GameRenderer>();

    private Array<Hud> mHuds = new Array<Hud>();
    private ScreenViewport mHudViewport = new ScreenViewport();
    private final Stage mHudStage;

    private final PerformanceCounters mPerformanceCounters = new PerformanceCounters();
    private PerformanceCounter mGameWorldPerformanceCounter;
    private PerformanceCounter mRendererPerformanceCounter;
    private PerformanceCounter mOverallPerformanceCounter;

    public RaceScreen(TheGame game, GameInfo gameInfo) {
        mGame = game;
        mBatch = new SpriteBatch();
        mOverallPerformanceCounter = mPerformanceCounters.add("All");
        mGameWorldPerformanceCounter = mPerformanceCounters.add("GameWorld.act");
        mGameWorld = new GameWorld(game, gameInfo, mPerformanceCounters);
        mBackgroundColor = gameInfo.mapInfo.getBackgroundColor();
        mRendererPerformanceCounter = mPerformanceCounters.add("Renderer");

        mHudStage = new Stage(mHudViewport, mBatch);
        Gdx.input.setInputProcessor(mHudStage);

        for (int idx = 0; idx < gameInfo.playerInfos.size; ++idx) {
            Vehicle vehicle = mGameWorld.getPlayerVehicle(idx);
            GameRenderer gameRenderer = new GameRenderer(game.getAssets(), mGameWorld, vehicle, mBatch, mPerformanceCounters);
            setupGameRenderer(gameRenderer);

            Hud hud = new Hud(game.getAssets(), mGameWorld, mHudStage, idx, mPerformanceCounters);
            Racer racer = mGameWorld.getPlayerRacer(idx);
            Pilot pilot = racer.getPilot();
            if (pilot instanceof PlayerPilot) {
                ((PlayerPilot) pilot).createHudActors(hud.getRoot());
            }

            mGameRenderers.add(gameRenderer);
            mHuds.add(hud);
        }
    }

    private void setupGameRenderer(GameRenderer gameRenderer) {
        GameRenderer.DebugConfig config = new GameRenderer.DebugConfig();
        Preferences prefs = TheGame.getPreferences();
        config.enabled = prefs.getBoolean("debug/box2d", false);
        config.drawTileCorners = prefs.getBoolean("debug/tiles/drawCorners", false);
        config.drawVelocities = prefs.getBoolean("debug/box2d/drawVelocities", false);
        gameRenderer.setDebugConfig(config);
    }

    @Override
    public void render(float delta) {
        mOverallPerformanceCounter.start();
        mGameWorldPerformanceCounter.start();
        GameWorld.State oldState = mGameWorld.getState();
        mGameWorld.act(delta);
        GameWorld.State newState = mGameWorld.getState();
        mGameWorldPerformanceCounter.stop();

        if (oldState != newState) {
            showFinishedOverlay();
        }

        for (Hud hud : mHuds) {
            hud.act(delta);
        }
        mHudStage.act(delta);

        mRendererPerformanceCounter.start();
        Gdx.gl.glClearColor(mBackgroundColor.r, mBackgroundColor.g, mBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for (GameRenderer gameRenderer : mGameRenderers) {
            gameRenderer.render(delta);
        }

        mHudViewport.apply(true);
        mHudStage.draw();

        mRendererPerformanceCounter.stop();

        mOverallPerformanceCounter.stop();
        mPerformanceCounters.tick(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        int viewportWidth = width / mGameRenderers.size;
        int x = 0;
        for (int idx = 0; idx < mGameRenderers.size; ++idx) {
            mGameRenderers.get(idx).setScreenRect(x, 0, viewportWidth, height);
            mHuds.get(idx).setScreenRect(x, 0, viewportWidth, height);
            x += viewportWidth;
        }
        mHudViewport.update(width, height, true);
    }

    private void showFinishedOverlay() {
        FinishedOverlay overlay = new FinishedOverlay(mGame, mGameWorld.getRacers(), mGameWorld.getPlayerRacers());
        mHudStage.addActor(overlay);
    }

    @Override
    public void dispose() {
        super.dispose();
        mGameWorld.dispose();
    }
}
