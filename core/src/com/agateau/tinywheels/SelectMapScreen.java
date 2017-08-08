package com.agateau.tinywheels;

import com.agateau.ui.Menu;
import com.agateau.utils.FileUtils;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Select your map
 */
public class SelectMapScreen extends TwStageScreen {
    private final TwGame mGame;
    private final GameInfo mGameInfo;
    private final Maestro mMaestro;
    private final GameConfig.GameModeConfig mGameModeConfig;
    private MapSelector mMapSelector;

    public SelectMapScreen(TwGame game, Maestro maestro, GameInfo gameInfo, GameConfig.GameModeConfig gameModeConfig) {
        mGame = game;
        mMaestro = maestro;
        mGameInfo = gameInfo;
        mGameModeConfig = gameModeConfig;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectMapScreen(mGame, mMaestro, mGameInfo, mGameModeConfig));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selectmap.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");

        mMapSelector = new MapSelector(menu);
        mMapSelector.init(assets);
        mMapSelector.setCurrent(assets.findMapInfoByID(mGameModeConfig.map));
        menu.addItem(mMapSelector);

        mMapSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                next();
            }
        });

        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSelectedMap();
                mMaestro.actionTriggered("back");
            }
        });
    }

    private void saveSelectedMap() {
        mGameModeConfig.map = mMapSelector.getSelected().getId();
        mGame.getConfig().flush();
    }

    private void next() {
        saveSelectedMap();
        mGameInfo.mapInfo = mMapSelector.getSelected();
        mMaestro.actionTriggered("next");
    }
}
