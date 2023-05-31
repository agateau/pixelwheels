package com.agateau.pixelwheels.screens;

import static com.agateau.translations.Translator.tr;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.gameinput.EnoughInputsChecker;
import com.agateau.pixelwheels.gamesetup.Maestro;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.ui.ScreenStack;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

public class NotEnoughInputsScreen extends PwStageScreen {
    private final PwGame mGame;
    private final Maestro mMaestro;
    private final EnoughInputsChecker mEnoughInputsChecker;
    private Label mLabel;

    public NotEnoughInputsScreen(PwGame game, Maestro maestro, EnoughInputsChecker checker) {
        super(game.getAssets().ui);
        mGame = game;
        mMaestro = maestro;
        mEnoughInputsChecker = checker;
        setupUi();
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                ScreenStack stack = mGame.getScreenStack();
                stack.hideBlockingScreen();
                stack.showBlockingScreen(
                        new NotEnoughInputsScreen(mGame, mMaestro, mEnoughInputsChecker));
            }
        };
        updateMissingInputs();
    }

    @Override
    public void onBackPressed() {}

    private static final StringBuilder sStringBuilder = new StringBuilder();

    public void updateMissingInputs() {
        sStringBuilder.setLength(0);
        Array<String> inputNames = mEnoughInputsChecker.getInputNames();
        for (int playerId = 0; playerId < mEnoughInputsChecker.getInputCount(); ++playerId) {
            if (playerId > 0) {
                sStringBuilder.append("\n");
            }
            String name = inputNames.get(playerId);
            String status = name == null ? tr("Missing") : name;
            sStringBuilder.append(StringUtils.format(tr("Player #%d: %s"), playerId + 1, status));
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
        menu.addButton(tr("MAIN MENU"))
                .addListener(
                        new MenuItemListener() {
                            @Override
                            public void triggered() {
                                mMaestro.stopEnoughInputChecker();
                                mGame.showMainMenu();
                            }
                        });
    }
}
