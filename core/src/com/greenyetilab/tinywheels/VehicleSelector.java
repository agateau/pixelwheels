package com.greenyetilab.tinywheels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.XmlReader;
import com.greenyetilab.utils.GridSelector;
import com.greenyetilab.utils.UiBuilder;

/**
 * An actor to select a vehicle
 */
public class VehicleSelector extends GridSelector<VehicleDef> {
    private Assets mAssets;

    private class Renderer implements GridSelector.ItemRenderer<VehicleDef> {
        @Override
        public void render(Batch batch, float x, float y, float width, float height, VehicleDef vehicleDef, boolean selected) {
            TextureRegion region = mAssets.findRegion("vehicles/" + vehicleDef.mainImage);
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
        setItemSize(80, 80);
        setItemRenderer(new Renderer());
        setItems(mAssets.vehicleDefs);
    }

    public String getSelectedId() {
        return getSelected().id;
    }

    public static void register(UiBuilder builder) {
        builder.registerActorFactory("VehicleSelector", new UiBuilder.ActorFactory() {
            @Override
            public Actor createActor(XmlReader.Element element) {
                return new VehicleSelector();
            }
        });
    }
}
