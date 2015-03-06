package com.greenyetilab.race;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.anchor.Anchor;
import com.greenyetilab.utils.anchor.AnchorGroup;
import com.greenyetilab.utils.anchor.SizeRule;

/**
 * An actor to select the input handler
 */
public class GameInputHandlerSelector extends AnchorGroup {
    private final Label mNameLabel;
    private final Label mDescriptionLabel;
    private Array<GameInputHandler> mHandlers;
    private int mIndex = 0;

    public GameInputHandlerSelector(Skin skin) {
        setSpacing(20);
        mHandlers = GameInputHandlers.getAvailableHandlers();
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

        String inputHandlerName = RaceGame.getPreferences().getString("input", "");
        setIndex(findHandler(inputHandlerName));

        setHeight(mNameLabel.getHeight() + mDescriptionLabel.getHeight());
    }

    public int findHandler(String name) {
        for (int i = 0; i < mHandlers.size; ++i) {
            if (mHandlers.get(i).getClass().getSimpleName().equals(name)) {
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
            mIndex = mHandlers.size - 1;
        } else if (mIndex >= mHandlers.size) {
            mIndex = 0;
        }
        GameInputHandler handler = mHandlers.get(mIndex);
        mNameLabel.setText(handler.getName());

        mDescriptionLabel.setText(handler.getDescription());

        Preferences prefs = RaceGame.getPreferences();
        prefs.putString("input", handler.getClass().getSimpleName());
        prefs.flush();
    }
}
