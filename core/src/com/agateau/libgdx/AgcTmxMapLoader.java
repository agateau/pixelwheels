/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.libgdx;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * This class is here to fix a crash when loading recent Tiled maps with TmxMapLoader: it does not
 * know how to handle properties of type file and empty.
 */
public class AgcTmxMapLoader extends TmxMapLoader {
    @Override
    protected Object castProperty(String name, String value, String type) {
        if (type == null || type.equals("file") || type.equals("string")) {
            return value;
        }
        return super.castProperty(name, value, type);
    }
}
