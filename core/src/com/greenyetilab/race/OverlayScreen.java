package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.StageScreen;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Created by aurelien on 21/11/14.
 */
public class OverlayScreen extends StageScreen {
    private final RaceGame mGame;

    public OverlayScreen(RaceGame game, final MapInfo mapInfo, TextureRegion bg, String text) {
        mGame = game;

        Image image = new Image(bg);
        image.setColor(1, 1, 1, 0.5f);
        getStage().addActor(image);

        Skin skin = game.getAssets().skin;
        Label label = new Label(text, skin);
        label.setAlignment(Align.center);

        TextButton tryAgainButton = createButton("Try Again", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.start(mapInfo);
            }
        });
        TextButton menuButton = createButton("Menu", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.showMainMenu();
            }
        });

        AnchorGroup group = new AnchorGroup();
        group.setSpacing(20);
        group.setFillParent(true);
        group.addPositionRule(tryAgainButton, Anchor.CENTER, group, Anchor.CENTER, 0, 0);
        group.addPositionRule(menuButton, Anchor.TOP_CENTER, tryAgainButton, Anchor.BOTTOM_CENTER, 0, -1);
        group.addPositionRule(label, Anchor.BOTTOM_CENTER, tryAgainButton, Anchor.TOP_CENTER, 0, 3);
        getStage().addActor(group);
    }

    private TextButton createButton(String text, ClickListener listener) {
        Skin skin = mGame.getAssets().skin;
        TextButton button = new TextButton(text, skin);
        button.setSize(300, 60);
        button.addListener(listener);
        return button;
    }
}
