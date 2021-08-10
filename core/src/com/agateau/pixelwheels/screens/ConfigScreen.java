/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.screens;

import static com.agateau.translations.Translator.tr;

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameConfig;
import com.agateau.pixelwheels.Language;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.VersionInfo;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactories;
import com.agateau.pixelwheels.gameinput.GameInputHandlerFactory;
import com.agateau.pixelwheels.gameinput.GamepadInputHandler;
import com.agateau.pixelwheels.gameinput.KeyboardInputHandler;
import com.agateau.pixelwheels.utils.StringUtils;
import com.agateau.ui.UiAssets;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.ButtonMenuItem;
import com.agateau.ui.menu.LabelMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.menu.MenuItemGroup;
import com.agateau.ui.menu.MenuItemListener;
import com.agateau.ui.menu.SelectorMenuItem;
import com.agateau.ui.menu.SwitchMenuItem;
import com.agateau.ui.menu.TabMenuItem;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.agateau.utils.PlatformUtils;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

/** The config screen */
public class ConfigScreen extends PwStageScreen {
    public static class WebSiteLinkInfo {
        public String url;
        public String label;
        public String buttonText;

        public WebSiteLinkInfo(String url, String label, String buttonText) {
            this.url = url;
            this.label = label;
            this.buttonText = buttonText;
        }
    }

    private final PwGame mGame;

    private static WebSiteLinkInfo sWebSiteLinkInfo =
            new WebSiteLinkInfo(
                    "https://agateau.com/support/",
                    tr(
                            "Pixel Wheels is free, but you can support its\ndevelopment in various ways."),
                    tr("VISIT SUPPORT PAGE"));

    Menu mMenu;
    TabMenuItem mTabMenuItem;
    MenuItemGroup mLanguageGroup;

    interface GameInputHandlerConfigScreenFactory {
        Screen createScreen(PwGame game, int playerIdx);
    }

    private static final GameInputHandlerConfigScreenFactory sGamepadConfigScreenFactory =
            (game, playerIdx) -> new GamepadConfigScreen(game, playerIdx);

    private static final GameInputHandlerConfigScreenFactory sKeyboardConfigScreenFactory =
            (game, playerIdx) -> new KeyboardConfigScreen(game, playerIdx);

    public static void setWebSiteLinkInfo(WebSiteLinkInfo info) {
        sWebSiteLinkInfo = info;
    }

