package com.greenyetilab.race;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntArray;

/**
 * Generates enemies as the player advances through the map
 */
public class EnemySpawner {
    private final GameWorld mGameWorld;
    private final Assets mAssets;
    private float mTopY = 0;
    private final float mEnemyYInterval = Constants.VIEWPORT_WIDTH * 2 / 3;
    private final IntArray mRowUsableTXs;

    public EnemySpawner(GameWorld gameWorld, Assets assets) {
        mAssets = assets;
        mGameWorld = gameWorld;
        mRowUsableTXs = new IntArray(mGameWorld.getMapInfo().getMapTWidth());
    }

    public void setTopY(float y) {
        float oldY = mTopY;
        mTopY = y;
        if (MathUtils.floor(mTopY / mEnemyYInterval) > MathUtils.floor(oldY / mEnemyYInterval)) {
            spawnEnemies();
        }
    }

    private void spawnEnemies() {
        MapInfo mapInfo = mGameWorld.getMapInfo();
        TiledMapTileLayer groundLayer = mapInfo.getGroundLayer();

        // Init mRowUsableTiles
        final int ty = MathUtils.floor((mTopY + mEnemyYInterval) / mapInfo.getTileHeight());
        if (ty > groundLayer.getHeight()) {
            return;
        }
        mRowUsableTXs.clear();
        for (int tx = 0; tx < groundLayer.getWidth(); ++tx) {
            TiledMapTileLayer.Cell cell = groundLayer.getCell(tx, ty);
            if (cell == null) {
                continue;
            }
            int tileId = cell.getTile().getId();
            if (mapInfo.isRoadTile(tileId)) {
                mRowUsableTXs.add(tx);
            }
        }

        int idx = MathUtils.random(mRowUsableTXs.size - 1);
        spawnEnemyAt(mRowUsableTXs.get(idx), ty);
    }

    private void spawnEnemyAt(int tx, int ty) {
        MapInfo mapInfo = mGameWorld.getMapInfo();
        float posX = mapInfo.getTileWidth() * tx;
        float posY = mapInfo.getTileHeight() * ty;

        GameObject object;
        int choice = MathUtils.random(0, 2);
        if (choice == 0) {
            float angle = mapInfo.getDirectionAt(posX, posY);
            object = generateEnemyCar(mAssets, mGameWorld, posX, posY, angle);
        } else if (choice == 1) {
            object = Mine.create(mGameWorld, mAssets, posX, posY);
        } else {
            EnemyTruck truck = new EnemyTruck(mAssets, mGameWorld, posX, posY);
            float angle = mapInfo.getDirectionAt(posX, posY);
            truck.setInitialAngle(angle);
            object = truck;
        }
        mGameWorld.addGameObject(object);
    }

    public static GameObject generateEnemyCar(Assets assets, GameWorld gameWorld, float originX, float originY, float angle) {
        TextureRegion region = assets.cars.get(MathUtils.random(assets.cars.size - 1));
        PendingVehicle car = new PendingVehicle(region, gameWorld, originX, originY);
        car.setPilot(new BasicPilot(gameWorld.getMapInfo(), car));

        // Wheels
        TextureRegion wheelRegion = assets.wheel;
        final float REAR_WHEEL_Y = Constants.UNIT_FOR_PIXEL * 16f;
        final float WHEEL_BASE = Constants.UNIT_FOR_PIXEL * 46f;

        float wheelW = Constants.UNIT_FOR_PIXEL * wheelRegion.getRegionWidth();
        float rightX = car.getWidth() / 2 - wheelW / 2 + 0.05f;
        float leftX = -rightX;
        float rearY = -car.getHeight() / 2 + REAR_WHEEL_Y;
        float frontY = rearY + WHEEL_BASE;

        Vehicle.WheelInfo info;
        info = car.addWheel(wheelRegion, leftX, frontY);
        info.steeringFactor = 1;
        info = car.addWheel(wheelRegion, rightX, frontY);
        info.steeringFactor = 1;
        info = car.addWheel(wheelRegion, leftX, rearY);
        info.wheel.setCanDrift(true);
        info = car.addWheel(wheelRegion, rightX, rearY);
        info.wheel.setCanDrift(true);

        car.setInitialAngle(angle);
        return car;
    }
}
