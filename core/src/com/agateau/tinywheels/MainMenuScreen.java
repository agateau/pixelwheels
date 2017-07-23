package com.agateau.tinywheels;

import com.agateau.utils.FileUtils;
import com.agateau.utils.RefreshHelper;
import com.agateau.utils.UiBuilder;
import com.agateau.utils.anchor.AnchorGroup;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Main menu, shown at startup
 */
public class MainMenuScreen extends TwStageScreen {
    private final TwGame mGame;

    public MainMenuScreen(TwGame game) {
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
        boolean desktop = Gdx.app.getType() == Application.ApplicationType.Desktop;
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        if (desktop) {
            builder.defineVariable("desktop");
        }

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/mainmenu.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);
        builder.getActor("onePlayerButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.showOnePlayer();
            }
        });
        if (desktop) {
            builder.getActor("multiPlayerButton").addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    mGame.showMultiPlayer();
                }
            });
        }
        builder.getActor("settingsButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.pushScreen(new ConfigScreen(mGame));
            }
        });
    }
}