    public ConfigScreen(PwGame game) {
        super(game.getAssets().ui);
        mGame = game;
        setupUi();
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new ConfigScreen(mGame));
            }
        };
    }

    private void setupUi() {
        final GameConfig gameConfig = mGame.getConfig();

        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);

        AnchorGroup root = (AnchorGroup) builder.build(FileUtils.assets("screens/config.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        mMenu = builder.getActor("menu");
        mMenu.setLabelColumnWidth(250);

        mTabMenuItem = new TabMenuItem(mMenu);
        mMenu.addItem(mTabMenuItem);

        addAudioVideoTab(gameConfig);
        addControlsTab();
        addOthersTab(gameConfig);
        addAboutTab();

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });
    }

    private void addAboutTab() {
        MenuItemGroup group = mTabMenuItem.addPage(tr("About"));
        group.setWidth(800);
        group.addLabel(StringUtils.format(tr("Pixel Wheels %s"), VersionInfo.VERSION));
        group.addButton(tr("CREDITS"))
                .setParentWidthRatio(0.5f)
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                mGame.pushScreen(new CreditsScreen(mGame));
                            }
                        });
        // This is a ugly hack, but it should do for now
        LabelMenuItem labelMenuItem = group.addLabel(sWebSiteLinkInfo.label);
        labelMenuItem.setWrap(true);
        group.addButton(sWebSiteLinkInfo.buttonText)
                .setParentWidthRatio(0.5f)
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                PlatformUtils.openURI(sWebSiteLinkInfo.url);
                            }
                        });
    }

    private void addOthersTab(GameConfig gameConfig) {
        MenuItemGroup group = mTabMenuItem.addPage(tr("Others"));
        mLanguageGroup = group;

        SelectorMenuItem<String> languageItem = createLanguageSelectorItem(gameConfig);
        group.addItemWithLabel(tr("Language:"), languageItem);
        group.addSpacer();

        ButtonMenuItem developerButton = new ButtonMenuItem(mMenu, tr("DEVELOPER OPTIONS"));
        developerButton
                .getActor()
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                mGame.pushScreen(new DebugScreen(mGame));
                            }
                        });
        group.addItemWithLabel(tr("Internal:"), developerButton);
    }

    private void addControlsTab() {
        MenuItemGroup group = mTabMenuItem.addPage(tr("Controls"));

        if (PlatformUtils.isDesktop()) {
            for (int idx = 0; idx < Constants.MAX_PLAYERS; ++idx) {
                setupInputSelector(
                        mMenu, group, StringUtils.format(tr("Player #%d:"), idx + 1), idx);
            }
        } else {
            setupInputSelector(mMenu, group, tr("Controls:"), 0);
        }
    }

    private void addAudioVideoTab(GameConfig gameConfig) {
        MenuItemGroup group = mTabMenuItem.addPage(tr("Audio & Video"));

        final SwitchMenuItem soundFxSwitch = new SwitchMenuItem(mMenu);
        soundFxSwitch.setChecked(gameConfig.playSoundFx);
        soundFxSwitch
                .getActor()
                .addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                gameConfig.playSoundFx = soundFxSwitch.isChecked();
                                gameConfig.flush();
                            }
                        });
        group.addItemWithLabel(tr("Sound FX:"), soundFxSwitch);

        final SwitchMenuItem musicSwitch = new SwitchMenuItem(mMenu);
        musicSwitch.setChecked(gameConfig.playMusic);
        musicSwitch
                .getActor()
                .addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                gameConfig.playMusic = musicSwitch.isChecked();
                                gameConfig.flush();
                            }
                        });
        group.addItemWithLabel(tr("Music:"), musicSwitch);

        if (PlatformUtils.isDesktop()) {
            final SwitchMenuItem fullscreenSwitch = new SwitchMenuItem(mMenu);
            fullscreenSwitch.setChecked(gameConfig.fullscreen);
            fullscreenSwitch
                    .getActor()
                    .addListener(
                            new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    gameConfig.fullscreen = fullscreenSwitch.isChecked();
                                    mGame.setFullscreen(gameConfig.fullscreen);
                                    gameConfig.flush();
                                }
                            });
            group.addItemWithLabel(tr("Fullscreen:"), fullscreenSwitch);
        }
    }

    private SelectorMenuItem<String> createLanguageSelectorItem(GameConfig gameConfig) {
        final SelectorMenuItem<String> languageItem = new SelectorMenuItem<>(mMenu);
        for (Language language : Language.ALL) {
            languageItem.addEntry(language.name, language.id);
        }
        languageItem.setCurrentData(gameConfig.languageId);
        languageItem
                .getActor()
                .addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                gameConfig.languageId = languageItem.getCurrentData();
                                gameConfig.flush();
                                changeScreenLanguage();
                            }
                        });
        return languageItem;
    }

    private void changeScreenLanguage() {
        ConfigScreen screen = new ConfigScreen(mGame);
        // Reselect the language selector to make the replace seamless
        screen.mTabMenuItem.setCurrentPage(screen.mLanguageGroup);
        screen.mMenu.setCurrentItem(screen.mLanguageGroup);
        mGame.replaceScreen(screen);
    }

    class InputSelectorController {
        private final SelectorMenuItem<GameInputHandlerFactory> mSelector;
        private final ButtonMenuItem mConfigureButton;
        private final int mPlayerIdx;

        InputSelectorController(
                SelectorMenuItem<GameInputHandlerFactory> selector,
                ButtonMenuItem configureButton,
                int idx) {
            mSelector = selector;
            mConfigureButton = configureButton;
            mPlayerIdx = idx;

            UiAssets uiAssets = mGame.getAssets().ui;
            Array<GameInputHandlerFactory> inputFactories =
                    GameInputHandlerFactories.getAvailableFactories();
            for (GameInputHandlerFactory factory : inputFactories) {
                String iconName = "input-icons/" + factory.getId();
                Drawable drawable = new TextureRegionDrawable(uiAssets.atlas.findRegion(iconName));
                selector.addEntry(drawable, factory.getName(), factory);
            }

            selector.getActor()
                    .addListener(
                            new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    onInputChanged();
                                }
                            });

            configureButton.addListener(
                    new MenuItemListener() {
                        @Override
                        public void triggered() {
                            onConfigureClicked();
                        }
                    });
        }

        private void onInputChanged() {
            GameInputHandlerFactory factory = mSelector.getCurrentData();
            mGame.getConfig().setPlayerInputHandlerFactory(mPlayerIdx, factory);
            mGame.getConfig().flush();
            updateConfigureButton();
        }

        private void onConfigureClicked() {
            GameInputHandlerFactory factory = mSelector.getCurrentData();
            GameInputHandlerConfigScreenFactory configScreenFactory =
                    getInputConfigScreenFactory(factory);
            Assert.check(configScreenFactory != null, "No config screen for this game factory");
            mGame.pushScreen(configScreenFactory.createScreen(mGame, mPlayerIdx));
        }

        private void setStartupState() {
            GameInputHandlerFactory factory =
                    mGame.getConfig().getPlayerInputHandlerFactory(mPlayerIdx);
            mSelector.setCurrentData(factory);
            updateConfigureButton();
        }

        private void updateConfigureButton() {
            GameInputHandlerFactory factory = mSelector.getCurrentData();
            boolean canBeConfigured = getInputConfigScreenFactory(factory) != null;
            mConfigureButton.setDisabled(!canBeConfigured);
        }
    }

    private void setupInputSelector(Menu menu, MenuItemGroup group, String label, final int idx) {
        SelectorMenuItem<GameInputHandlerFactory> selector = new SelectorMenuItem<>(menu);
        group.addItemWithLabel(label, selector);

        ButtonMenuItem configureButton = new ButtonMenuItem(menu, tr("CONFIGURE"));
        group.addItemWithLabel("", configureButton);

        InputSelectorController controller =
                new InputSelectorController(selector, configureButton, idx);
        controller.setStartupState();
    }

    private GameInputHandlerConfigScreenFactory getInputConfigScreenFactory(
            GameInputHandlerFactory factory) {
        if (factory instanceof GamepadInputHandler.Factory) {
            return sGamepadConfigScreenFactory;
        } else if (factory instanceof KeyboardInputHandler.Factory) {
            return sKeyboardConfigScreenFactory;
        } else {
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }
}
