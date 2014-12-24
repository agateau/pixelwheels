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
        if (zIndex == Constants.Z_GROUND) {
            DrawUtils.drawBodyRegionShadow(batch, mVehicle.getBody(), mVehicle.getRegion());
            return;
        }
        if (zIndex != Constants.Z_VEHICLES) {
            return;
        }
        Color oldColor = batch.getColor();
        HealthComponent.State state = mHealthComponent.getState();
        float rgb;
        float alpha;
        if (state == HealthComponent.State.ALIVE) {
            float k = mHealthComponent.getHealth() / (float)mHealthComponent.getMaxHealth();
            rgb = MathUtils.lerp(0.3f, 1, k);
            alpha = 1;
        } else {
            float k = state == HealthComponent.State.DEAD ? 1 : (mHealthComponent.getKilledTime() / HealthComponent.DYING_DURATION);
            rgb = MathUtils.lerp(0.3f, 0, k);
            alpha = MathUtils.cos(k / (MathUtils.PI / 2));
        }
        batch.setColor(rgb, rgb, rgb, alpha);

        for(Vehicle.WheelInfo info: mVehicle.getWheelInfos()) {
            DrawUtils.drawBodyRegion(batch, info.wheel.getBody(), info.wheel.getRegion());
        }
        DrawUtils.drawBodyRegion(batch, mVehicle.getBody(), mVehicle.getRegion());
        batch.setColor(oldColor);
    }
}
