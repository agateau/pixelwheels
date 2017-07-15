package com.greenyetilab.tinywheels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.XmlReader;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;
import com.greenyetilab.utils.anchor.AnchorGroup;
import com.greenyetilab.utils.anchor.SizeRule;

/**
 * The config screen
 */
public class ConfigScreen extends TwStageScreen {
    private final TheGame mGame;

    public ConfigScreen(TheGame game) {
        mGame = game;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new ConfigScreen(mGame));
            }
        };
    }

    private void setupUi() {
        final GameConfig gameConfig = mGame.getConfig();

        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        builder.registerActorFactory("GameInputHandlerSelector", new UiBuilder.ActorFactory() {
            @Override
            public Actor createActor(XmlReader.Element element) {
                return new GameInputHandlerSelector(gameConfig, mGame.getAssets().skin);
            }
        });

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/config.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        final CheckBox checkBox = builder.getActor("rotateScreenCheckBox");
        checkBox.setChecked(gameConfig.rotateCamera);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameConfig.rotateCamera = checkBox.isChecked();
                gameConfig.flush();
            }
        });

        builder.getActor("debugButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.pushScreen(new DebugScreen(mGame));
            }
        });
        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.popScreen();
            }
        });
        root.addSizeRule(builder.getActor("gameInputHandlerSelector"), root, 1, SizeRule.IGNORE, -2, 0);
    }
}
