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
import com.agateau.pixelwheels.utils.DrawUtils;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.ui.TextureRegionItemRendererAdapter;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** A menu item to select a vehicle */
public class VehicleSelector extends GridMenuItem<VehicleDef> {
    private Assets mAssets;
    private RewardManager mRewardManager;

    private class Renderer extends TextureRegionItemRendererAdapter<VehicleDef> {
        private final VehicleDrawer mVehicleDrawer;

        private Renderer() {
            mVehicleDrawer = new VehicleDrawer(mAssets);
        }

        @Override
        protected TextureRegion getItemRegion(VehicleDef vehicleDef) {
            return isItemEnabled(vehicleDef) ? vehicleDef.getImage(mAssets) : mAssets.lockedVehicle;
        }

        @Override
        public boolean isItemEnabled(VehicleDef vehicleDef) {
            return mRewardManager.isVehicleUnlocked(vehicleDef);
        }

        @Override
        public void render(
                Batch batch, float x, float y, float width, float height, VehicleDef vehicleDef) {
            TextureRegion region = getItemRegion(vehicleDef);
            updateRenderInfo(width, height, region);

            if (isItemEnabled(vehicleDef)) {
                mVehicleDrawer.vehicleDef = vehicleDef;
                mVehicleDrawer.center.x = x + width / 2;
                mVehicleDrawer.center.y = y + height / 2;
                mVehicleDrawer.scale = getScale();
                mVehicleDrawer.angle = getAngle();
                mVehicleDrawer.draw(batch);
            } else {
                DrawUtils.drawCentered(
                        batch, region, x + width / 2, y + height / 2, getScale(), getAngle());
            }
        }
    }

    public VehicleSelector(Menu menu) {
        super(menu);
    }

    public void init(Assets assets, RewardManager rewardManager) {
        mAssets = assets;
        mRewardManager = rewardManager;
        setItemSize(80, 80);
        Renderer renderer = new Renderer();
        renderer.setAngle(90);
        setItemRenderer(renderer);
        setItems(mAssets.vehicleDefs);
    }
}
