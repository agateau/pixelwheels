package com.greenyetilab.tinywheels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
public class SpinBox extends HorizontalGroup {
    private int mMinValue;
    private int mMaxValue;
    private int mValue;
    private Label mLabel;
    private int mStepSize = 1;

    public SpinBox(int min, int max, Skin skin) {
        mMinValue = min;
        mMaxValue = max;
        mLabel = new Label("", skin);
        Button decButton = new TextButton("-", skin);
        Button incButton = new TextButton("+", skin);

        decButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setValue(mValue - mStepSize);
                // Cancel the event so that the button does not stay down
                event.cancel();
            }
        });
        incButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setValue(mValue + mStepSize);
                event.cancel();
            }
        });

        addActor(mLabel);
        addActor(decButton);
        addActor(incButton);
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        value = MathUtils.clamp(value, mMinValue, mMaxValue);
        if (mValue == value) {
            return;
        }
        mValue = value;

        mLabel.setText(String.valueOf(mValue) + " ");

        ChangeListener.ChangeEvent changeEvent = Pools.obtain(ChangeListener.ChangeEvent.class);
        fire(changeEvent);
        Pools.free(changeEvent);
    }

    public void setStepSize(int stepSize) {
        mStepSize = stepSize;
    }

    public int getStepSize() {
        return mStepSize;
    }
}
