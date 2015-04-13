package com.greenyetilab.race;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashMap;

/**
 * An helper class to register global debug shape drawers
 */
public class DebugShapeMap {
    interface Shape {
        void draw(ShapeRenderer renderer);
    }
    private static HashMap<Object, Shape> sMap = new HashMap<Object, Shape>();

    public static HashMap<Object, Shape> getMap() {
        return sMap;
    }

    public static void put(Object key, Shape shape) {
        sMap.put(key, shape);
    }

    public static void remove(Object key) {
        sMap.remove(key);
    }
}
