/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Tiny Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.tinywheels;

import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.anchor.SizeRule;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * An actor to select the input handler
 */
public class GameInputHandlerSelector extends AnchorGroup {
    private final GameConfig mGameConfig;
    private final Label mNameLabel;
    private final Label mDescriptionLabel;
    private Array<GameInputHandlerFactory> mFactories;
    private int mIndex = 0;

    public GameInputHandlerSelector(GameConfig gameConfig, Skin skin) {
        setSpacing(20);
        mGameConfig = gameConfig;
        mFactories = GameInputHandlerFactories.getAvailableFactories();
        ImageButton leftButton = addButton("icon-left", skin, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndex(mIndex - 1);
            }
        });

        mNameLabel = new Label("", skin);

        mDescriptionLabel = new Label("", skin);
        mDescriptionLabel.setWrap(true);

        ImageButton rightButton = addButton("icon-right", skin, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndex(mIndex + 1);
            }
        });

        mNameLabel.setHeight(rightButton.getHeight());
        mDescriptionLabel.setHeight(rightButton.getHeight() * 1.5f);
        mDescriptionLabel.setAlignment(Align.topLeft, Align.left);

        addPositionRule(leftButton, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT);
        addPositionRule(mNameLabel, Anchor.TOP_LEFT, leftButton, Anchor.TOP_RIGHT, 1, 0);
        addPositionRule(rightButton, Anchor.TOP_RIGHT, this, Anchor.TOP_RIGHT);
        addPositionRule(mDescriptionLabel, Anchor.TOP_LEFT, leftButton, Anchor.BOTTOM_LEFT, 0, -0.5f);
        addSizeRule(mDescriptionLabel, this, 1, SizeRule.IGNORE);

        setIndex(findHandler(mGameConfig.input));

        setHeight(mNameLabel.getHeight() + mDescriptionLabel.getHeight());
    }

    public int findHandler(String id) {
        for (int i = 0; i < mFactories.size; ++i) {
            if (mFactories.get(i).getId().equals(id)) {
                return i;
            }
        }
        return 0;
    }

    private ImageButton addButton(String imageName, Skin skin, ClickListener listener) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get("default", ImageButton.ImageButtonStyle.class));
        style.imageUp = skin.getDrawable(imageName);
        ImageButton button = new ImageButton(style);
        button.addListener(listener);
        return button;
    }

    private void setIndex(int value) {
        mIndex = value;
        if (mIndex < 0) {
            mIndex = mFactories.size - 1;
        } else if (mIndex >= mFactories.size) {
            mIndex = 0;
        }
        GameInputHandlerFactory factory = mFactories.get(mIndex);
        mNameLabel.setText(factory.getName());

        mDescriptionLabel.setText(factory.getDescription());

        mGameConfig.input = factory.getId();
        mGameConfig.flush();
    }
}
