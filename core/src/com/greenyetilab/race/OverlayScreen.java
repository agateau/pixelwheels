package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.StageScreen;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Created by aurelien on 21/11/14.
 */
public class OverlayScreen extends StageScreen {
    private final RaceGame mGame;

    public OverlayScreen(RaceGame game, TextureRegion bg, String text) {
        mGame = game;

        Image image = new Image(bg);
        image.setColor(1, 1, 1, 0.5f);
        getStage().addActor(image);

        Skin skin = game.getAssets().skin;
        Label label = new Label(text, skin);

        TextButton button = new TextButton("Menu", skin);
        button.setSize(200, 40);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.showMainMenu();
            }
        });

        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);
        group.addRule(label, Anchor.BOTTOM_CENTER, group, Anchor.CENTER);
        group.addRule(button, Anchor.TOP_CENTER, label, Anchor.BOTTOM_CENTER, 0, -20);
        getStage().addActor(group);
    }
}
