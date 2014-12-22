package com.greenyetilab.race;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Show the high scores
 */
public class HighScoreScreen extends com.greenyetilab.utils.StageScreen {
    private final RaceGame mGame;

    public HighScoreScreen(RaceGame game) {
        mGame = game;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new HighScoreScreen(mGame));
            }
        };
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        HighScorePane.register(builder);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/highscore.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);
        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.popScreen();
            }
        });
        HighScorePane scorePane = builder.getActor("scoreScrollPane");
        scorePane.init(mGame.getAssets().skin, mGame.getHighScoreTable());
    }
}
