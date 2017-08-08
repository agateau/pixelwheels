package com.agateau.tinywheels;

import com.agateau.ui.KeyMapper;
import com.agateau.ui.Menu;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.VirtualKey;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Select player vehicles
 */
public class MultiPlayerScreen extends TwStageScreen {
    private final TwGame mGame;
    private final Maestro mMaestro;
    private final GameInfo mGameInfo;
    private VehicleSelector[] mVehicleSelectors = new VehicleSelector[2];
    private KeyMapper[] mKeyMappers = new KeyMapper[2];

    public MultiPlayerScreen(TwGame game, Maestro maestro, GameInfo gameInfo) {
        mGame = game;
        mMaestro = maestro;
        mGameInfo = gameInfo;

        mKeyMappers[0] = new KeyMapper();
        mKeyMappers[1] = new KeyMapper();

        mKeyMappers[1].put(VirtualKey.LEFT, Input.Keys.X);
        mKeyMappers[1].put(VirtualKey.RIGHT, Input.Keys.V);
        mKeyMappers[1].put(VirtualKey.UP, Input.Keys.D);
        mKeyMappers[1].put(VirtualKey.DOWN, Input.Keys.C);
        mKeyMappers[1].put(VirtualKey.TRIGGER, Input.Keys.CONTROL_LEFT);

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
        UiBuilder builder = new UiBuilder(assets.atlas, assets.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/multiplayer.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        createVehicleSelector(builder, assets, 0);
        createVehicleSelector(builder, assets, 1);

        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mMaestro.actionTriggered("back");
            }
        });
    }

    private void createVehicleSelector(UiBuilder builder, Assets assets, int idx) {
        GameConfig gameConfig = mGame.getConfig();
        String vehicleId = gameConfig.multiPlayerVehicles[idx];

        Menu menu = builder.getActor("menu" + String.valueOf(idx + 1));

        final Label readyLabel = builder.getActor("ready" + String.valueOf(idx + 1));

        VehicleSelector selector = new VehicleSelector(menu);
        mVehicleSelectors[idx] = selector;
        selector.init(assets);
        selector.setCurrent(assets.findVehicleDefByID(vehicleId));
        selector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                readyLabel.setVisible(true);
                nextIfPossible();
            }
        });

        menu.setKeyMapper(mKeyMappers[idx]);
        menu.addItem(selector);
    }

    private void nextIfPossible() {
        for (VehicleSelector selector : mVehicleSelectors) {
            if (selector.getSelected() == null) {
                return;
            }
        }
        next();
    }

    private void next() {
        // If we go back and forth between screens, there might already be some PlayerInfo instances
        // remove them
        mGameInfo.playerInfos.clear();

        GameConfig gameConfig = mGame.getConfig();

        for (int idx = 0; idx < 2; ++idx) {
            KeyboardInputHandler inputHandler;
            inputHandler = new KeyboardInputHandler();
            inputHandler.setKeyMapper(mKeyMappers[idx]);

            String id = mVehicleSelectors[idx].getSelectedId();
            gameConfig.multiPlayerVehicles[idx] = id;

            mGameInfo.addPlayerInfo(id, inputHandler);
        }

        gameConfig.flush();
        mMaestro.actionTriggered("next");
    }
}
