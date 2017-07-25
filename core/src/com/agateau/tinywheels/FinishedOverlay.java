package com.agateau.tinywheels;

import com.agateau.utils.FileUtils;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

/**
 * Appears on top of RaceScreen at the end of the race
 */
public class FinishedOverlay extends Overlay {
    private final TwGame mGame;
    private final Maestro mMaestro;

    public FinishedOverlay(TwGame game, Maestro maestro, final Array<Racer> racers, final Array<Racer> playerRacers) {
        super(game.getAssets().dot);
        mGame = game;
        mMaestro = maestro;
        new RefreshHelper(this) {
            @Override
            protected void refresh() {
                setContent(createContent(racers, playerRacers));
            }
        };
        setContent(createContent(racers, playerRacers));
    }

    private Actor createContent(Array<Racer> racers, final Array<Racer> playerRacers) {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        RacerListPane.register(builder);
        Actor content = builder.build(FileUtils.assets("screens/finishedoverlay.gdxui"));
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
        RacerListPane racerListPane = builder.getActor("racerListPane");
        racerListPane.init(mGame.getAssets().skin, racers, playerRacers);
        return content;
    }
}
