package com.agateau.tinywheels;

import com.agateau.utils.FileUtils;
import com.agateau.utils.RefreshHelper;
import com.agateau.utils.UiBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Appears on top of RaceScreen when paused
 */
public class PauseOverlay extends Overlay {
    private final TwGame mGame;
    private final Maestro mMaestro;
    private final RaceScreen mRaceScreen;

    public PauseOverlay(TwGame game, Maestro maestro, RaceScreen raceScreen) {
        super(game.getAssets().dot);
        mGame = game;
        mMaestro = maestro;
        mRaceScreen = raceScreen;
        new RefreshHelper(this) {
            @Override
            protected void refresh() {
                setContent(createContent());
            }
        };
        setContent(createContent());
    }

    private Actor createContent() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        RacerListPane.register(builder);
        Actor content = builder.build(FileUtils.assets("screens/pauseoverlay.gdxui"));
        builder.getActor("resumeButton").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mRaceScreen.resumeRace();
            }
        });
        builder.getActor("restartButton").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mMaestro.actionTriggered("restart");
            }
        });
        builder.getActor("menuButton").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mMaestro.actionTriggered("quit");
            }
        });
        builder.getActor("settingsButton").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mGame.pushScreen(new ConfigScreen(mGame));
            }
        });
        return content;
    }
}
