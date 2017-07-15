package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Select player vehicles
 */
public class MultiPlayerScreen extends TwStageScreen {
    private final TheGame mGame;
    private final Maestro mMaestro;
    private final GameInfo mGameInfo;
    private VehicleSelector mVehicleSelector1;
    private VehicleSelector mVehicleSelector2;

    public MultiPlayerScreen(TheGame game, Maestro maestro, GameInfo gameInfo) {
        mGame = game;
        mMaestro = maestro;
        mGameInfo = gameInfo;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new MultiPlayerScreen(mGame, mMaestro, mGameInfo));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        Preferences prefs = mGame.getPreferences();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.skin);
        VehicleSelector.register(builder);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/multiplayer.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mVehicleSelector1 = builder.getActor("vehicleSelector1");
        mVehicleSelector1.init(assets);
        String id = prefs.getString(PrefConstants.MULTIPLAYER_VEHICLE_ID1);
        mVehicleSelector1.setSelected(assets.findVehicleDefByID(id));

        mVehicleSelector2 = builder.getActor("vehicleSelector2");
        mVehicleSelector2.init(assets);
        id = prefs.getString(PrefConstants.MULTIPLAYER_VEHICLE_ID2);
        mVehicleSelector2.setSelected(assets.findVehicleDefByID(id));

        builder.getActor("goButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                next();
            }
        });
        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mMaestro.actionTriggered("back");
            }
        });
    }

    private void next() {
        // If we go back and forth between screens, there might already be some PlayerInfo instances
        // remove them
        mGameInfo.playerInfos.clear();

        Preferences prefs = mGame.getPreferences();
        KeyboardInputHandler inputHandler;
        inputHandler = new KeyboardInputHandler();
        inputHandler.setActionKey(KeyboardInputHandler.Action.LEFT, Input.Keys.X);
        inputHandler.setActionKey(KeyboardInputHandler.Action.RIGHT, Input.Keys.V);
        inputHandler.setActionKey(KeyboardInputHandler.Action.BRAKE, Input.Keys.C);
        inputHandler.setActionKey(KeyboardInputHandler.Action.TRIGGER, Input.Keys.CONTROL_LEFT);
        String id = mVehicleSelector1.getSelectedId();
        mGameInfo.addPlayerInfo(id, inputHandler);
        prefs.putString(PrefConstants.MULTIPLAYER_VEHICLE_ID1, id);

        inputHandler = new KeyboardInputHandler();
        inputHandler.setActionKey(KeyboardInputHandler.Action.TRIGGER, Input.Keys.CONTROL_RIGHT);

        id = mVehicleSelector2.getSelectedId();
        mGameInfo.addPlayerInfo(id, inputHandler);
        prefs.putString(PrefConstants.MULTIPLAYER_VEHICLE_ID2, id);

        prefs.flush();
        mMaestro.actionTriggered("next");
    }
}
