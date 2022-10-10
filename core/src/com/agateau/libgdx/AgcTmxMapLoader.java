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

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.XmlReader;

/** This class is here to fix issues with loading recent Tiled maps with TmxMapLoader */
public class AgcTmxMapLoader extends TmxMapLoader {

    /*
     * Fix a crash when loading properties of type file and empty.
     */
    @Override
    protected Object castProperty(String name, String value, String type) {
        if (type == null || type.equals("file") || type.equals("string")) {
            return value;
        }
        return super.castProperty(name, value, type);
    }

    /*
     * Tiled 1.9.0 replaced the `type` attribute of `<object>` with `class`. Convert it back to
     * `type`.
     */
    @Override
    protected void loadObject(
            TiledMap map, MapObjects objects, XmlReader.Element element, float heightInPixels) {
        super.loadObject(map, objects, element, heightInPixels);
        if (element.getName().equals("object")) {
            String type = element.getAttribute("class", null);
            if (type != null) {
                // The last element in objects is the one which was just inserted by
                // super.loadObject()
                MapObject object = objects.get(objects.getCount() - 1);
                object.getProperties().put("type", type);
            }
        }
    }
}
