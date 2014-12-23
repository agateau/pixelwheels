package com.greenyetilab.race;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.XmlReader;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;

/**
 * Appears on top of RaceGameScreen when player has lost
 */
public class GameOverOverlay extends Overlay {
    private final RaceGame mGame;

    public GameOverOverlay(RaceGame game, final int newHighScoreIndex, final String message) {
        super(game.getAssets().dot);
        mGame = game;
        new RefreshHelper(this) {
            @Override
            protected void refresh() {
                setContent(createContent(newHighScoreIndex, message));
            }
        };
        setContent(createContent(newHighScoreIndex, message));
    }

    private Actor createContent(int newHighScoreIndex, String message) {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        HighScorePane.register(builder);
        Actor content = builder.build(FileUtils.assets("screens/gameoveroverlay.gdxui"));
        Label mainLabel = builder.getActor("mainLabel");
        mainLabel.setText(message);
        mainLabel.setWidth(mainLabel.getPrefWidth());
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
        HighScorePane scorePane = builder.getActor("scoreScrollPane");
        scorePane.init(mGame.getAssets().skin, mGame.getHighScoreTable(), newHighScoreIndex);
        return content;
    }
}
