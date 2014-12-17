package com.greenyetilab.race;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;

/**
 * Create obstacles from tiled map objects
 */
public class ObstacleCreator {
    private static class NineTile {
        int originId;
        int width;
        int height;
        int leftWidth = 1;
        int rightWidth = 1;
        int bottomHeight = 1;
        int topHeight = 1;
    }

    private static final int TILESET_WIDTH = 8;

    private static final float TREE_PROBABILITY = 0.75f;
    private static final NineTile TREE_NINE_TILE = new NineTile() {{
        originId = 57;
        width = 2;
        height = 2;
    }};
    private static final float TREE_TRUNK_RADIUS = 0.8f;

    private static final float HOUSE_PROBABILITY = 0.9f;
    private static final NineTile HOUSE_NINE_TILE = new NineTile() {{
        originId = 76;
        width = 3;
        height = 4;
        leftWidth = 1;
        rightWidth = 2;
        bottomHeight = 2;
        topHeight = 1;
    }};
    private static final float HOUSE_BODY_OFFSET = Constants.UNIT_FOR_PIXEL * 8;

    private final Assets mAssets;
    private final GameWorld mWorld;
    private final TiledMapTileSets mTileSets;
    private final TiledMapTileLayer mDestLayer;
    private final float mTileWidth;
    private final float mTileHeight;

    public ObstacleCreator(GameWorld world, Assets assets, TiledMapTileSets tileSets, TiledMapTileLayer destinationLayer) {
        mWorld = world;
        mAssets = assets;
        mTileSets = tileSets;
        mDestLayer = destinationLayer;
        mTileWidth = mDestLayer.getTileWidth();
        mTileHeight = mDestLayer.getTileHeight();
    }

    public void create(MapObject object) {
        String name = object.getName();
        if (name.equals("forest")) {
            createForest(object);
        } else if (name.equals("house")) {
            createHouse(object);
        } else if (name.equals("car")) {
            createCar(object);
        }
    }

    private void createForest(MapObject object) {
        RectangleMapObject rectObject = (RectangleMapObject)object;
        assert rectObject != null;
        Rectangle rect = rectObject.getRectangle();
        int startX = (int)(rect.getX() / mTileWidth);
        int startY = (int)(rect.getY() / mTileHeight);
        int width = (int)(rect.getWidth() / mTileWidth);
        int height = (int)(rect.getHeight() / mTileHeight);

        for (int ty = 0; ty < height - 1; ty += 2) {
            for (int tx = 0; tx < width - 1; tx += 2) {
                if (MathUtils.random() >= TREE_PROBABILITY) {
                    continue;
                }
                addNineTile(startX + tx, startY + ty, 2, 2, TREE_NINE_TILE);

                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set(
                        Constants.UNIT_FOR_PIXEL * (startX + tx + 1) * mTileWidth,
                        Constants.UNIT_FOR_PIXEL * (startY + ty + 1) * mTileHeight);
                Body body = mWorld.getBox2DWorld().createBody(bodyDef);

                CircleShape shape = new CircleShape();
                shape.setRadius(TREE_TRUNK_RADIUS);
                body.createFixture(shape, 1f);

                body.setAwake(false);
            }
        }
    }

    private void createHouse(MapObject object) {
        if (MathUtils.random() >= HOUSE_PROBABILITY) {
            return;
        }

        RectangleMapObject rectObject = (RectangleMapObject)object;
        assert rectObject != null;
        Rectangle rect = rectObject.getRectangle();
        int startX = (int)(rect.getX() / mTileWidth);
        int startY = (int)(rect.getY() / mTileHeight) - 1; // - 1 for the bottom-right shadow
        int width = 3;
        int height = (int)(rect.getHeight() / mTileHeight) + 1; // + 1 for the bottom-right shadow

        addNineTile(startX, startY, width, height, HOUSE_NINE_TILE);

        final float unitForTx = Constants.UNIT_FOR_PIXEL * mTileWidth;
        final float unitForTy = Constants.UNIT_FOR_PIXEL * mTileHeight;
        Body body = Box2DUtils.createStaticBox(mWorld.getBox2DWorld(),
                unitForTx * startX + HOUSE_BODY_OFFSET,
                unitForTx * (startY + 1) + HOUSE_BODY_OFFSET,
                unitForTy * (width - 1) - 2 * HOUSE_BODY_OFFSET,
                unitForTy * (height - 1) - 2 * HOUSE_BODY_OFFSET);
        body.setAwake(false);
    }

    private void addNineTile(int startX, int startY, int width, int height, NineTile nineTile) {
        width = Math.max(width, nineTile.leftWidth + nineTile.rightWidth);
        height = Math.max(height, nineTile.bottomHeight + nineTile.topHeight);
        int tileMiddleWidth = nineTile.width - nineTile.leftWidth - nineTile.rightWidth;
        int tileMiddleHeight = nineTile.height - nineTile.topHeight - nineTile.bottomHeight;
        for (int ty = 0; ty < height; ++ty) {
            for (int tx = 0; tx < width; ++tx) {
                int tileX;
                int tileY;
                if (tx < nineTile.leftWidth) {
                    tileX = tx;
                } else if (tx < width - nineTile.rightWidth) {
                    tileX = nineTile.leftWidth + (tx - nineTile.rightWidth) % tileMiddleWidth;
                } else {
                    tileX = nineTile.width - (width - tx);
                }
                if (ty < nineTile.bottomHeight) {
                    tileY = ty;
                } else if (ty < height - nineTile.topHeight) {
                    tileY = nineTile.bottomHeight + (ty - nineTile.bottomHeight) % tileMiddleHeight;
                } else {
                    tileY = nineTile.height - (height - ty);
                }
                int tileId = nineTile.originId - tileY * TILESET_WIDTH + tileX;
                createCell(startX + tx, startY + ty, tileId);
            }
        }
    }

    // This is only used for special places like cross sections where we want to generate several cars
    // coming from the left and right sides
    private void createCar(MapObject object) {
        RectangleMapObject rectObject = (RectangleMapObject)object;
        assert rectObject != null;

        Rectangle rect = rectObject.getRectangle();
        float originX = Constants.UNIT_FOR_PIXEL * (rect.getX() + MathUtils.random(rect.getWidth()));
        float originY = Constants.UNIT_FOR_PIXEL * (rect.getY() + MathUtils.random(rect.getHeight()));
        float angle = MapUtils.getFloatProperty(rectObject.getProperties(), "angle", 270f);

        EnemyCar car = new EnemyCar(mWorld, mAssets, originX, originY);
        car.setDrivingAngle(angle);
        car.setPilot(new BasicPilot(mWorld.getMapInfo(), car));
        mWorld.addGameObject(car);
    }

    private void createCell(int tx, int ty, int tileId) {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(mTileSets.getTile(tileId));
        mDestLayer.setCell(tx, ty, cell);
    }
}
