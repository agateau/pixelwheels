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
public class SelectMapScreen extends com.greenyetilab.utils.StageScreen {
    private final TheGame mGame;
    private final GameInfo mGameInfo;
    private MapSelector mMapSelector;

    public SelectMapScreen(TheGame game, GameInfo gameInfo) {
        mGame = game;
        mGameInfo = gameInfo;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.showSelectMap(mGameInfo);
            }
        };
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        MapSelector.register(builder);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selectmap.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mMapSelector = builder.getActor("mapSelector");
        mMapSelector.init(mGame.getAssets());

        builder.getActor("goButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startRace();
            }
        });
        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.showSelectVehicle();
            }
        });
    }

    private void startRace() {
        mGameInfo.mapInfo = mMapSelector.getSelected();
        mGame.start(mGameInfo);
    }
}
