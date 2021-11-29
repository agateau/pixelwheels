/*
 * Copyright 2021 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.ui;

import static com.agateau.utils.CollectionUtils.addToIntegerArray;

import com.agateau.utils.Assert;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;

/** Default mapping between Gdx.Input keys and VirtualKey */
public class DefaultKeys {
    private static Array<HashMap<VirtualKey, Integer[]>> sDefaultKeysForPlayer;

    static Integer[] getDefaultKeys(int playerIdx, VirtualKey vkey) {
        initDefaultKeys();
        Assert.check(
                playerIdx < sDefaultKeysForPlayer.size,
                "No default keys for playerId " + playerIdx);
        return sDefaultKeysForPlayer.get(playerIdx).get(vkey);
    }

    private static void initDefaultKeys() {
        if (sDefaultKeysForPlayer != null) {
            return;
        }
        sDefaultKeysForPlayer = new Array<>();
        HashMap<VirtualKey, Integer[]> keyMap;

        // Player 1
        keyMap = new HashMap<>();
        sDefaultKeysForPlayer.add(keyMap);
        addDefaultKey(keyMap, VirtualKey.LEFT, Input.Keys.LEFT);
        addDefaultKey(keyMap, VirtualKey.RIGHT, Input.Keys.RIGHT);
        addDefaultKey(keyMap, VirtualKey.UP, Input.Keys.UP);
        addDefaultKey(keyMap, VirtualKey.DOWN, Input.Keys.DOWN);
        addDefaultKey(keyMap, VirtualKey.TRIGGER, Input.Keys.SPACE);
        addDefaultKey(keyMap, VirtualKey.BACK, Input.Keys.ESCAPE);

        // Player 2
        keyMap = new HashMap<>();
        sDefaultKeysForPlayer.add(keyMap);
        addDefaultKey(keyMap, VirtualKey.LEFT, Input.Keys.X);
        addDefaultKey(keyMap, VirtualKey.RIGHT, Input.Keys.V);
        addDefaultKey(keyMap, VirtualKey.UP, Input.Keys.D);
        addDefaultKey(keyMap, VirtualKey.DOWN, Input.Keys.C);
        addDefaultKey(keyMap, VirtualKey.TRIGGER, Input.Keys.CONTROL_LEFT);
        addDefaultKey(keyMap, VirtualKey.BACK, Input.Keys.Q);
    }

    private static void addDefaultKey(
            HashMap<VirtualKey, Integer[]> keyMap, VirtualKey vkey, int key) {
        Integer[] keys = keyMap.get(vkey);
        if (keys == null) {
            keys = new Integer[] {key};
        } else {
            keys = addToIntegerArray(keys, key);
        }
        keyMap.put(vkey, keys);
    }
}
