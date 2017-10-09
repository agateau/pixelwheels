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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pools;

/**
 * An integer spinbox
 */
public abstract class SpinBox<T extends Number> extends HorizontalGroup {
    private float mMinValue;
    private float mMaxValue;
    private float mValue;
    private Label mLabel;
    private float mStepSize = 1;

    public SpinBox(T min, T max, Skin skin) {
        mMinValue = floatFromT(min);
        mMaxValue = floatFromT(max);
        mLabel = new Label("", skin);
        Button decButton = new TextButton("-", skin);
        Button incButton = new TextButton("+", skin);

        decButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setValue(tFromFloat(mValue - mStepSize));
                // Cancel the event so that the button does not stay down
                event.cancel();
            }
        });
        incButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setValue(tFromFloat(mValue + mStepSize));
                event.cancel();
            }
        });

        addActor(mLabel);
        addActor(decButton);
        addActor(incButton);
    }

    protected abstract T tFromFloat(float f);

    protected abstract float floatFromT(T t);

    protected abstract String stringFromT(T t);

    public T getValue() {
        return tFromFloat(mValue);
    }

    public void setValue(T tvalue) {
        float value = floatFromT(tvalue);
        value = MathUtils.clamp(value, mMinValue, mMaxValue);
        if (mValue == value) {
            return;
        }
        mValue = value;

        mLabel.setText(stringFromT(tFromFloat(mValue)) + " ");

        ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
        fire(changeEvent);
        Pools.free(changeEvent);
    }

    public void setStepSize(T stepSize) {
        mStepSize = floatFromT(stepSize);
    }

    public T getStepSize() {
        return tFromFloat(mStepSize);
    }
}
