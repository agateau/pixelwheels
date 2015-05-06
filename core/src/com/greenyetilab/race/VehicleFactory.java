package com.greenyetilab.race;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.XmlReader;
import com.greenyetilab.utils.FileUtils;

/**
 * Generate Vehicles
 */
public class VehicleFactory {
    private final Assets mAssets;
    private final GameWorld mGameWorld;

    private static class EnemyInfo {
        private final String name;
        private final float speed;

        public EnemyInfo(String name, float speed) {
            this.name = name;
            this.speed = speed;
        }
    }

    private EnemyInfo[] mEnemyInfos = new EnemyInfo[] {
            new EnemyInfo("Ice Man", 1),
            new EnemyInfo("Purple", 1),
            new EnemyInfo("Martin", 0.6f),
            new EnemyInfo("Red Bob", 0.8f),
            new EnemyInfo("Yellow Star", 0.9f),
            new EnemyInfo("Enzo", 1f),
    };

    public VehicleFactory(Assets assets, GameWorld gameWorld) {
        mAssets = assets;
        mGameWorld = gameWorld;
    }

    public Vehicle create(String name, float originX, float originY, float angle) {
        String fileName = "vehicles/" + name + ".xml";
        FileHandle handle = FileUtils.assets(fileName);
        if (!handle.exists()) {
            throw new RuntimeException("No such file " + fileName);
        }
        XmlReader.Element root = FileUtils.parseXml(handle);
        return create(root, originX, originY, angle);
    }

    public Vehicle create(XmlReader.Element root, float originX, float originY, float angle) {
        final float U = Constants.UNIT_FOR_PIXEL;
        float maxDrivingForce = GamePlay.instance.maxDrivingForce * root.getFloatAttribute("speed");

        XmlReader.Element mainElement = root.getChildByName("main");
        TextureRegion mainRegion = mAssets.findRegion("vehicles/" + mainElement.getAttribute("image"));
        TextureRegion wheelRegion = mAssets.wheel;

        Vehicle vehicle = new Vehicle(mainRegion, mGameWorld, originX, originY);
        vehicle.setName(root.getAttribute("name"));

        for (XmlReader.Element element : root.getChildrenByName("axle")) {
            float width = element.getFloatAttribute("width") * U;
            float y = (element.getFloatAttribute("y") - mainRegion.getRegionHeight() / 2) * U;
            float steer = element.getFloatAttribute("steer", 0);
            float drive = maxDrivingForce * element.getFloatAttribute("drive", 1);

            createWheel(vehicle, wheelRegion, width / 2, y, steer, drive);
            createWheel(vehicle, wheelRegion, -width / 2, y, steer, drive);
        }

        // Set angle *after* adding the wheels!
        vehicle.setInitialAngle(angle);

        return vehicle;
    }

    private void createWheel(Vehicle vehicle, TextureRegion region, float x, float y, float steer, float drive) {
        Vehicle.WheelInfo info = vehicle.addWheel(region, x, y);
        info.steeringFactor = steer;
        info.wheel.setCanDrift(true);
        info.wheel.setMaxDrivingForce(drive);
    }
}
