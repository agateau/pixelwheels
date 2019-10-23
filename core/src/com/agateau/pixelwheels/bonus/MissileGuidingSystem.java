/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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

import com.agateau.pixelwheels.utils.Box2DUtils;
import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class MissileGuidingSystem {
    private static final float MAX_ROTATION = 5f * MathUtils.degRad;
    public static float MAX_SPEED = 160 * AgcMathUtils.kmhToMs;
    private Body mBody;

    public void init(Body body) {
        mBody = body;
    }

    public void act(Vector2 target) {
        move();
        if (target == null) {
            return;
        }

        float angle = computeAngle(target);
        mBody.setTransform(mBody.getWorldCenter(), angle);
    }

    private final Vector2 mTmp = new Vector2();

    private float computeAngle(Vector2 target) {
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
        float delta = AgcMathUtils.normalizeAnglePiRad(desiredAngle - bodyAngle);
        return bodyAngle + MathUtils.clamp(delta, -MAX_ROTATION, MAX_ROTATION);
    }

    private void move() {
        Vector2 velocity = mBody.getLinearVelocity();
        float speed = velocity.len();

        float delta = MAX_SPEED - speed;
        float impulse = delta * mBody.getMass();
        mTmp.set(impulse, 0).rotateRad(mBody.getAngle());
        mBody.applyLinearImpulse(mTmp, mBody.getWorldCenter(), true);

        Vector2 latImpulse = Box2DUtils.getLateralVelocity(mBody).scl(-mBody.getMass());
        mBody.applyLinearImpulse(latImpulse, mBody.getWorldCenter(), true);
    }
}
