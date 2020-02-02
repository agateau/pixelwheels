/*
 * Copyright 2020 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels;

import com.agateau.pixelwheels.bonus.Mine;
import com.agateau.pixelwheels.racer.Racer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

/** Helper class to identify what entity a body is */
public class BodyIdentifier {
    /** A mine or a wall */
    public static boolean isStaticObstacle(Body body) {
        return body.getType() == BodyDef.BodyType.StaticBody;
    }

    public static boolean isWall(Body body) {
        return body.getType() == BodyDef.BodyType.StaticBody && !isMine(body);
    }

    public static boolean isVehicle(Body body) {
        return body.getUserData() instanceof Racer;
    }

    public static boolean isMine(Body body) {
        return body.getUserData() instanceof Mine;
    }
}
