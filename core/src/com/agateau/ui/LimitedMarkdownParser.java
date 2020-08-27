/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;

/**
 * A very limited markdown parser
 *
 * <p>Reads a .md and creates Actors in a group to represent the text.
 *
 * <p>Supported syntax:
 *
 * <ul>
 *   <li>"# text" and "## text" for titles
 *   <li>"- text" for lists. No support for nested lists
 * </ul>
 *
 * #
 */
public class LimitedMarkdownParser {
    private final VerticalGroup mParent;
    private final Skin mSkin;

    public static void createActors(VerticalGroup parent, Skin skin, String text) {
        LimitedMarkdownParser parser = new LimitedMarkdownParser(parent, skin);
        parser.parse(text);
    }

    private LimitedMarkdownParser(VerticalGroup parent, Skin skin) {
        mParent = parent;
        mSkin = skin;
    }

    private void parse(String text) {
        for (String line : text.split("\n")) {
            if (line.startsWith("# ")) {
                addTitleLabel(line.substring(2), 1);
            } else if (line.startsWith("## ")) {
                addTitleLabel(line.substring(3), 2);
            } else if (line.startsWith("- ")) {
                addListLabel(line.substring(2));
            } else {
                addParagraph(line);
            }
        }
    }

    private void addTitleLabel(String text, int level) {
        addActor(createLabel(text, "mdH" + level));
    }

    private void addListLabel(String text) {
        addActor(createLabel("• " + text));
    }

    private void addParagraph(String text) {
        addActor(createLabel(text));
    }

    private void addActor(Actor actor) {
        mParent.addActor(actor);
    }

    private Label createLabel(String text) {
        return createLabel(text, "default");
    }

    private Label createLabel(String text, String styleName) {
        Label label = new Label(text, mSkin, styleName);
        label.setAlignment(Align.center);
        label.setWrap(true);
        label.setWidth(mParent.getWidth());
        label.setHeight(label.getPrefHeight());
        return label;
    }
}
