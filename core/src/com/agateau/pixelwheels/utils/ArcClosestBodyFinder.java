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
package com.agateau.pixelwheels.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

/**
 * An helper class to find the closest body using a raycast in an arc
 *
 * <p>World is not passed to the constructor to make it easier for the class to be used in a
 * poolable object
 */
public class ArcClosestBodyFinder implements RayCastCallback {
    public enum FilterResult {
        IGNORE,
        STOP_FAILED,
        STOP_SUCCESS
    }

    public interface BodyFilter {
        FilterResult filter(Body body);
    }

    private static final float ANGLE_BETWEEN_RAYS = 3;
    private final float mDepth;
    private final float mArc;
    private BodyFilter mBodyFilter = sDefaultBodyFilter;
    private Body mBody;
    private float mFraction;

    /** Default filter which reports success on first hit */
    private static BodyFilter sDefaultBodyFilter = body -> FilterResult.STOP_SUCCESS;

    // Work vars
    private final Vector2 mTmp = new Vector2();

    public ArcClosestBodyFinder(float depth) {
        this(depth, 0);
    }

    public ArcClosestBodyFinder(float depth, float arc) {
        mDepth = depth;
        mArc = arc;
    }

    public void setBodyFilter(BodyFilter bodyFilter) {
        mBodyFilter = bodyFilter;
    }

    public Body find(World world, Vector2 origin, float angle) {
        mFraction = 1;
        mBody = null;
        for (float a = angle - mArc / 2; a <= angle + mArc / 2; a += ANGLE_BETWEEN_RAYS) {
            mTmp.set(mDepth, 0).rotate(a).add(origin);
            world.rayCast(this, origin, mTmp);
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
            // Too far, no need to look further
            return mFraction;
        }
        Body body = fixture.getBody();
        switch (mBodyFilter.filter(body)) {
            case IGNORE:
                return -1;
            case STOP_FAILED:
                return 0;
            case STOP_SUCCESS:
                mFraction = fraction;
                mBody = body;
                break;
        }
        return fraction;
    }
}
