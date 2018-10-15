package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.gameinput.GamepadInputWatcher;
import com.agateau.pixelwheels.gamesetup.Maestro;
import com.agateau.ui.RefreshHelper;
import com.agateau.ui.ScreenStack;
import com.agateau.ui.UiAssets;
import com.agateau.ui.UiBuilder;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.utils.FileUtils;
import com.agateau.utils.log.NLog;
import com.badlogic.gdx.utils.IntArray;

public class NotEnoughGamepadsScreen extends PwStageScreen {
    private final PwGame mGame;
    private final Maestro mMaestro;
    private final GamepadInputWatcher mWatcher;

    public NotEnoughGamepadsScreen(PwGame game, Maestro maestro, GamepadInputWatcher watcher) {
        super(game.getAssets().ui);
        mGame = game;
        mMaestro = maestro;
        mWatcher = watcher;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                ScreenStack stack = mGame.getScreenStack();
                stack.hideBlockingScreen();
                stack.showBlockingScreen(new NotEnoughGamepadsScreen(mGame, mMaestro, mWatcher));
            }
        };
        updateMissingGamepads();
    }

    @Override
    public void onBackPressed() {

    }

    public void updateMissingGamepads() {
        IntArray missingGamepads = mWatcher.getMissingGamepads();
        for (int idx = 0; idx < missingGamepads.size; ++idx) {
            int playerId = missingGamepads.get(idx);
            NLog.d("missing gamepad for player %d", playerId + 1);
        }
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/notenoughgamepads.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");
        menu.addButton("Main Menu").addListener(new MenuItemListener() {
            @Override
            public void triggered() {
                mMaestro.stop();
            }
        });
    }
}
