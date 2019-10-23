/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.ui.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

/** A Menu item to select int values */
public class SliderMenuItem extends RangeMenuItem {
    private static final float NO_DIVISOR = -1;

    private static class SliderMainActor extends Actor {
        private final Skin mSkin;
        private final SliderMenuItemStyle mStyle;
        private final BitmapFont mFont;
        private final SliderMenuItem mMenuItem;
        private String mText;
        private float mPercent = 0;

        SliderMainActor(Skin skin, final SliderMenuItem menuItem) {
            mSkin = skin;
            mStyle = mSkin.get("default", SliderMenuItemStyle.class);
            mFont = mSkin.get("default-font", BitmapFont.class);
            mMenuItem = menuItem;
            setTouchable(Touchable.enabled);

            addCaptureListener(
                    new InputListener() {
                        @Override
                        public boolean touchDown(
                                InputEvent event, float x, float y, int pointer, int button) {
                            mMenuItem.onSliderChanged(computePercent(x));
                            return true;
                        }

                        @Override
                        public void touchDragged(InputEvent event, float x, float y, int pointer) {
                            mMenuItem.onSliderChanged(computePercent(x));
                        }

                        private float computePercent(float x) {
                            float handleWidth = mStyle.handle.getMinWidth();
                            float fullWidth = getWidth() - mStyle.framePadding * 2 - handleWidth;
                            return (x - mStyle.framePadding - handleWidth / 2) / fullWidth;
                        }
                    });
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float handleWidth = mStyle.handle.getMinWidth();
            float fullWidth = getWidth() - mStyle.framePadding * 2 - handleWidth;
            float handleX = getX() + mStyle.framePadding + fullWidth * mPercent;

            mStyle.handle.draw(batch, handleX, getY(), handleWidth, getHeight());

            float y = getY() + (mFont.getCapHeight() + getHeight()) / 2;
            mFont.draw(batch, mText, getX(), y, getWidth(), Align.center, /* wrap= */ false);
        }

        public void setPercent(float percent) {
            mPercent = percent;
        }

        public void setText(String text) {
            mText = text;
        }
    }

    public static class SliderMenuItemStyle extends RangeMenuItemStyle {
        Drawable handle;
    }

    private SliderMainActor mMainActor;
    private int mMin = 0;
    private int mMax = 100;
    private int mStepSize = 1;
    private float mDivisor = NO_DIVISOR;
    private int mValue = 0;

    public SliderMenuItem(Menu menu) {
        super(menu);
    }

    public void setRange(int min, int max) {
        setRange(min, max, 1);
    }

    public void setRange(int min, int max, int stepSize) {
        mMin = min;
        mMax = max;
        mStepSize = stepSize;
        mDivisor = NO_DIVISOR;
        setIntValue(getIntValue());
    }

    public void setRange(float min, float max, float stepSize) {
        mStepSize = 1;
        mDivisor = 1 / stepSize;
        mMin = (int) (min * mDivisor);
        mMax = (int) (max * mDivisor);
        setIntValue(getIntValue());
    }

    public int getIntValue() {
        return mValue;
    }

    public void setIntValue(int value) {
        mValue = MathUtils.clamp(value, mMin, mMax);
        int reminder = (mValue - mMin) % mStepSize;
        if (reminder > 0) {
            mValue -= reminder;
        }
        updateMainActor();
    }

    public void setFloatValue(float value) {
        setIntValue((int) (value * mDivisor));
    }

    public float getFloatValue() {
        return mValue / mDivisor;
    }

    @Override
    protected Actor createMainActor(Menu menu) {
        mMainActor = new SliderMainActor(menu.getSkin(), this);
        return mMainActor;
    }

    @Override
    public void updateMainActor() {
        if (mMainActor == null) {
            return;
        }
        mMainActor.setPercent((mValue - mMin) / (float) (mMax - mMin));
        mMainActor.setText(formatValue(getIntValue()));
    }

    @Override
    protected void decrease() {
        setIntValue(mValue - mStepSize);
    }

    @Override
    protected void increase() {
        setIntValue(mValue + mStepSize);
    }

    protected String formatValue(int value) {
        if (mDivisor == NO_DIVISOR) {
            return String.valueOf(value);
        } else {
            return String.valueOf(value / mDivisor);
        }
    }

    protected float getDivisor() {
        return mDivisor;
    }

    private void onSliderChanged(float percent) {
        setIntValue(mMin + (int) (percent * (mMax - mMin)));
        fireChangeEvent();
    }
}
