package com.agateau.tinywheels;

import com.agateau.utils.FileUtils;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Select your vehicle
 */
public class SelectVehicleScreen extends TwStageScreen {
    private final TwGame mGame;
    private final Maestro mMaestro;
    private final GameInfo mGameInfo;
    private VehicleSelector mVehicleSelector;

    public SelectVehicleScreen(TwGame game, Maestro maestro, GameInfo gameInfo) {
        mGame = game;
        mMaestro = maestro;
        mGameInfo = gameInfo;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectVehicleScreen(mGame, mMaestro, mGameInfo));
            }
        };
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.skin);
        VehicleSelector.register(builder);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selectvehicle.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mVehicleSelector = builder.getActor("vehicleSelector");
        mVehicleSelector.init(assets);

        String id = mGame.getConfig().onePlayerVehicle;
        mVehicleSelector.setSelected(assets.findVehicleDefByID(id));

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
        String id = mVehicleSelector.getSelectedId();
        GameConfig gameConfig = mGame.getConfig();

        gameConfig.onePlayerVehicle = id;
        gameConfig.flush();

        String inputHandlerId = gameConfig.input;
        GameInputHandlerFactory factory = GameInputHandlerFactories.getFactoryById(inputHandlerId);
        mGameInfo.addPlayerInfo(id, factory.create());
        mMaestro.actionTriggered("next");
    }
}
