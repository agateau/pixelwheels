package com.greenyetilab.race;

import com.badlogic.gdx.maps.MapProperties;
import com.greenyetilab.utils.log.NLog;

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

    public static boolean getBooleanProperty(MapProperties properties, String key, boolean defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        String v = value.toString();
        if (v.equals("true")) {
            return true;
        } else if (v.equals("false")) {
            return  false;
        }
        NLog.e("invalid boolean value: %s", v);
        return defaultValue;
    }
}
