package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Preferences;
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
    private final String mPreferenceKey;
    private MapSelector mMapSelector;

    public SelectMapScreen(TheGame game, Maestro maestro, GameInfo gameInfo, String preferenceKey) {
        mGame = game;
        mMaestro = maestro;
        mGameInfo = gameInfo;
        mPreferenceKey = preferenceKey;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectMapScreen(mGame, mMaestro, mGameInfo, mPreferenceKey));
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
        String id = TheGame.getPreferences().getString(mPreferenceKey);
        mMapSelector.setSelected(assets.findMapInfoByID(id));

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
        Preferences prefs = TheGame.getPreferences();
        prefs.putString(mPreferenceKey, mMapSelector.getSelected().getId());
        prefs.flush();
    }

    private void next() {
        saveSelectedMap();
        mGameInfo.mapInfo = mMapSelector.getSelected();
        mMaestro.actionTriggered("next");
    }
}
