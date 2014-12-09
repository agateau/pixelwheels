package com.greenyetilab.race;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Created by aurelien on 09/12/14.
 */
public class DebugScreen  extends com.greenyetilab.utils.StageScreen {
    private final RaceGame mGame;

    public DebugScreen(RaceGame game) {
        mGame = game;

        AnchorGroup root = new AnchorGroup();
        root.setFillParent(true);
        getStage().addActor(root);

        TextButton backButton = new TextButton("Back", mGame.getAssets().skin, "default");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.popScreen();
            }
        });
        root.addPositionRule(backButton, Anchor.BOTTOM_LEFT, root, Anchor.BOTTOM_LEFT);
    }
}
