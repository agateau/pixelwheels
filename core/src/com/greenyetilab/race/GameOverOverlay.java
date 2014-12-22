package com.greenyetilab.race;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Appears on top of RaceGameScreen when player has lost
 */
public class GameOverOverlay extends Overlay {
    private final RaceGame mGame;

    public GameOverOverlay(RaceGame game, GameWorld gameWorld) {
        super(game.getAssets().dot);
        mGame = game;
    }

    @Override
    protected Actor createContent() {
        Skin skin = mGame.getAssets().skin;
        Label label = new Label("Game Over", skin);
        label.setAlignment(Align.center);

        TextButton tryAgainButton = createButton("Try Again", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.start();
            }
        });
        TextButton menuButton = createButton("Menu", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.showMainMenu();
            }
        });

        AnchorGroup mContent = new AnchorGroup();
        mContent.setSpacing(20);
        mContent.addPositionRule(tryAgainButton, Anchor.CENTER, mContent, Anchor.CENTER, 0, 0);
        mContent.addPositionRule(menuButton, Anchor.TOP_CENTER, tryAgainButton, Anchor.BOTTOM_CENTER, 0, -1);
        mContent.addPositionRule(label, Anchor.BOTTOM_CENTER, tryAgainButton, Anchor.TOP_CENTER, 0, 3);

        return mContent;
    }

    private TextButton createButton(String text, ClickListener listener) {
        Skin skin = mGame.getAssets().skin;
        TextButton button = new TextButton(text, skin);
        button.setSize(300, 60);
        button.addListener(listener);
        return button;
    }
}
