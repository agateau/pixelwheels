package com.agateau.ui;

/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/** An image which can show an animation, can be used in UiBuilder too */
public class AnimatedImage extends Image {
    private Animation<TextureRegion> mAnimation;
    private float mTime = 0;
    private final TextureRegionDrawable mDrawable = new TextureRegionDrawable();

    public AnimatedImage(Animation<TextureRegion> animation) {
        setAnimation(animation);
    }

    public AnimatedImage() {}

    @Override
    public void act(float delta) {
        super.act(delta);
        if (mAnimation == null) {
            return;
        }
        mTime += delta;
        TextureRegion region = mAnimation.getKeyFrame(mTime, /* looping */ true);
        mDrawable.setRegion(region);
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        mAnimation = animation;
        setDrawable(mDrawable);
        mDrawable.setRegion(mAnimation.getKeyFrame(0));
        pack();
        mTime = 0;
    }

    public void setStartTime(float time) {
        mTime = time;
    }
}
