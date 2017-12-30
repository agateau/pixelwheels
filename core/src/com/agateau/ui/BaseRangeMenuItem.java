package com.agateau.ui;

import com.agateau.ui.anchor.Anchor;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.anchor.EdgeRule;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Base class for all menu items with plus|minus buttons and a UI between those
 */
abstract class BaseRangeMenuItem extends AnchorGroup implements MenuItem {
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
                if (mValue > mMin) {
                    setValue(mValue - 1);
                } else {
                    setValue(mMax);
                }
            }
        });

        mRightButton = createButton("+", menu.getSkin());
        mRightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mValue < mMax) {
                    setValue(mValue + 1);
                } else {
                    setValue(mMin);
                }
            }
        });

        setHeight(mLeftButton.getPrefHeight());
    }

    @Override
    public void layout() {
        if (mMainActor == null) {
            mMainActor = createMainActor(mMenu);
            addPositionRule(mLeftButton, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT);
            addPositionRule(mRightButton, Anchor.TOP_RIGHT, this, Anchor.TOP_RIGHT);

            addPositionRule(mMainActor, Anchor.TOP_LEFT, mLeftButton, Anchor.TOP_RIGHT);
            addRule(new EdgeRule(mMainActor, EdgeRule.Edge.RIGHT, mRightButton, EdgeRule.Edge.LEFT));
            addRule(new EdgeRule(mMainActor, EdgeRule.Edge.BOTTOM, mRightButton, EdgeRule.Edge.BOTTOM));

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
        Scene2dUtils.simulateClick(mLeftButton);
    }

    @Override
    public void goRight() {
        Scene2dUtils.simulateClick(mRightButton);
    }

    @Override
    public Rectangle getFocusRectangle() {
        mRect.x = 0;
        mRect.y = 0;
        mRect.width = getWidth();
        mRect.height = getHeight();
        return mRect;
    }

    @Override
    public void setDefaultColumnWidth(float width) {
        setWidth(width);
    }

    private static Button createButton(String text, Skin skin) {
        return new TextButton(text, skin);
    }
}
