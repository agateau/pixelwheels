package com.greenyetilab.tinywheels;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Created by aurelien on 21/11/14.
 */
public class MainMenuScreen extends com.greenyetilab.utils.StageScreen {
    private final TheGame mGame;

    public MainMenuScreen(TheGame game) {
        mGame = game;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.showMainMenu();
            }
        };
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/mainmenu.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);
        builder.getActor("startButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.showSelectVehicle();
            }
        });
        builder.getActor("settingsButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.pushScreen(new ConfigScreen(mGame));
            }
        });
    }
}
