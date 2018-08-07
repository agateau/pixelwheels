/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
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
package com.agateau.pixelwheels.bonus;

import com.agateau.pixelwheels.GameWorld;
import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class MissileGuidingSystem {
    private static final float MAX_ROTATION = 5f * MathUtils.degRad;
    private Body mBody;
    private float mForwardForce;
    private float MAX_SPEED = 80 * 3.6f;

    public void init(Body body, float force) {
        mBody = body;
        mForwardForce = force;
    }

    public void act(Vector2 target) {
        move();
        if (target == null) {
            return;
        }
        float impulse = computeImpulse(target);
        mBody.applyAngularImpulse(impulse, true);
    }

    private Vector2 mTmp = new Vector2();
    private float computeImpulse(Vector2 target) {
        /*
                       x target
               ,
              /
             x body.worldCenter
            /
           '

         */
        mTmp.set(target).sub(mBody.getWorldCenter());
        float bodyAngle = AgcMathUtils.normalizeAnglePiRad(mBody.getAngle());
        float desiredAngle = AgcMathUtils.normalizeAnglePiRad(mTmp.angleRad());

        float nextAngle = bodyAngle + mBody.getAngularVelocity() * GameWorld.BOX2D_TIME_STEP;
        float totalRotation = MathUtils.clamp(desiredAngle - nextAngle, -MAX_ROTATION, MAX_ROTATION);
        float desiredAngularVelocity = totalRotation / GameWorld.BOX2D_TIME_STEP;
        return mBody.getInertia() * desiredAngularVelocity;
    }

    private void move() {
        float k = 1 - Interpolation.pow2Out.apply(mBody.getLinearVelocity().len() / MAX_SPEED);
        mTmp.set(k * mForwardForce, 0).rotateRad(mBody.getAngle());
        mBody.applyForce(mTmp, mBody.getWorldCenter(), true);
    }
}
