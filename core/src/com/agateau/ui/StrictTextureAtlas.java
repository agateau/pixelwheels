/*
 * Copyright 2018 Aurélien Gâteau <mail@agateau.com>
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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;

/**
 * An atlas with these extra features:
 *
 * <ul>
 *   <li>fails immediately when it does not find a region
 *   <li>caches the found regions
 * </ul>
 */
public class StrictTextureAtlas extends TextureAtlas {
    private final HashMap<String, TextureAtlas.AtlasRegion> mRegions = new HashMap<>();

    public StrictTextureAtlas(FileHandle handle) {
        super(handle);
    }

    @Override
    public TextureAtlas.AtlasRegion findRegion(String name) {
        TextureAtlas.AtlasRegion region = mRegions.get(name);
        if (region != null) {
            return region;
        }
        region = super.findRegion(name);
        if (region == null) {
            throw new RuntimeException(
                    "Failed to load a texture region named '" + name + "' from atlas " + this);
        }
        mRegions.put(name, region);
        return region;
    }

    @Override
    public Array<AtlasRegion> findRegions(String name) {
        Array<TextureAtlas.AtlasRegion> lst = super.findRegions(name);
        if (lst.size == 0) {
            throw new RuntimeException(
                    "Failed to load an array of regions named '" + name + "' from atlas " + this);
        }
        return lst;
    }
}
