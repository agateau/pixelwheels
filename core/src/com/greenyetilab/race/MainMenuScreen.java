package com.greenyetilab.race;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
    private static final float PADDING = 20;

    public MainMenuScreen(RaceGame game) {
        mGame = game;
        Skin skin = mGame.getAssets().skin;

        AnchorGroup group = new AnchorGroup();
        group.setFillParent(true);

        Group vGroup = new Group();
        float w = 0;
        float y = 0;
        for(MapInfo mapInfo: game.getAssets().mapInfoList) {
            TextButton button = createStartButton(mapInfo);
            vGroup.addActor(button);
            button.setY(y);
            y += button.getHeight() + PADDING;
            w = Math.max(button.getWidth(), w);
        }
        vGroup.setSize(w, y - PADDING);

        group.addRule(vGroup, Anchor.CENTER, group, Anchor.CENTER);
        group.addRule(new Label("Select Race", skin), Anchor.BOTTOM_CENTER, vGroup, Anchor.TOP_CENTER, 0, PADDING);
        getStage().addActor(group);
    }

    private TextButton createStartButton(final MapInfo mapInfo) {
        Skin skin = mGame.getAssets().skin;
        String text = mapInfo.getTitle();
        float best = mapInfo.getBestTime();
        if (best > 0) {
            text += " (" + StringUtils.formatRaceTime(best) + ")";
        }
        TextButton button = new TextButton(text, skin, "default");
        button.setSize(200, 60);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.start(mapInfo);
            }
        });
        return button;
    }
}
