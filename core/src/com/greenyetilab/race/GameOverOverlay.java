package com.greenyetilab.race;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Appears on top of RaceGameScreen when player has lost
 */
public class GameOverOverlay extends WidgetGroup {
    private static final float IN_DURATION = 0.5f;
    private final RaceGame mGame;
    private final Actor mContent;

    public GameOverOverlay(RaceGame game, GameWorld gameWorld) {
        mGame = game;
        setFillParent(true);
        Image bg = new Image(mGame.getAssets().dot);
        bg.setColor(0, 0, 0, 0);
        bg.setFillParent(true);
        addActor(bg);
        bg.addAction(Actions.alpha(0.6f, IN_DURATION));

        mContent = createContent();
    }

    private Actor createContent() {
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

    @Override
    public void layout() {
        super.layout();
        if (mContent.getParent() == null) {
            mContent.setSize(this.getWidth(), this.getHeight());
            mContent.setPosition(0, this.getHeight());
            mContent.addAction(Actions.moveTo(0, 0, IN_DURATION, Interpolation.swingOut));
            addActor(mContent);
        }
    }

    private TextButton createButton(String text, ClickListener listener) {
        Skin skin = mGame.getAssets().skin;
        TextButton button = new TextButton(text, skin);
        button.setSize(300, 60);
        button.addListener(listener);
        return button;
    }
}
