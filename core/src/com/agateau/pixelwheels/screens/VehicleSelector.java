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
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.TextureRegionItemRendererAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A menu item to select a vehicle
 */
public class VehicleSelector extends GridMenuItem<VehicleDef> {
    private Assets mAssets;

    private class Renderer extends TextureRegionItemRendererAdapter<VehicleDef> {
        @Override
        protected TextureRegion getItemRegion(VehicleDef vehicleDef) {
            return mAssets.getVehicleRegion(vehicleDef);
        }
    }

    public VehicleSelector(Menu menu) {
        super(menu);
    }

    public void init(Assets assets) {
        mAssets = assets;
        setItemSize(80, 80);
        Renderer renderer = new Renderer();
        renderer.setAngle(90);
        setItemRenderer(renderer);
        setItems(mAssets.vehicleDefs);
    }

    public String getSelectedId() {
        return getSelected().id;
    }
}
