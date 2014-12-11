package com.greenyetilab.race;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Create obstacles from tiled map objects
 */
public class ObstacleCreator {
    private final Assets mAssets;
    private final GameWorld mWorld;

    public ObstacleCreator(GameWorld world, Assets assets) {
        mWorld = world;
        mAssets = assets;
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
        float density = MapUtils.getFloatProperty(rectObject.getProperties(), "density", 0.075f);
        Rectangle rect = rectObject.getRectangle();
        float originX = Constants.UNIT_FOR_PIXEL * rect.getX();
        float originY = Constants.UNIT_FOR_PIXEL * rect.getY();
        float width = Constants.UNIT_FOR_PIXEL * rect.getWidth();
        float height = Constants.UNIT_FOR_PIXEL * rect.getHeight();

        float delta = (float) (1 / Math.sqrt(density));
        float variation = 0.3f;
        for (float y = originY; y < originY + height; y += delta) {
            boolean horizontalEdge = y == originY || y + delta >= originY + height;
            for (float x = originX; x < originX + width; x += delta) {
                boolean verticalEdge = x == originX || x + delta >= originX + width;
                float treeX = x + MathUtils.random(-variation, variation);
                float treeY = y + MathUtils.random(-variation, variation);
                TreeObject tree = new TreeObject(mWorld, mAssets, treeX, treeY);
                if (horizontalEdge || verticalEdge) {
                    tree.createBody();
                }
                mWorld.addGameObject(tree);
            }
        }
    }

    private void createHouse(MapObject object) {
        RectangleMapObject rectObject = (RectangleMapObject)object;
        assert rectObject != null;

        Rectangle rect = rectObject.getRectangle();
        float originX = Constants.UNIT_FOR_PIXEL * rect.getX();
        float originY = Constants.UNIT_FOR_PIXEL * rect.getY();
        float width = Constants.UNIT_FOR_PIXEL * rect.getWidth();
        float height = Constants.UNIT_FOR_PIXEL * rect.getHeight();

        mWorld.addGameObject(new HouseObject(mWorld, mAssets, originX, originY, width, height));
    }

    private void createCar(MapObject object) {
        RectangleMapObject rectObject = (RectangleMapObject)object;
        assert rectObject != null;

        Rectangle rect = rectObject.getRectangle();
        float originX = Constants.UNIT_FOR_PIXEL * (rect.getX() + MathUtils.random(rect.getWidth()));
        float originY = Constants.UNIT_FOR_PIXEL * (rect.getY() + MathUtils.random(rect.getHeight()));
        float angle = MapUtils.getFloatProperty(rectObject.getProperties(), "angle", 270f);

        EnemyCar car = new EnemyCar(mWorld, mAssets, originX, originY);
        car.setDrivingAngle(angle);
        mWorld.addGameObject(car);
    }
}
