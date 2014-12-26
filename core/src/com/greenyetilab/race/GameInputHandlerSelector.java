package com.greenyetilab.race;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * An actor to select the input handler
 */
public class GameInputHandlerSelector extends HorizontalGroup {
    private final Label mLabel;
    private Array<GameInputHandler> mHandlers;
    private int mIndex = 0;

    public GameInputHandlerSelector(Skin skin) {
        space(20);
        mHandlers = GameInputHandlers.getAvailableHandlers();
        addButton("icon-left", skin, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndex(mIndex - 1);
            }
        });

        mLabel = new Label("", skin);
        mLabel.setWidth(150);
        addActor(mLabel);

        addButton("icon-right", skin, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndex(mIndex + 1);
            }
        });

        String inputHandlerName = RaceGame.getPreferences().getString("input", "");
        setIndex(findHandler(inputHandlerName));
        setHeight(getPrefHeight());
    }

    public int findHandler(String name) {
        for (int i = 0; i < mHandlers.size; ++i) {
            if (mHandlers.get(i).getClass().getSimpleName().equals(name)) {
                return i;
            }
        }
        return 0;
    }

    private void addButton(String imageName, Skin skin, ClickListener listener) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get("default", ImageButton.ImageButtonStyle.class));
        style.imageUp = skin.getDrawable(imageName);
        ImageButton button = new ImageButton(style);
        button.addListener(listener);
        addActor(button);
    }

    private void setIndex(int value) {
        mIndex = value;
        if (mIndex < 0) {
            mIndex = mHandlers.size - 1;
        } else if (mIndex >= mHandlers.size) {
            mIndex = 0;
        }
        GameInputHandler handler = mHandlers.get(mIndex);
        mLabel.setText(handler.getName());
        Preferences prefs = RaceGame.getPreferences();
        prefs.putString("input", handler.getClass().getSimpleName());
        prefs.flush();
    }
}
