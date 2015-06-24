package com.greenyetilab.tinywheels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Select player vehicles
 */
public class MultiPlayerScreen extends com.greenyetilab.utils.StageScreen {
    private final TheGame mGame;
    private VehicleSelector mVehicleSelector1;
    private VehicleSelector mVehicleSelector2;

    public MultiPlayerScreen(TheGame game) {
        mGame = game;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.showMultiPlayer();
            }
        };
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);
        VehicleSelector.register(builder);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/multiplayer.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mVehicleSelector1 = builder.getActor("vehicleSelector1");
        mVehicleSelector1.init(mGame.getAssets());
        mVehicleSelector2 = builder.getActor("vehicleSelector2");
        mVehicleSelector2.init(mGame.getAssets());

        builder.getActor("goButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startRace();
            }
        });
        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.showMainMenu();
            }
        });
    }

    private void startRace() {
        GameInfo info = new GameInfo();
        info.mapInfo = mGame.getAssets().mapInfos.get(0);

        KeyboardInputHandler inputHandler = new KeyboardInputHandler();
        info.addPlayerInfo(mVehicleSelector1.getSelectedId(), inputHandler);

        inputHandler = new KeyboardInputHandler();
        inputHandler.setActionKey(KeyboardInputHandler.Action.LEFT, Input.Keys.X);
        inputHandler.setActionKey(KeyboardInputHandler.Action.RIGHT, Input.Keys.V);
        inputHandler.setActionKey(KeyboardInputHandler.Action.BRAKE, Input.Keys.C);
        inputHandler.setActionKey(KeyboardInputHandler.Action.TRIGGER, Input.Keys.CONTROL_LEFT);
        info.addPlayerInfo(mVehicleSelector2.getSelectedId(), inputHandler);

        mGame.start(info);
    }
}
