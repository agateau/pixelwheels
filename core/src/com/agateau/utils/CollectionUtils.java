/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CollectionUtils {

    public static <T> Set<T> newSet(T... items) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, items);
        return set;
    }

    /** Implementation of Map.getOrDefault() which works on Android API < 24 */
    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.containsKey(key) ? null : defaultValue;
    }
}
