package com.agateau.tinywheels;

import com.agateau.ui.KeyMapper;
import com.agateau.ui.VirtualKey;
import com.agateau.utils.FileUtils;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.agateau.ui.anchor.AnchorGroup;

/**
 * Select player vehicles
 */
public class MultiPlayerScreen extends TwStageScreen {
    private final TwGame mGame;
    private final Maestro mMaestro;
    private final GameInfo mGameInfo;
    private VehicleSelector mVehicleSelector1;
    private VehicleSelector mVehicleSelector2;
    private KeyMapper mKeyMapper1 = new KeyMapper();
    private KeyMapper mKeyMapper2 = new KeyMapper();

    public MultiPlayerScreen(TwGame game, Maestro maestro, GameInfo gameInfo) {
        mGame = game;
        mMaestro = maestro;
        mGameInfo = gameInfo;

        mKeyMapper2.put(VirtualKey.LEFT, Input.Keys.X);
        mKeyMapper2.put(VirtualKey.RIGHT, Input.Keys.V);
        mKeyMapper2.put(VirtualKey.UP, Input.Keys.D);
        mKeyMapper2.put(VirtualKey.DOWN, Input.Keys.C);
        mKeyMapper2.put(VirtualKey.TRIGGER, Input.Keys.CONTROL_LEFT);

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
        GameConfig gameConfig = mGame.getConfig();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.skin);
        VehicleSelector.register(builder);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/multiplayer.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mVehicleSelector1 = builder.getActor("vehicleSelector1");
        mVehicleSelector1.init(assets);
        mVehicleSelector1.setSelected(assets.findVehicleDefByID(gameConfig.twoPlayersVehicle1));

        mVehicleSelector2 = builder.getActor("vehicleSelector2");
        mVehicleSelector2.init(assets);
        mVehicleSelector2.setSelected(assets.findVehicleDefByID(gameConfig.twoPlayersVehicle2));

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

        GameConfig gameConfig = mGame.getConfig();
        KeyboardInputHandler inputHandler;
        inputHandler = new KeyboardInputHandler();
        inputHandler.setKeyMapper(mKeyMapper1);

        String id = mVehicleSelector1.getSelectedId();
        mGameInfo.addPlayerInfo(id, inputHandler);
        gameConfig.twoPlayersVehicle1 = id;

        inputHandler = new KeyboardInputHandler();
        inputHandler.setKeyMapper(mKeyMapper2);

        id = mVehicleSelector2.getSelectedId();
        mGameInfo.addPlayerInfo(id, inputHandler);
        gameConfig.twoPlayersVehicle2 = id;

        gameConfig.flush();
        mMaestro.actionTriggered("next");
    }
}
