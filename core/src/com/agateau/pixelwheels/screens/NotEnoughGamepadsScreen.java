package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.gameinput.GamepadInputWatcher;
import com.agateau.pixelwheels.gamesetup.Maestro;
import com.agateau.ui.ScreenStack;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.IntArray;
import java.util.Locale;

public class NotEnoughGamepadsScreen extends PwStageScreen {
    private final PwGame mGame;
    private final Maestro mMaestro;
    private final GamepadInputWatcher mWatcher;
    private Label mLabel;

    public NotEnoughGamepadsScreen(PwGame game, Maestro maestro, GamepadInputWatcher watcher) {
        super(game.getAssets().ui);
        mGame = game;
        mMaestro = maestro;
        mWatcher = watcher;
        setupUi();
        new PwRefreshHelper(mGame, getStage()) {
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
    public void onBackPressed() {}

    private static final StringBuilder sStringBuilder = new StringBuilder();

    public void updateMissingGamepads() {
        sStringBuilder.setLength(0);
        IntArray missingGamepads = mWatcher.getMissingGamepads();
        for (int playerId = 0; playerId < mWatcher.getInputCount(); ++playerId) {
            boolean ok = !missingGamepads.contains(playerId);
            if (playerId > 0) {
                sStringBuilder.append("\n");
            }
            sStringBuilder.append(String.format(Locale.US, "Player #%d: ", playerId + 1));
            sStringBuilder.append(ok ? "OK" : "Missing");
        }
        mLabel.setText(sStringBuilder.toString());
        mLabel.setSize(mLabel.getPrefWidth(), mLabel.getPrefHeight());
    }

    private void setupUi() {
        Assets assets = mGame.getAssets();
        UiBuilder builder = new UiBuilder(assets.atlas, assets.ui.skin);

        AnchorGroup root =
                (AnchorGroup) builder.build(FileUtils.assets("screens/notenoughgamepads.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mLabel = builder.getActor("gamepadsLabel");

        Menu menu = builder.getActor("menu");
        menu.addButton("MAIN MENU")
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                mMaestro.stopGamepadInputWatcher();
                                mGame.showMainMenu();
                            }
                        });
    }
}
