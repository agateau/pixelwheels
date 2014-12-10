package com.greenyetilab.race;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.greenyetilab.utils.log.NLog;

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
        }
    }

    private static float getFloatProperty(MapProperties properties, String key, float defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Float.valueOf(value.toString());
    }

    private void createForest(MapObject object) {
        RectangleMapObject rectObject = (RectangleMapObject)object;
        assert rectObject != null;
        float density = getFloatProperty(rectObject.getProperties(), "density", 0.075f);
        Rectangle rect = rectObject.getRectangle();
        float originX = Constants.UNIT_FOR_PIXEL * rect.getX();
        float originY = Constants.UNIT_FOR_PIXEL * rect.getY();
        float width = Constants.UNIT_FOR_PIXEL * rect.getWidth();
        float height = Constants.UNIT_FOR_PIXEL * rect.getHeight();

        float delta = (float) (1 / Math.sqrt(density));
        float variation = 0.3f;
        for (float y = originY; y < originY + height; y += delta) {
            for (float x = originX; x < originX + width; x += delta) {
                float treeX = x + MathUtils.random(-variation, variation);
                float treeY = y + MathUtils.random(-variation, variation);
                mWorld.addGameObject(new TreeObject(mWorld, mAssets, treeX, treeY));
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
}
