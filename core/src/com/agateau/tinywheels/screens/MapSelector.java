/*
 * Copyright 2017 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Tiny Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.tinywheels.screens;

import com.agateau.tinywheels.Assets;
import com.agateau.tinywheels.map.MapInfo;
import com.agateau.ui.menu.GridMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.TextureRegionItemRendererAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A menu item to select a map
 */
public class MapSelector extends GridMenuItem<MapInfo> {
    private Assets mAssets;

    private class Renderer extends TextureRegionItemRendererAdapter<MapInfo> {
        @Override
        protected TextureRegion getItemRegion(MapInfo mapInfo) {
            return mAssets.ui.atlas.findRegion("map-screenshots/" + mapInfo.getId());
        }
    }

    public MapSelector(Menu menu) {
        super(menu);
    }

    public void init(Assets assets) {
        mAssets = assets;
        setItemSize(160, 160);
        setItemRenderer(new Renderer());
        setItems(mAssets.mapInfos);
    }
}
