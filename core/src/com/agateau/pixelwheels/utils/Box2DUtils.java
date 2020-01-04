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

import com.agateau.pixelwheels.Constants;
import com.agateau.pixelwheels.map.MapUtils;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import java.util.Arrays;

/** A set of utility functions for Box2D */
public class Box2DUtils {
    public static final float MS_TO_KMH = 3.6f;
    private static final Vector2 FORWARD_VECTOR = new Vector2(1, 0);
    private static final Vector2 LATERAL_VECTOR = new Vector2(0, 1);
    private static final Vector2 sTmp = new Vector2();

    @SuppressWarnings("unused")
    public static Vector2 getForwardVelocity(Body body) {
        Vector2 currentRightNormal = body.getWorldVector(FORWARD_VECTOR);
        float v = currentRightNormal.dot(body.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    public static Vector2 getLateralVelocity(Body body) {
        Vector2 currentRightNormal = body.getWorldVector(LATERAL_VECTOR);
        float v = currentRightNormal.dot(body.getLinearVelocity());
        return currentRightNormal.scl(v);
    }

    public static void applyDrag(Body body, float factor) {
        Vector2 dragForce = body.getLinearVelocity().scl(-factor);
        body.applyForce(dragForce, body.getWorldCenter(), true);
    }

    public static void applyCircularDrag(Body body, float factor) {
        float velocity = -body.getAngularVelocity() * factor;
        body.applyTorque(velocity, true);
    }

    @SuppressWarnings("unused")
    public static Body createStaticBox(World world, float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width / 2, y + height / 2);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        body.createFixture(shape, 1);
        return body;
    }

    public static void setCollisionInfo(Body body, int categoryBits, int maskBits) {
        for (Fixture fixture : body.getFixtureList()) {
            Filter filter = fixture.getFilterData();
            filter.categoryBits = (short) categoryBits;
            filter.maskBits = (short) maskBits;
            fixture.setFilterData(filter);
        }
    }

    public static Body createStaticBodyForMapObject(World world, MapObject object) {
        final float u = Constants.UNIT_FOR_PIXEL;
        float rotation = MapUtils.getObjectRotation(object);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.angle = rotation * MathUtils.degreesToRadians;

        if (object instanceof RectangleMapObject) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            /*
             A          D
              x--------x
              |        |
              x--------x
             B          C
            */
            float[] vertices = new float[8];
            // A
            vertices[0] = 0;
            vertices[1] = 0;
            // B
            vertices[2] = 0;
            vertices[3] = -rect.getHeight();
            // C
            vertices[4] = rect.getWidth();
            vertices[5] = -rect.getHeight();
            // D
            vertices[6] = rect.getWidth();
            vertices[7] = 0;
            scaleVertices(vertices, u);

            bodyDef.position.set(u * rect.getX(), u * (rect.getY() + rect.getHeight()));
            Body body = world.createBody(bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.set(vertices);

            body.createFixture(shape, 1);
            return body;
        } else if (object instanceof PolygonMapObject) {
            Polygon polygon = ((PolygonMapObject) object).getPolygon();
            float[] vertices = polygon.getVertices().clone();
            scaleVertices(vertices, u);

            bodyDef.position.set(polygon.getX() * u, polygon.getY() * u);
            Body body = world.createBody(bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.set(vertices);

            body.createFixture(shape, 1);
            return body;
        } else if (object instanceof EllipseMapObject) {
            Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
            float radius = ellipse.width * u / 2;
            float x = ellipse.x * u + radius;
            float y = ellipse.y * u + radius;

            bodyDef.position.set(x, y);
            Body body = world.createBody(bodyDef);

            CircleShape shape = new CircleShape();
            shape.setRadius(radius);

            body.createFixture(shape, 1);
            return body;
        }
        throw new RuntimeException("Unsupported MapObject type: " + object);
    }

    public static void setBodyRestitution(Body body, float restitution) {
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setRestitution(restitution);
        }
    }

    /** Returns vertices for a rectangle of size width x height with truncated corners */
    public static float[] createOctogon(
            float width, float height, float cornerWidth, float cornerHeight) {
        return new float[] {
            width / 2 - cornerWidth,
            -height / 2,
            width / 2,
            -height / 2 + cornerHeight,
            width / 2,
            height / 2 - cornerHeight,
            width / 2 - cornerWidth,
            height / 2,
            -width / 2 + cornerWidth,
            height / 2,
            -width / 2,
            height / 2 - cornerHeight,
            -width / 2,
            -height / 2 + cornerHeight,
            -width / 2 + cornerWidth,
            -height / 2
        };
    }

    public static Shape createBox2DShape(Shape2D shape2D, float zoomFactor) {
        if (shape2D instanceof Polygon) {
            float[] polygonVertices = ((Polygon) shape2D).getTransformedVertices();
            PolygonShape shape = new PolygonShape();
            float[] vertices = Arrays.copyOf(polygonVertices, polygonVertices.length);
            scaleVertices(vertices, zoomFactor);
            shape.set(vertices);
            return shape;
        } else if (shape2D instanceof Circle) {
            Circle circleShape2D = (Circle) shape2D;

            CircleShape shape = new CircleShape();
            sTmp.set(circleShape2D.x, circleShape2D.y).scl(zoomFactor);
            shape.setPosition(sTmp);

            shape.setRadius(circleShape2D.radius * zoomFactor);

            return shape;
        } else if (shape2D instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape2D;

            float[] vertices = new float[8];
            float x1 = rectangle.x;
            float y1 = rectangle.y;
            float x2 = x1 + rectangle.width;
            float y2 = y1 + rectangle.height;
            setVertice(vertices, 0, x1, y1);
            setVertice(vertices, 2, x2, y1);
            setVertice(vertices, 4, x2, y2);
            setVertice(vertices, 6, x1, y2);

            scaleVertices(vertices, zoomFactor);
            PolygonShape shape = new PolygonShape();
            shape.set(vertices);
            return shape;
        } else {
            throw new RuntimeException("Unsupported Shape2D type " + shape2D);
        }
    }

    private static void scaleVertices(float[] vertices, float factor) {
        for (int idx = 0; idx < vertices.length; ++idx) {
            vertices[idx] *= factor;
        }
    }

    private static void setVertice(float[] vertices, int idx, float x, float y) {
        vertices[idx] = x;
        vertices[idx + 1] = y;
    }
}
