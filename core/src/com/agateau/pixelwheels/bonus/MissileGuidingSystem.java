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

import com.agateau.utils.AgcMathUtils;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class MissileGuidingSystem {
    private static final float MAX_ROTATION = 30f * MathUtils.degRad;
    private Body mBody;

    public static float MAX_SPEED = 150 / 3.6f;

    private static final float FORCE = 3;

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

    private Vector2 mTmp = new Vector2();
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
        float delta = desiredAngle - bodyAngle;
        return bodyAngle + MathUtils.clamp(delta, -MAX_ROTATION, MAX_ROTATION);
    }

    private void move() {
        float k = 1 - Interpolation.pow2Out.apply(mBody.getLinearVelocity().len() / MAX_SPEED);
        mTmp.set(k * FORCE, 0).rotateRad(mBody.getAngle());
        mBody.applyForce(mTmp, mBody.getWorldCenter(), true);
    }
}
