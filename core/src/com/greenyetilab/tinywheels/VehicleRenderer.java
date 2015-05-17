package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

/**
 * Renders a vehicle
 */
public class VehicleRenderer implements Renderer {
    private final Vehicle mVehicle;
    private final Array<Renderer> mRenderers = new Array<Renderer>();

    public VehicleRenderer(Vehicle vehicle) {
        mVehicle = vehicle;
    }

    public void addRenderer(Renderer renderer) {
        mRenderers.add(renderer);
    }

    public void removeRenderer(Renderer renderer) {
        mRenderers.removeValue(renderer, true);
    }

    public void draw(Batch batch, int zIndex) {
        if (zIndex == Constants.Z_GROUND) {
            DrawUtils.drawBodyRegionShadow(batch, mVehicle.getBody(), mVehicle.getRegion());
            return;
        }
        if (zIndex != Constants.Z_VEHICLES) {
            return;
        }

        for(Vehicle.WheelInfo info: mVehicle.getWheelInfos()) {
            DrawUtils.drawBodyRegion(batch, info.wheel.getBody(), info.wheel.getRegion());
        }
        DrawUtils.drawBodyRegion(batch, mVehicle.getBody(), mVehicle.getRegion());

        for (Renderer renderer : mRenderers) {
            renderer.draw(batch, zIndex);
        }
    }
}
