package com.greenyetilab.race;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.IntArray;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;
import com.greenyetilab.utils.log.NLog;

import javax.swing.Scrollable;

/**
 * Appears on top of RaceGameScreen when player has lost
 */
public class GameOverOverlay extends Overlay {
    private final RaceGame mGame;
    private Cell mNewHighScoreCell = null;
    private ScrollPane mScrollPane;

    public GameOverOverlay(RaceGame game, final int newHighScoreIndex) {
        super(game.getAssets().dot);
        mGame = game;
        new RefreshHelper(this) {
            @Override
            protected void refresh() {
                setContent(createContent(newHighScoreIndex));
            }
        };
        setContent(createContent(newHighScoreIndex));
    }

    @Override
    public void layout() {
        super.layout();
        if (mNewHighScoreCell != null) {
            float y = mNewHighScoreCell.getActorY();
            float height = mNewHighScoreCell.getActorHeight();
            mScrollPane.scrollToCenter(0, y, mScrollPane.getWidth(), height);
        }
    }

    private Actor createContent(int newHighScoreIndex) {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        Actor content = builder.build(FileUtils.assets("screens/gameoveroverlay.gdxui"));
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
        mScrollPane = builder.getActor("scoreScrollPane");
        Actor actor = createScoreTable(newHighScoreIndex);
        actor.setWidth(mScrollPane.getWidth());
        mScrollPane.setWidget(actor);
        return content;
    }

    private Actor createScoreTable(int newHighScoreIndex) {
        Skin skin = mGame.getAssets().skin;
        Table table = new Table(skin);
        IntArray scores = mGame.getHighScoreTable().getScores();
        for (int idx = 0; idx < scores.size; ++idx) {
            String style = idx == newHighScoreIndex ? "newHighScore" : "highScore";
            Cell cell = table.add(String.format("%d.", idx + 1), style).right();
            if (idx == newHighScoreIndex) {
                mNewHighScoreCell = cell;
            }
            table.add(String.valueOf(scores.get(idx)), style).expandX().right();
            table.row();
        }
        return table;
    }
}
