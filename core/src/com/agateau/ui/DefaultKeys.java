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

import com.agateau.utils.Assert;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;

/** Default mapping between Gdx.Input keys and VirtualKey */
public class DefaultKeys {
    private static Array<HashMap<VirtualKey, Integer>> sDefaultKeysForPlayer;

    static int getDefaultKey(int playerIdx, VirtualKey vkey) {
        initDefaultKeys();
        Assert.check(
                playerIdx < sDefaultKeysForPlayer.size,
                "No default keys for playerId " + playerIdx);
        Integer key = sDefaultKeysForPlayer.get(playerIdx).get(vkey);
        return key == null ? Input.Keys.UNKNOWN : key;
    }

    public static int getDefaultKeysCount() {
        initDefaultKeys();
        return sDefaultKeysForPlayer.size;
    }

    private static void initDefaultKeys() {
        if (sDefaultKeysForPlayer != null) {
            return;
        }
        sDefaultKeysForPlayer = new Array<>();
        HashMap<VirtualKey, Integer> keyMap;

        // Player 1
        keyMap = new HashMap<>();
        sDefaultKeysForPlayer.add(keyMap);
        keyMap.put(VirtualKey.LEFT, Input.Keys.LEFT);
        keyMap.put(VirtualKey.RIGHT, Input.Keys.RIGHT);
        keyMap.put(VirtualKey.UP, Input.Keys.UP);
        keyMap.put(VirtualKey.DOWN, Input.Keys.DOWN);
        keyMap.put(VirtualKey.TRIGGER, Input.Keys.SPACE);
        keyMap.put(VirtualKey.BACK, Input.Keys.ESCAPE);

        // Player 2
        keyMap = new HashMap<>();
        sDefaultKeysForPlayer.add(keyMap);
        keyMap.put(VirtualKey.LEFT, Input.Keys.X);
        keyMap.put(VirtualKey.RIGHT, Input.Keys.V);
        keyMap.put(VirtualKey.UP, Input.Keys.D);
        keyMap.put(VirtualKey.DOWN, Input.Keys.C);
        keyMap.put(VirtualKey.TRIGGER, Input.Keys.CONTROL_LEFT);
        keyMap.put(VirtualKey.BACK, Input.Keys.Q);

        // Player 3
        sDefaultKeysForPlayer.add(new HashMap<>());

        // Player 4
        sDefaultKeysForPlayer.add(new HashMap<>());
    }
}
