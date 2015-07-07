package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.XmlReader;
import com.greenyetilab.utils.GridSelector;
import com.greenyetilab.utils.UiBuilder;

/**
 * An actor to select a map
 */
public class MapSelector extends GridSelector<MapInfo> {
    private Assets mAssets;

    private class Renderer implements GridSelector.ItemRenderer<MapInfo> {
        @Override
        public void render(Batch batch, float x, float y, float width, float height, MapInfo mapInfo, boolean selected) {
            TextureRegion region = mAssets.uiAtlas.findRegion("map-screenshots/" + mapInfo.getId());
            mAssets.renderGridSelectorItem(batch, x, y, width, height, region, selected);
        }
    }

    public void init(Assets assets) {
        mAssets = assets;
        setItemSize(160, 160);
        setItemRenderer(new Renderer());
        setItems(mAssets.mapInfos);
    }

    public static void register(UiBuilder builder) {
        builder.registerActorFactory("MapSelector", new UiBuilder.ActorFactory() {
            @Override
            public Actor createActor(XmlReader.Element element) {
                return new MapSelector();
            }
        });
    }
}
