package com.greenyetilab.race;

import java.util.HashMap;

/**
 * Helper class to show debug info on hud
 */
public class DebugStringMap {
    private static HashMap<String, String> sMap = new HashMap<String, String>();

    public static HashMap<String, String> getMap() {
        return sMap;
    }

    public static void put(String key, Object message) {
        sMap.put(key, message.toString());
    }
}
