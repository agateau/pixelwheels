package com.greenyetilab.tinywheels;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * A SpinBox for floats
 */
public class FloatSpinBox extends SpinBox<Float> {
    public FloatSpinBox(float min, float max, Skin skin) {
        super(min, max, skin);
    }

    @Override
    protected Float tFromFloat(float f) {
        return Float.valueOf(f);
    }

    @Override
    protected float floatFromT(Float f) {
        return f.floatValue();
    }

    @Override
    protected String stringFromT(Float f) {
        return String.format("%.2f", f.floatValue());
    }
}
