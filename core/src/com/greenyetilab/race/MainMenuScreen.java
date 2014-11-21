package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
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

        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);

        Group vGroup = new Group();
        float w = 0;
        float y = 0;
        for(String name: game.getAssets().mapNameList) {
            TextButton button = createStartButton(name);
            vGroup.addActor(button);
            button.setY(y);
            y += button.getHeight() + 10;
            w = Math.max(button.getWidth(), w);
        }
        vGroup.setSize(w, y - 10);

        group.addRule(vGroup, Anchor.CENTER, group, Anchor.CENTER);
        getStage().addActor(group);
    }

    private TextButton createStartButton(final String name) {
        Skin skin = mGame.getAssets().skin;
        TextButton button = new TextButton("Start " + name, skin, "default");
        button.setSize(200, 40);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.start(name);
            }
        });
        return button;
    }
}
