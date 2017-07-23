package com.agateau.tinywheels;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * A SpinBox for integers
 */
public class IntSpinBox extends SpinBox<Integer> {
    public IntSpinBox(int min, int max, Skin skin) {
        super(min, max, skin);
    }

    @Override
    protected Integer tFromFloat(float f) {
        return Integer.valueOf((int)f);
    }

    @Override
    protected float floatFromT(Integer integer) {
        return integer.floatValue();
    }

    @Override
    protected String stringFromT(Integer integer) {
        return integer.toString();
    }
}
