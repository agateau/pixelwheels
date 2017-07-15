package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
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
    private final TheGame mGame;
    private final Maestro mMaestro;
    private final GameWorld mGameWorld;
    private final Color mBackgroundColor;

    private Array<GameRenderer> mGameRenderers = new Array<GameRenderer>();

    private Array<Hud> mHuds = new Array<Hud>();
    private Array<HudContent> mHudContents = new Array<HudContent>();
    private ScreenViewport mHudViewport = new ScreenViewport();
    private final Stage mHudStage;

    private final PerformanceCounters mPerformanceCounters = new PerformanceCounters();
    private PerformanceCounter mGameWorldPerformanceCounter;
    private PerformanceCounter mRendererPerformanceCounter;
    private PerformanceCounter mOverallPerformanceCounter;
    private PauseOverlay mPauseOverlay = null;

    public RaceScreen(TheGame game, Maestro maestro, GameInfo gameInfo) {
        mGame = game;
        mMaestro = maestro;
        SpriteBatch batch = new SpriteBatch();
        mOverallPerformanceCounter = mPerformanceCounters.add("All");
        mGameWorldPerformanceCounter = mPerformanceCounters.add("GameWorld.act");
        mGameWorld = new GameWorld(game, gameInfo, mPerformanceCounters);
        mBackgroundColor = gameInfo.mapInfo.getBackgroundColor();
        mRendererPerformanceCounter = mPerformanceCounters.add("Renderer");

        mHudStage = new Stage(mHudViewport, batch);
        mHudStage.setDebugAll(GamePlay.instance.debugHud);

        for (int idx = 0; idx < gameInfo.playerInfos.size; ++idx) {
            Vehicle vehicle = mGameWorld.getPlayerVehicle(idx);
            GameRenderer gameRenderer = new GameRenderer(game.getAssets(), mGameWorld, vehicle, batch, mPerformanceCounters);
            setupGameRenderer(gameRenderer);

            Hud hud = new Hud(game.getAssets(), mHudStage);
            HudContent hudContent = setupHudContent(hud, idx);
            Racer racer = mGameWorld.getPlayerRacer(idx);
            Pilot pilot = racer.getPilot();
            if (pilot instanceof PlayerPilot) {
                ((PlayerPilot) pilot).createHudButtons(hud);
            }

            mGameRenderers.add(gameRenderer);
            mHuds.add(hud);
            mHudContents.add(hudContent);
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

    private HudContent setupHudContent(Hud hud, int idx) {
        HudContent hudContent = new HudContent(mGame.getAssets(), mGameWorld, hud, idx);
        if (idx == 0) {
            if (TheGame.getPreferences().getBoolean("debug/showDebugHud", false)) {
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

        for (Hud hud : mHuds) {
            hud.act(delta);
        }
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
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
        // 5. TheGame.replaceScreen() calls dispose() on the old screen
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
