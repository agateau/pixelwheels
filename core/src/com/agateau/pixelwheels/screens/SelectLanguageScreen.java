/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.pixelwheels.Language;
import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.CollectionUtils;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import java.util.Map;

/** Select the game language */
public class SelectLanguageScreen extends PwStageScreen {
    private static final int FONT_SIZE = 24;
    private static final float ITEM_HEIGHT = 40;

    private static class LanguageSelectorRenderer implements GridMenuItem.ItemRenderer<Language> {
        private final HashMap<String, BitmapFont> mFontForLanguage;
        private final Rectangle mRectangle = new Rectangle();

        public LanguageSelectorRenderer(HashMap<String, BitmapFont> fontForLanguage) {
            mFontForLanguage = fontForLanguage;
        }

        @Override
        public Rectangle getItemRectangle(float width, float height, Language item) {
            mRectangle.set(0, 0, width, height);
            return mRectangle;
        }

        @Override
        public void render(
                Batch batch, float x, float y, float width, float height, Language item) {
            BitmapFont font = mFontForLanguage.get(item.id);
            y += height - (height - font.getCapHeight()) / 2;
            font.setColor(batch.getColor());
            font.draw(batch, item.name, x, y, width, Align.center, /* wrap */ false);
        }

        @Override
        public boolean isItemEnabled(Language item) {
            return true;
        }
    }

    private final PwGame mGame;

    SelectLanguageScreen(PwGame game) {
        super(game.getAssets().ui);
        mGame = game;
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new SelectLanguageScreen(mGame));
            }
        };
        setupUi();
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);

        AnchorGroup root =
                (AnchorGroup) builder.build(FileUtils.assets("screens/selectlanguage.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");
        GridMenuItem<Language> languageSelector = createLanguageSelector(menu);
        menu.addItem(languageSelector);

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });
    }

    private GridMenuItem<Language> createLanguageSelector(Menu menu) {
        GridMenuItem<Language> languageSelector = new GridMenuItem<>(menu);
        Array<Language> languages = mGame.getAssets().languages.getAll();
        languages.sort((l1, l2) -> l1.name.compareToIgnoreCase(l2.name));
        languageSelector.setItems(languages);
        languageSelector.setItemSize(menu.getWidth(), ITEM_HEIGHT);
        languageSelector.setColumnCount(1);
        languageSelector.setTouchUiConfirmMode(GridMenuItem.TouchUiConfirmMode.SINGLE_TOUCH);

        HashMap<String, BitmapFont> fontForLanguage = getFontForLanguage(languages);

        languageSelector.setItemRenderer(new LanguageSelectorRenderer(fontForLanguage));

        // Select current language
        for (Language language : languages) {
            if (language.id.equals(mGame.getConfig().languageId)) {
                languageSelector.setCurrent(language);
            }
        }

        languageSelector.setSelectionListener(
                new GridMenuItem.SelectionListener<Language>() {
                    @Override
                    public void currentChanged(Language item, int index) {}

                    @Override
                    public void selectionConfirmed() {
                        selectLanguage(languageSelector.getSelected().id);
                    }
                });

        return languageSelector;
    }

    private static HashMap<String, BitmapFont> getFontForLanguage(Array<Language> languages) {
        /*
        Creating fonts for all characters used in the game would be too slow, so we only create the
        characters necessary to render the language names.
        Since some fonts are used for multiple languages, we first gather all the necessary
        characters, then we create the fonts, then we create a map of language => font.
         */
        // For each font, list the required characters
        HashMap<String, String> alphabetForFontName = new HashMap<>();
        for (Language language : languages) {
            String fontName = language.fontSet.defaultFontName;
            String alphabet = CollectionUtils.getOrDefault(alphabetForFontName, fontName, "");
            alphabet += language.name;
            alphabetForFontName.put(fontName, alphabet);
        }

        // Create the fonts
        HashMap<String, BitmapFont> fontForFontName = new HashMap<>();
        for (Map.Entry<String, String> entry : alphabetForFontName.entrySet()) {
            String fontName = entry.getKey();
            String alphabet = entry.getValue();

            FreeTypeFontGenerator generator =
                    new FreeTypeFontGenerator(FileUtils.assets("fonts/" + fontName));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                    new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.characters = alphabet;
            parameter.size = FONT_SIZE;
            BitmapFont font = generator.generateFont(parameter);
            generator.dispose();
            fontForFontName.put(fontName, font);
        }

        // Create the final map
        HashMap<String, BitmapFont> fontForLanguage = new HashMap<>();
        for (Language language : languages) {
            fontForLanguage.put(language.id, fontForFontName.get(language.fontSet.defaultFontName));
        }
        return fontForLanguage;
    }

    private void selectLanguage(String languageId) {
        getStage()
                .getRoot()
                .addAction(
                        Actions.sequence(
                                Actions.alpha(0.3f, 0.1f, Interpolation.pow2Out),
                                Actions.run(() -> doSelectLanguage(languageId))));
    }

    private void doSelectLanguage(String languageId) {
        mGame.getConfig().languageId = languageId;

        // Flushing the config causes the new language to be loaded
        mGame.getConfig().flush();

        ConfigScreen screen = new ConfigScreen(mGame);
        screen.selectLanguageButton();
        mGame.popScreen();
        mGame.replaceScreen(screen);
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }
}
