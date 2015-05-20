package com.greenyetilab.tinywheels;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.greenyetilab.utils.FileUtils;
import com.greenyetilab.utils.RefreshHelper;
import com.greenyetilab.utils.UiBuilder;
import com.greenyetilab.utils.anchor.AnchorGroup;

/**
 * Select your vehicle
 */
public class SelectVehicleScreen extends com.greenyetilab.utils.StageScreen {
    private final TheGame mGame;
    private final ButtonGroup mVehicleButtonGroup = new ButtonGroup();

    private class VehicleButton extends TextButton {
        private final VehicleDef mVehicleDef;

        public VehicleButton(VehicleDef vehicleDef) {
            super(vehicleDef.name, mGame.getAssets().skin);
            mVehicleDef = vehicleDef;
        }
    }

    public SelectVehicleScreen(TheGame game) {
        mGame = game;
        setupUi();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                mGame.showSelectVehicle();
            }
        };
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().skin);

        AnchorGroup root = (AnchorGroup)builder.build(FileUtils.assets("screens/selectvehicle.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        HorizontalGroup group = builder.getActor("vehicleGroup");
        createVehicleButtons(group);
        builder.getActor("goButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startRace();
            }
        });
        builder.getActor("backButton").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGame.showMainMenu();
            }
        });
    }

    private void createVehicleButtons(HorizontalGroup group) {
        group.space(12);
        for(VehicleDef vehicleDef : mGame.getAssets().vehicleDefs) {
            VehicleButton button = new VehicleButton(vehicleDef);
            group.addActor(button);
            mVehicleButtonGroup.add(button);
        }
        Array<Button> buttons = mVehicleButtonGroup.getButtons();
        buttons.get(0).setChecked(true);
        group.layout();
        group.setWidth(buttons.get(buttons.size - 1).getRight());
        group.setHeight(buttons.get(0).getHeight());
    }

    private void startRace() {
        VehicleButton button = (VehicleButton)mVehicleButtonGroup.getChecked();
        mGame.start(button.mVehicleDef.id);
    }
}
