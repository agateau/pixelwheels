package com.agateau.tinywheels;

import com.agateau.ui.GridMenuItem;
import com.agateau.ui.Menu;
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
            return mAssets.uiAtlas.findRegion("map-screenshots/" + mapInfo.getId());
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
