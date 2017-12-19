package com.agateau.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Base class for all menu items with plus|minus buttons and a UI between those
 */
abstract class BaseRangeMenuItem extends HorizontalGroup implements MenuItem {
    private final Menu mMenu;
    private final Button mLeftButton;
    private final Button mRightButton;
    private final Rectangle mRect = new Rectangle();
    private Actor mMainActor;

    private int mMin = 0;
    private int mMax = 0;
    private int mValue = 0;

    public BaseRangeMenuItem(Menu menu) {
        mMenu = menu;
        mLeftButton = createButton("-", menu.getSkin());
        mLeftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goLeft();
            }
        });

        mRightButton = createButton("+", menu.getSkin());
        mRightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goRight();
            }
        });
    }

    @Override
    public void layout() {
        if (mMainActor == null) {
            mMainActor = createMainActor(mMenu);
            addActor(mLeftButton);
            addActor(mMainActor);
            addActor(mRightButton);
            updateMainActor();
        }
        super.layout();
    }

    @SuppressWarnings("SameParameterValue")
    public void setRange(int min, int max) {
        mMin = min;
        mMax = max;
        setValue(MathUtils.clamp(mValue, mMin, mMax));
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
        if (mMainActor != null) {
            updateMainActor();
        }
    }

    /**
     * Must create the actor to show between the left and right buttons
     */
    protected abstract Actor createMainActor(Menu menu);

    /**
     * Called when main actor must be updated because value changed
     */
    protected abstract void updateMainActor();

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void trigger() {

    }

    @Override
    public boolean goUp() {
        return false;
    }

    @Override
    public boolean goDown() {
        return false;
    }

    @Override
    public void goLeft() {
        if (mValue > mMin) {
            setValue(mValue - 1);
        } else {
            setValue(mMax);
        }
    }

    @Override
    public void goRight() {
        if (mValue < mMax) {
            setValue(mValue + 1);
        } else {
            setValue(mMin);
        }
    }

    @Override
    public Rectangle getFocusRectangle() {
        mRect.x = 0;
        mRect.y = 0;
        mRect.width = getWidth();
        mRect.height = getHeight();
        return mRect;
    }

    private static Button createButton(String text, Skin skin) {
        return new TextButton(text, skin);
    }
}
