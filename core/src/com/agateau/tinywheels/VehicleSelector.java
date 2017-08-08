package com.agateau.tinywheels;

import com.agateau.ui.GridMenuItem;
import com.agateau.ui.Menu;
import com.agateau.ui.TextureRegionItemRendererAdapter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A menu item to select a vehicle
 */
public class VehicleSelector extends GridMenuItem<VehicleDef> {
    private Assets mAssets;

    private class Renderer extends TextureRegionItemRendererAdapter<VehicleDef> {
        @Override
        protected TextureRegion getItemRegion(VehicleDef vehicleDef) {
            return mAssets.findRegion("vehicles/" + vehicleDef.mainImage);
        }
    }

    public VehicleSelector(Menu menu) {
        super(menu);
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
