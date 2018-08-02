/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

/**
 * An helper class to find the closest body using a raycast
 */
public class ClosestBodyFinder implements RayCastCallback {
    private static final float ANGLE_BETWEEN_RAYS = 3;
    private final World mWorld;
    private final float mDepth;
    private final float mArc;
    private BodyFilter mBodyFilter = null;
    private Body mBody = null;
    private float mFraction;

    // Work vars
    private Vector2 mTmp = new Vector2();

    public interface BodyFilter {
        boolean acceptBody(Body body);
    }

    public ClosestBodyFinder(World world, float depth) {
        this(world, depth, 0);
    }

    public ClosestBodyFinder(World world, float depth, float arc) {
        mWorld = world;
        mDepth = depth;
        mArc = arc;
    }

    public void setBodyFilter(BodyFilter bodyFilter) {
        mBodyFilter = bodyFilter;
    }

    public Body find(Vector2 origin, float angle) {
        mFraction = 1;
        mBody = null;
        for (float a = angle - mArc / 2; a <= angle + mArc / 2; a += ANGLE_BETWEEN_RAYS) {
            mTmp.set(mDepth, 0).rotate(a).add(origin);
            mWorld.rayCast(this, origin, mTmp);
        }
        return mBody;
    }

    public Vector2 getLeftVertex(Vector2 origin, float angle) {
        mTmp.set(mDepth, 0).rotate(angle + mArc / 2).add(origin);
        return mTmp;
    }

    public Vector2 getRightVertex(Vector2 origin, float angle) {
        mTmp.set(mDepth, 0).rotate(angle - mArc / 2).add(origin);
        return mTmp;
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if (mFraction < fraction) {
            // Too far, no need to go further
            return mFraction;
        }
        Body body = fixture.getBody();
        if (mBodyFilter != null && !mBodyFilter.acceptBody(body)) {
            return -1;
        }
        mFraction = fraction;
        mBody = body;
        return fraction;
    }
}
