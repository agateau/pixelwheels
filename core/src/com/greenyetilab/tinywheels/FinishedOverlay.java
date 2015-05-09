package com.greenyetilab.tinywheels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;

/**
 * Appears on top of RaceGameScreen when player has lost
 */
public class FinishedOverlay extends Overlay {
    private final RaceGame mGame;

    public FinishedOverlay(RaceGame game, final Array<Racer> racers, final Racer playerRacer) {
        super(game.getAssets().dot);
        mGame = game;
        new RefreshHelper(this) {
            @Override
            protected void refresh() {
                setContent(createContent(racers, playerRacer));
            }
        };
        setContent(createContent(racers, playerRacer));
    }

    private Actor createContent(Array<Racer> racers, Racer playerRacer) {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        RacerListPane.register(builder);
        Actor content = builder.build(FileUtils.assets("screens/finishedoverlay.gdxui"));
        Label mainLabel = builder.getActor("mainLabel");
        mainLabel.setAlignment(Align.center, Align.center);
        mainLabel.setWidth(mainLabel.getPrefWidth());
        mainLabel.setHeight(mainLabel.getPrefHeight());
        builder.getActor("restartButton").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mGame.start();
            }
        });
        builder.getActor("menuButton").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mGame.showMainMenu();
            }
        });
        RacerListPane racerListPane = builder.getActor("racerListPane");
        racerListPane.init(mGame.getAssets().skin, racers, playerRacer);
        return content;
    }
}
