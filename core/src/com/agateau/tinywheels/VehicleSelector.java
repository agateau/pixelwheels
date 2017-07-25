package com.agateau.tinywheels;

import com.agateau.ui.GridMenuItem;
import com.agateau.ui.Menu;
import com.agateau.ui.TextureRegionItemRenderer;
import com.agateau.ui.TextureRegionItemRendererAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * An actor to select a vehicle
 */
public class VehicleSelector extends GridMenuItem<VehicleDef> {
    private Assets mAssets;

    public VehicleSelector(Menu menu) {
        super(menu);
    }

    private class Renderer extends TextureRegionItemRendererAdapter<VehicleDef> {
        private final TextureRegionItemRendererAdapter mRenderer = new TextureRegionItemRenderer();

        @Override
        protected TextureRegion getItemRegion(VehicleDef vehicleDef) {
            return mAssets.findRegion("vehicles/" + vehicleDef.mainImage);
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
}
