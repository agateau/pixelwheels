package com.agateau.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

/**
 * A Menu item to select int values
 */
public class IntSliderMenuItem extends RangeMenuItem {
    private Label mLabel;
    private int mMin = 0;
    private int mMax = 100;
    private int mStepSize = 1;
    private int mValue = 0;

    public IntSliderMenuItem(Menu menu) {
        super(menu);
        setStepSize(1);
    }

    public void setStepSize(int stepSize) {
        mStepSize = stepSize;
    }

    public void setRange(int min, int max) {
        mMin = min;
        mMax = max;
        setValue(getValue());
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = MathUtils.clamp(value, mMin, mMax);
        updateMainActor();
    }

    @Override
    protected Actor createMainActor(Menu menu) {
        mLabel = new Label("", menu.getSkin());
        mLabel.setAlignment(Align.center);
        return mLabel;
    }

    @Override
    protected void updateMainActor() {
        if (mLabel == null) {
            return;
        }
        mLabel.setText(String.valueOf(getValue()));
    }

    @Override
    protected void decreaseValue() {
        setValue(mValue - mStepSize);
    }

    @Override
    protected void increaseValue() {
        setValue(mValue + mStepSize);
    }
}
