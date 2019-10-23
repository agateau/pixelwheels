/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
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
package com.agateau.pixelwheels.gameobjet;

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.sound.AudioManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/** A generic short-animation game object */
public class AnimationObject extends GameObjectAdapter implements Pool.Poolable, Disposable {
    private static final ReflectionPool<AnimationObject> sPool =
            new ReflectionPool<>(AnimationObject.class);
    private float mTime;
    private Animation<TextureRegion> mAnimation;
    private float mPosX;
    private float mPosY;

    private Sound mSound;
    private AudioManager mAudioManager;

    @Override
    public void reset() {}

    @Override
    public void dispose() {
        sPool.free(this);
    }

    @Override
    public void act(float delta) {
        mTime += delta;
        if (mTime < 0) {
            return;
        }
        if (mAnimation.isAnimationFinished(mTime)) {
            setFinished(true);
        }
    }

    @Override
    public void draw(Batch batch, ZLevel zLevel) {
        if (mTime < 0) {
            return;
        }
        if (zLevel == ZLevel.OBSTACLES) {
            TextureRegion region = mAnimation.getKeyFrame(mTime);
            float w = Constants.UNIT_FOR_PIXEL * region.getRegionWidth();
            float h = Constants.UNIT_FOR_PIXEL * region.getRegionHeight();
            batch.draw(region, mPosX - w / 2, mPosY - h / 2, w, h);
        }
    }

    @Override
    public void audioRender(AudioClipper clipper) {
        if (mSound != null) {
            mAudioManager.play(mSound, clipper.clip(this));
            // Make sure we don't play twice
            mSound = null;
        }
    }

    @Override
    public float getX() {
        return mPosX;
    }

    @Override
    public float getY() {
        return mPosY;
    }

    public void initAudio(AudioManager audioManager, Sound sound) {
        mAudioManager = audioManager;
        mSound = sound;
    }

    public static AnimationObject create(
            Animation<TextureRegion> animation, float posX, float posY) {
        return create(animation, posX, posY, 0);
    }

    public static AnimationObject create(
            Animation<TextureRegion> animation, float posX, float posY, float delay) {
        AnimationObject obj = sPool.obtain();
        obj.mTime = -delay;
        obj.mAnimation = animation;
        obj.mPosX = posX;
        obj.mPosY = posY;
        obj.mSound = null;
        obj.setFinished(false);
        return obj;
    }
}
