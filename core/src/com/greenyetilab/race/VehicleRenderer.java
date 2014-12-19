package com.greenyetilab.race;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;

/**
 * Renders a vehicle
 */
public class VehicleRenderer {
    private final Vehicle mVehicle;
    private final HealthComponent mHealthComponent;

    public VehicleRenderer(Vehicle vehicle, HealthComponent healthComponent) {
        mVehicle = vehicle;
        mHealthComponent = healthComponent;
    }

    public void draw(Batch batch, int zIndex) {
        if (zIndex != Constants.Z_VEHICLES) {
            return;
        }
        for(Vehicle.WheelInfo info: mVehicle.getWheelInfos()) {
            info.wheel.draw(batch);
        }
        Color oldColor = batch.getColor();

        HealthComponent.State state = mHealthComponent.getState();
        if (state != HealthComponent.State.ALIVE) {
            float k = state == HealthComponent.State.DEAD ? 1 : (mHealthComponent.getKilledTime() / Vehicle.DYING_DURATION);
            float rgb = MathUtils.lerp(1, 0.3f, k);
            batch.setColor(rgb, rgb, rgb, 1);
        }
        DrawUtils.drawBodyRegion(batch, mVehicle.getBody(), mVehicle.getRegion());
        if (state != HealthComponent.State.ALIVE) {
            batch.setColor(oldColor);
        }
    }
}
