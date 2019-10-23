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
package com.agateau.pixelwheels.bonus;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.GameWorld;
import com.agateau.pixelwheels.ZLevel;
import com.agateau.pixelwheels.gameobjet.AudioClipper;
import com.agateau.pixelwheels.gameobjet.GameObjectAdapter;
import com.agateau.pixelwheels.sound.AudioManager;
import com.agateau.pixelwheels.utils.BodyRegionDrawer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/** The bonus waiting to be hit by a the player */
public class BonusSpot extends GameObjectAdapter {
    private static final float DISABLED_TIMEOUT = 5;
    private final TextureRegion mRegion;
    private final Sound mSound;
    private final AudioManager mAudioManager;
    private final float mX;
    private final float mY;
    private final Body mBody;
    private float mDisabledTimeout = 0;
    private final BodyRegionDrawer mDrawer = new BodyRegionDrawer();
    private boolean mJustPicked = false;

    public BonusSpot(
            Assets assets, AudioManager audioManager, GameWorld gameWorld, float x, float y) {
        final float U = Constants.UNIT_FOR_PIXEL;
        mAudioManager = audioManager;
        mX = x;
        mY = y;

        mRegion = assets.gift;
        mSound = assets.soundAtlas.get("bonus");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(U * mRegion.getRegionWidth() / 2, U * mRegion.getRegionHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(mX, mY);

        mBody = gameWorld.getBox2DWorld().createBody(bodyDef);
        Fixture fixture = mBody.createFixture(shape, 1f);
        fixture.setSensor(true);
        mBody.setUserData(this);

        mBody.setAngularVelocity(240 * MathUtils.degreesToRadians);

        shape.dispose();
    }

    @Override
    public void act(float delta) {
        if (mDisabledTimeout > 0) {
            // mBody is still active on the first call of act() after pickBonus()
            mBody.setActive(false);
            mDisabledTimeout -= delta;
            if (mDisabledTimeout <= 0) {
                mDisabledTimeout = 0;
                mBody.setActive(true);
            }
        }
    }

    @Override
    public void draw(Batch batch, ZLevel zLevel) {
        if (!mBody.isActive()) {
            return;
        }
        if (zLevel == ZLevel.GROUND) {
            mDrawer.setBatch(batch);
            mDrawer.drawShadow(mBody, mRegion);
        } else if (zLevel == ZLevel.OBSTACLES) {
            mDrawer.setBatch(batch);
            mDrawer.draw(mBody, mRegion);
        }
    }

    @Override
    public void audioRender(AudioClipper audioClipper) {
        if (mJustPicked) {
            float volume = audioClipper.clip(this);
            mAudioManager.play(mSound, volume);
            mJustPicked = false;
        }
    }

    @Override
    public float getX() {
        return mX;
    }

    @Override
    public float getY() {
        return mY;
    }

    public void pickBonus() {
        mDisabledTimeout = DISABLED_TIMEOUT;
        mJustPicked = true;
    }
}
