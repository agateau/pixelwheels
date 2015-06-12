package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.PerformanceCounters;

public class RaceScreen extends ScreenAdapter {
    private final TheGame mGame;
    private final GameWorld mGameWorld;
    private final Color mBackgroundColor;
    private Batch mBatch;

    private Array<GameRenderer> mGameRenderers = new Array<GameRenderer>();

    private Hud mHud;

    private final PerformanceCounters mPerformanceCounters = new PerformanceCounters();
    private PerformanceCounter mGameWorldPerformanceCounter;
    private PerformanceCounter mRendererPerformanceCounter;
    private PerformanceCounter mOverallPerformanceCounter;

    public RaceScreen(TheGame game, MapInfo mapInfo, GameInfo gameInfo) {
        mGame = game;
        mBatch = new SpriteBatch();
        mOverallPerformanceCounter = mPerformanceCounters.add("All");
        mGameWorldPerformanceCounter = mPerformanceCounters.add("GameWorld.act");
        mGameWorld = new GameWorld(game, mapInfo, gameInfo, mPerformanceCounters);
        mBackgroundColor = mapInfo.getBackgroundColor();
        mRendererPerformanceCounter = mPerformanceCounters.add("Renderer");
        for (int idx = 0; idx < gameInfo.playerInfos.size; ++idx) {
            Vehicle vehicle = mGameWorld.getPlayerVehicle(idx);
            GameRenderer gameRenderer = new GameRenderer(game.getAssets(), mGameWorld, vehicle, mBatch, mPerformanceCounters);
            setupGameRenderer(gameRenderer);
            mGameRenderers.add(gameRenderer);
        }
        mHud = new Hud(game.getAssets(), mGameWorld, mBatch, 0, mGameRenderers.first().getCamera(), mPerformanceCounters);
        Racer racer = mGameWorld.getPlayerRacer(0);
        Pilot pilot = racer.getPilot();
        if (pilot instanceof PlayerPilot) {
            HudBridge hudBridge = mHud.getHudBridge();
            ((PlayerPilot) pilot).createHudActors(hudBridge);
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

        mHud.act(delta);

        mRendererPerformanceCounter.start();
        Gdx.gl.glClearColor(mBackgroundColor.r, mBackgroundColor.g, mBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for (GameRenderer gameRenderer : mGameRenderers) {
            gameRenderer.render(delta);
        }
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mHud.draw();
        mRendererPerformanceCounter.stop();

        mOverallPerformanceCounter.stop();
        mPerformanceCounters.tick(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mHud.resize(width, height);
        int viewportWidth = width / mGameRenderers.size;
        int x = 0;
        for (GameRenderer gameRenderer : mGameRenderers) {
            gameRenderer.setScreenRect(x, 0, viewportWidth, height);
            x += viewportWidth;
        }
    }

    private void showFinishedOverlay() {
        mHud.getStage().addActor(new FinishedOverlay(mGame, mGameWorld.getRacers(), mGameWorld.getPlayerRacer(0))); // FIXME
    }

    @Override
    public void dispose() {
        super.dispose();
        mGameWorld.dispose();
    }
}
