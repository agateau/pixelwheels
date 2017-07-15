package com.greenyetilab.tinywheels;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Select your map
 */
public class SelectMapScreen extends TwStageScreen {
    private final TheGame mGame;
    private final GameInfo mGameInfo;
    private final Maestro mMaestro;
    private final GameConfig.GameModeConfig mGameModeConfig;
    private MapSelector mMapSelector;

    public SelectMapScreen(TheGame game, Maestro maestro, GameInfo gameInfo, GameConfig.GameModeConfig gameModeConfig) {
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
        MapSelector.register(builder);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selectmap.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mMapSelector = builder.getActor("mapSelector");
        mMapSelector.init(assets);
        mMapSelector.setSelected(assets.findMapInfoByID(mGameModeConfig.map));

        builder.getActor("goButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
