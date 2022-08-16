/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.utils.DrawUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.uibuilder.UiBuilder;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

class VehicleActor extends Actor {
    private final VehicleDrawer mDrawer;

    private VehicleActor(Assets assets) {
        mDrawer = new VehicleDrawer(assets);
    }

    static void register(UiBuilder builder, Assets assets) {
        builder.registerActorFactory("Vehicle", (uiBuilder, element) -> new VehicleActor(assets));
    }

    public void setVehicleDef(VehicleDef vehicleDef) {
        mDrawer.setVehicleDef(vehicleDef);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        DrawUtils.multiplyBatchAlphaBy(batch, parentAlpha);
        mDrawer.setCenter(getX(), getY());
        mDrawer.setAngle(90 + getRotation());
        mDrawer.draw(batch);
    }
}
