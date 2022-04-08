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
package com.agateau.pixelwheels.screens;

import com.agateau.pixelwheels.Assets;
import com.agateau.pixelwheels.rewards.RewardManager;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

/** A menu item to select a vehicle */
public class VehicleSelector extends GridMenuItem<VehicleDef> {
    private Assets mAssets;
    private RewardManager mRewardManager;

    private class Renderer implements GridMenuItem.ItemRenderer<VehicleDef> {
        private final VehicleDrawer mVehicleDrawer;

        private Renderer() {
            mVehicleDrawer = new VehicleDrawer(mAssets);
        }

        @Override
        public Rectangle getItemRectangle(float width, float height, VehicleDef vehicleDef) {
            mVehicleDrawer.vehicleDef = vehicleDef;
            mVehicleDrawer.center.set(width / 2, height / 2);
            mVehicleDrawer.angle = 90;
            return mVehicleDrawer.getRectangle();
        }

        @Override
        public boolean isItemEnabled(VehicleDef vehicleDef) {
            return mRewardManager.isVehicleUnlocked(vehicleDef);
        }

        @Override
        public void render(
                Batch batch, float x, float y, float width, float height, VehicleDef vehicleDef) {
            float old = batch.getPackedColor();
            if (!isItemEnabled(vehicleDef)) {
                batch.setColor(0, 0, 0, 1);
            }
            mVehicleDrawer.vehicleDef = vehicleDef;
            mVehicleDrawer.center.x = x + width / 2;
            mVehicleDrawer.center.y = y + height / 2;
            mVehicleDrawer.angle = 90;
            mVehicleDrawer.draw(batch);
            batch.setPackedColor(old);
        }
    }

    public VehicleSelector(Menu menu) {
        super(menu);
    }

    public void init(Assets assets, RewardManager rewardManager) {
        mAssets = assets;
        mRewardManager = rewardManager;
        setItemSize(90, 90);
        Renderer renderer = new Renderer();
        setItemRenderer(renderer);
        setItems(mAssets.vehicleDefs);
    }
}
