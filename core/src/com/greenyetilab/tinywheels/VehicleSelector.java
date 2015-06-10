package com.greenyetilab.tinywheels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.greenyetilab.utils.UiBuilder;

/**
 * An actor to select a vehicle
 */
public class VehicleSelector extends VerticalGroup {
    private final ButtonGroup mVehicleButtonGroup = new ButtonGroup();
    private Assets mAssets;

    private class VehicleButton extends TextButton {
        private final VehicleDef mVehicleDef;

        public VehicleButton(VehicleDef vehicleDef) {
            super(vehicleDef.name, mAssets.skin);
            mVehicleDef = vehicleDef;
        }
    }

    public VehicleSelector() {
    }

    public void init(Assets assets) {
        mAssets = assets;
        createVehicleButtons();
    }

    public String getSelectedId() {
        VehicleButton button = (VehicleButton)mVehicleButtonGroup.getChecked();
        return button.mVehicleDef.id;
    }

    public static void register(UiBuilder builder) {
        builder.registerActorFactory("VehicleSelector", new UiBuilder.ActorFactory() {
            @Override
            public Actor createActor(XmlReader.Element element) {
                return new VehicleSelector();
            }
        });
    }

    private void createVehicleButtons() {
        space(12);
        for(VehicleDef vehicleDef : mAssets.vehicleDefs) {
            VehicleButton button = new VehicleButton(vehicleDef);
            addActor(button);
            mVehicleButtonGroup.add(button);
        }
        Array<Button> buttons = mVehicleButtonGroup.getButtons();
        buttons.get(0).setChecked(true);
        layout();
        int count = buttons.size;
        setWidth(buttons.get(0).getWidth());
        setHeight(buttons.get(0).getHeight() * count + 12 * (count - 1));
    }
}
