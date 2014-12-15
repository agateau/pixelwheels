package com.greenyetilab.race;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntArray;
import com.greenyetilab.utils.log.NLog;

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
        int choice = MathUtils.random(0, 1);
        if (choice == 0) {
            EnemyCar car = new EnemyCar(mGameWorld, mAssets, posX, posY);
            float angle = mapInfo.getDirectionAt(posX, posY);
            car.setDrivingAngle(angle);
            object = car;
        } else {
            Mine mine = new Mine();
            mine.init(mGameWorld, mAssets, posX, posY);
            object = mine;
        }
        mGameWorld.addGameObject(object);
    }
}
