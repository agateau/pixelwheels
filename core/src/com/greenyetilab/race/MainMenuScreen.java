package com.greenyetilab.race;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Created by aurelien on 21/11/14.
 */
public class MainMenuScreen extends com.greenyetilab.utils.StageScreen {
    private final RaceGame mGame;
    private static final float PADDING = 20;

    public MainMenuScreen(RaceGame game) {
        mGame = game;
        Skin skin = mGame.getAssets().skin;

        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);

        TextButton startButton = createStartButton();

        TextButton debugButton = new TextButton("Debug", skin, "default");
        debugButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.pushScreen(new DebugScreen(mGame));
            }
        });

        group.addPositionRule(new Label("Santa Claus Racer", skin), Anchor.TOP_CENTER, group, Anchor.TOP_CENTER, 0, -PADDING);
        group.addPositionRule(startButton, Anchor.CENTER, group, Anchor.CENTER);
        group.addPositionRule(debugButton, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT);

        getStage().addActor(group);
    }

    private TextButton createStartButton() {
        Skin skin = mGame.getAssets().skin;
        String text = "Start";
        TextButton button = new TextButton(text, skin, "default");
        button.setSize(300, 60);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.start();
            }
        });
        return button;
    }
}
