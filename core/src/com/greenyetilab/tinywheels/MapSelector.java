package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
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
            float oldAlpha = batch.getColor().a;
            if (selected) {
                Color color = batch.getColor();
                color.a /= 2;
                batch.setColor(color);
            }
            batch.draw(region, x + (width - region.getRegionWidth()) / 2, y + (height - region.getRegionHeight()) / 2);
            if (selected) {
                Color color = batch.getColor();
                color.a = oldAlpha;
                batch.setColor(color);
            }
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
