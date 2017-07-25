package com.agateau.tinywheels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.XmlReader;
import com.agateau.utils.GridSelector;
import com.agateau.ui.UiBuilder;

/**
 * An actor to select a vehicle
 */
public class VehicleSelector extends GridSelector<VehicleDef> {
    private Assets mAssets;

    private class Renderer implements GridSelector.ItemRenderer<VehicleDef> {
        @Override
        public void renderSelectionIndicator(Batch batch, float x, float y, float width, float height, VehicleDef vehicleDef) {
            TextureRegion region = mAssets.findRegion("vehicles/" + vehicleDef.mainImage);
            mAssets.renderGridSelectionIndicator(batch, x, y, width, height, region);
        }

        @Override
        public void render(Batch batch, float x, float y, float width, float height, VehicleDef vehicleDef) {
            TextureRegion region = mAssets.findRegion("vehicles/" + vehicleDef.mainImage);
            mAssets.renderGridItem(batch, x, y, width, height, region);
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
