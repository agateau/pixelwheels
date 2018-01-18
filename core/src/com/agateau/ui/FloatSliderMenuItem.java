package com.agateau.ui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

/**
 * A Menu item to select float values
 */
public class FloatSliderMenuItem extends RangeMenuItem {
    private Label mLabel;
    private float mMin = 0;
    private float mMax = 100;
    private float mStepSize = 1;
    private float mValue = 0;

    public FloatSliderMenuItem(Menu menu) {
        super(menu);
        setStepSize(1);
    }

    public void setStepSize(float stepSize) {
        mStepSize = stepSize;
    }

    public void setRange(float min, float max) {
        mMin = min;
        mMax = max;
        setValue(getValue());
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float value) {
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
    public void updateMainActor() {
        if (mLabel == null) {
            return;
        }
        mLabel.setText(String.valueOf(getValue()));
    }

    @Override
    protected void decrease() {
        setValue(mValue - mStepSize);
    }

    @Override
    protected void increase() {
        setValue(mValue + mStepSize);
    }
}
