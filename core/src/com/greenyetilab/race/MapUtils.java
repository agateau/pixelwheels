package com.greenyetilab.race;

import com.badlogic.gdx.maps.MapProperties;

/**
 * Created by aurelien on 11/12/14.
 */
public class MapUtils {
    public static float getFloatProperty(MapProperties properties, String key, float defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Float.valueOf(value.toString());
    }
}
