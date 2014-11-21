package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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

    public MainMenuScreen(RaceGame game) {
        super();
        mGame = game;

        Skin skin = game.getAssets().skin;

        final TextButton button = new TextButton("Start", skin, "default");

        button.setWidth(200f);
        button.setHeight(40f);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.start("race.tmx");
            }
        });

        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);
        group.addRule(button, Anchor.CENTER, group, Anchor.CENTER);
        getStage().addActor(group);
    }

}
